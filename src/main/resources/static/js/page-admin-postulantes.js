/* =========================================================
   page-admin-postulantes.js — Gestión completa de postulantes
   - Filtra, edita, cambia estado, ajusta puntaje
   - Eliminación lógica (rechazo) y eliminación física
   ========================================================= */

(() => {
  const init = async () => {
    await Storage.ready;
    if (!Auth.requireAdmin('../login.html')) return;
    populateFilters();
    document.getElementById('search-postulante').addEventListener('input', renderTable);
    document.getElementById('filter-oferta-postulante').addEventListener('change', renderTable);
    document.getElementById('filter-estado-postulante').addEventListener('change', renderTable);
    const newBtn = document.getElementById('btn-nuevo-postulante');
    if (newBtn) newBtn.addEventListener('click', openCreate);
    renderTable();
    renderPipeline();
  };

  /**
   * Crea un postulante a partir de un usuario existente y una oferta.
   * El postulante después puede entrar con su cuenta a rendir el examen.
   */
  const openCreate = () => {
    const postulantesUsers = Usuarios.list().filter((u) => u.rol === 'postulante' && u.activo);
    if (postulantesUsers.length === 0) {
      UI.showToast('Primero crea un usuario con rol postulante', 'error');
      return;
    }
    if (Ofertas.list().length === 0) {
      UI.showToast('Primero crea al menos una oferta', 'error');
      return;
    }

    const usuarioSel = UI.el('select', { class: 'select', name: 'usuarioId' });
    usuarioSel.appendChild(UI.el('option', { value: '', text: '— Selecciona un usuario —' }));
    postulantesUsers.forEach((u) => {
      usuarioSel.appendChild(UI.el('option', { value: u.id, text: `${u.nombre} ${u.apellido} (${u.email})` }));
    });

    const ofertaSel = UI.el('select', { class: 'select', name: 'ofertaId' });
    ofertaSel.appendChild(UI.el('option', { value: '', text: '— Selecciona una oferta —' }));
    Ofertas.list().forEach((o) => {
      ofertaSel.appendChild(UI.el('option', { value: o.id, text: `${o.titulo} · ${o.tipo || 'Trabajo'}` }));
    });

    const telefonoInput    = UI.el('input',    { class: 'input',    name: 'telefono',    placeholder: '+51 ...' });
    const experienciaInput = UI.el('textarea', { class: 'textarea', name: 'experiencia', placeholder: 'Experiencia profesional o académica' });
    const habilidadesInput = UI.el('textarea', { class: 'textarea', name: 'habilidades', placeholder: 'Habilidades clave' });
    const cvInput          = UI.el('input',    { class: 'input',    name: 'cv',          placeholder: 'archivo_cv.pdf' });

    const form = UI.el('form', { class: 'form-grid' }, [
      UI.el('div', { class: 'field' }, [
        UI.el('label', { text: 'Usuario postulante' }),
        usuarioSel,
        UI.el('span', { class: 'hint', text: 'El postulante usará esta cuenta para iniciar sesión y rendir el examen.' })
      ]),
      UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Oferta a la que postula' }), ofertaSel]),
      UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Teléfono' }), telefonoInput]),
      UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Experiencia' }), experienciaInput]),
      UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Habilidades' }), habilidadesInput]),
      UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Nombre del archivo CV' }), cvInput])
    ]);

    const submit = UI.el('button', { class: 'btn btn--primary', text: 'Crear postulante' });
    const footer = UI.el('footer', { class: 'form-actions' }, [submit]);
    const m = UI.openModal({ title: 'Nuevo postulante', content: form, footer });

    submit.addEventListener('click', (e) => {
      e.preventDefault();
      const usuarioId = usuarioSel.value;
      const ofertaId  = ofertaSel.value;
      if (!usuarioId) { UI.showToast('Selecciona un usuario', 'error'); return; }
      if (!ofertaId)  { UI.showToast('Selecciona una oferta', 'error');  return; }

      const yaExiste = Postulantes.list().some((p) => p.usuarioId === usuarioId && p.ofertaId === ofertaId);
      if (yaExiste) {
        UI.showToast('Este usuario ya postuló a esta oferta', 'error');
        return;
      }

      const usuario = Usuarios.get(usuarioId);
      Postulantes.create({
        usuarioId,
        ofertaId,
        nombre:      `${usuario.nombre} ${usuario.apellido}`,
        email:       usuario.email,
        telefono:    telefonoInput.value.trim(),
        experiencia: experienciaInput.value.trim(),
        habilidades: habilidadesInput.value.trim(),
        cv:          cvInput.value.trim() || `${usuario.email}_cv.pdf`
      });
      UI.showToast('Postulante creado · podrá rendir el examen al iniciar sesión', 'success');
      m.close();
      renderTable();
      renderPipeline();
    });
  };

  const populateFilters = () => {
    const ofertaSel = document.getElementById('filter-oferta-postulante');
    Ofertas.list().forEach((o) => ofertaSel.appendChild(UI.el('option', { value: o.id, text: o.titulo })));

    const estadoSel = document.getElementById('filter-estado-postulante');
    Object.entries(UI.ESTADOS).forEach(([k, v]) => estadoSel.appendChild(UI.el('option', { value: k, text: v.label })));
  };

  const renderTable = () => {
    const tbody  = document.getElementById('postulantes-tbody');
    const search = document.getElementById('search-postulante').value.toLowerCase();
    const oferta = document.getElementById('filter-oferta-postulante').value;
    const estado = document.getElementById('filter-estado-postulante').value;

    let items = Postulantes.list();
    if (oferta) items = items.filter((p) => p.ofertaId === oferta);
    if (estado) items = items.filter((p) => p.estado === estado);
    if (search) items = items.filter((p) => `${p.nombre} ${p.email}`.toLowerCase().includes(search));

    UI.clear(tbody);
    if (items.length === 0) {
      tbody.appendChild(UI.el('tr', {}, [UI.el('td', { colspan: 6, class: 'soft', style: 'text-align:center;padding:32px', text: 'Sin postulantes que coincidan.' })]));
      return;
    }

    items.forEach((p) => {
      const o = Ofertas.get(p.ofertaId);
      const tr = UI.el('tr', {}, [
        UI.el('td', {}, [
          UI.el('div', { class: 'flex gap-2', style: 'align-items:center' }, [
            UI.el('div', { class: 'avatar', text: p.nombre.split(' ').map((n) => n[0]).slice(0, 2).join('') }),
            UI.el('div', {}, [
              UI.el('div', { text: p.nombre, style: 'font-weight:500' }),
              UI.el('div', { class: 'soft', style: 'font-size:0.8rem', text: p.email })
            ])
          ])
        ]),
        UI.el('td', { text: o?.titulo || '—' }),
        UI.el('td', {}, [UI.renderEstadoBadge(p.estado)]),
        UI.el('td', {}, [
          UI.el('div', {}, [
            UI.el('div', { style: 'font-weight:500', text: `${p.puntajeFinal ?? p.puntaje} pts` }),
            UI.el('div', { class: 'soft', style: 'font-size:0.75rem', text: `Tec ${p.puntajeCuestionario ?? p.puntaje} · Exp ${p.puntajeExperiencia ?? 0} · Hab ${p.puntajeHabilidades ?? 0}` }),
            UI.el('div', { class: 'progress' }, [UI.el('div', { class: 'progress__bar', style: `width:${Math.min(100, p.puntajeFinal ?? p.puntaje)}%` })])
          ])
        ]),
        UI.el('td', { text: UI.formatDate(p.creadoEn) }),
        UI.el('td', {}, [
          UI.el('div', { class: 'row-actions' }, [
            UI.el('button', { class: 'btn btn--sm btn--ghost',  text: 'Ver',     onClick: () => openDetailV2(p) }),
            UI.el('button', { class: 'btn btn--sm btn--ghost',  text: 'Editar',  onClick: () => openEdit(p) }),
            UI.el('button', { class: 'btn btn--sm btn--ghost', text: 'Entrevista', onClick: () => openInterview(p) }),
            UI.el('button', { class: 'btn btn--sm btn--ghost', text: 'Psico', onClick: () => openPsych(p) }),
            UI.el('button', { class: 'btn btn--sm btn--danger', text: 'Rechazar', onClick: () => onSoftDelete(p) })
          ])
        ])
      ]);
      tbody.appendChild(tr);
    });
  };

  const openDetail = (p) => {
    const oferta = Ofertas.get(p.ofertaId);
    const area   = oferta ? Areas.get(oferta.areaId) : null;
    const initials = p.nombre.split(' ').map((n) => n[0]).slice(0, 2).join('');
    const docs = p.documentos || [];
    const historial = p.historialEstados || [];

    const content = UI.el('div', { class: 'applicant-grid' }, [
      UI.el('aside', { class: 'applicant-profile' }, [
        UI.el('div', { class: 'applicant-profile__avatar', text: initials }),
        UI.el('h3', { text: p.nombre }),
        UI.el('p',  { class: 'muted', text: p.email }),
        UI.el('p',  { class: 'soft',  text: p.telefono }),
        UI.el('div', { style: 'margin-top:16px' }, [UI.renderEstadoBadge(p.estado)]),
        UI.el('div', { style: 'margin-top:16px' }, [
          UI.el('div', { class: 'kpi__label', text: 'Puntaje final' }),
          UI.el('div', { class: 'kpi__value', text: p.puntajeFinal ?? p.puntaje })
        ]),
        UI.el('a', { href: '#', class: 'btn btn--ghost btn--sm', style: 'margin-top:16px', text: `📎 ${p.cv}` })
      ]),
      UI.el('section', {}, [
        UI.el('div', { class: 'card', style: 'margin-bottom:12px' }, [
          UI.el('h4', { text: 'Aplicación' }),
          UI.el('p',  { class: 'soft', text: 'Postuló a:' }),
          UI.el('p',  { style: 'font-weight:500', text: oferta?.titulo || '—' }),
          UI.el('p',  { class: 'soft', text: area?.nombre || '' })
        ]),
        UI.el('div', { class: 'card', style: 'margin-bottom:12px' }, [
          UI.el('h4', { text: 'Experiencia' }),
          UI.el('p', { class: 'soft', text: `Anios: ${p.aniosExperiencia ?? 0} · Estudios: ${p.nivelEstudios || '-'} · Carrera: ${p.carrera || '-'}` }),
          UI.el('p', { class: 'soft', text: `Cuestionario: ${p.puntajeCuestionario ?? p.puntaje} · Experiencia: ${p.puntajeExperiencia ?? 0} · Habilidades: ${p.puntajeHabilidades ?? 0} · Final: ${p.puntajeFinal ?? p.puntaje}` }),
          UI.el('p',  { text: p.experiencia || '—' })
        ]),
        UI.el('div', { class: 'card' }, [
          UI.el('h4', { text: 'Habilidades' }),
          UI.el('p', { class: 'soft', text: `Disponibilidad: ${p.disponibilidad || '-'} · Modalidad: ${p.modalidadPreferida || '-'} · Pretension: ${p.pretensionSalarial || '-'}` }),
          ...(docs.length ? docs.map((d) => UI.el('p', {}, [UI.el('a', { href: d.urlDescarga, text: `${d.tipoDocumento}: ${d.nombreOriginal}` })])) : []),
          ...(historial.length ? historial.slice(0, 5).map((h) => UI.el('p', { class: 'soft', text: `${UI.formatDate(h.fechaCambio)} · ${h.estadoAnterior || '-'} -> ${h.estadoNuevo}` })) : []),
          UI.el('p',  { text: p.habilidades || '—' })
        ])
      ])
    ]);

    UI.openModal({ title: 'Detalle del postulante', content });
  };

  const openDetailV2 = (p) => {
    const oferta = Ofertas.get(p.ofertaId);
    const area = oferta ? Areas.get(oferta.areaId) : null;
    const initials = p.nombre.split(' ').map((n) => n[0]).slice(0, 2).join('');
    const docs = p.documentos || [];
    const historial = (p.historialEstados || []).slice().sort((a, b) => (a.fechaCambio || 0) - (b.fechaCambio || 0));
    const entrevistas = p.entrevistas || [];
    const psicologicas = p.evaluacionesPsicologicas || [];
    const cvDoc = docs.find((d) => d.tipoDocumento === 'CV');

    const content = UI.el('div', { class: 'admin-applicant-detail' }, [
      UI.el('section', { class: 'admin-applicant-hero' }, [
        UI.el('div', { class: 'applicant-profile__avatar', text: initials }),
        UI.el('div', {}, [
          UI.el('div', { class: 'flex gap-2', style: 'align-items:center;flex-wrap:wrap' }, [
            UI.el('h3', { text: p.nombre, style: 'margin:0' }),
            UI.renderEstadoBadge(p.estado)
          ]),
          UI.el('p', { class: 'muted', text: `${p.email} - ${p.telefono || 'Sin telefono'}` }),
          UI.el('p', { class: 'soft', text: `Postulacion #${p.id} - ${UI.formatDate(p.fechaPostulacion || p.creadoEn)}` })
        ]),
        UI.el('div', { class: 'admin-applicant-actions' }, [
          cvDoc ? UI.el('a', { href: cvDoc.urlDescarga, class: 'btn btn--ghost btn--sm', text: 'Descargar CV' }) : UI.el('span', { class: 'soft', text: 'Sin CV descargable' }),
          UI.el('button', { class: 'btn btn--ghost btn--sm', text: 'Editar estado', onClick: () => openEdit(p) }),
          actionButton('Programar entrevista', puedeProgramarEntrevista(p), motivoEntrevista(p), () => openScheduleInterview(p, 'ENTREVISTA_NORMAL')),
          actionButton('Programar evaluacion psicologica', puedeProgramarPsicologica(p), motivoPsicologica(p), () => openScheduleInterview(p, 'EVALUACION_PSICOLOGICA'))
        ])
      ]),
      UI.el('section', { class: 'admin-score-strip' }, [
        detailScore('Final', p.puntajeFinal ?? p.puntaje),
        detailScore('Tecnico', p.puntajeCuestionario ?? p.puntaje),
        detailScore('Experiencia', p.puntajeExperiencia ?? 0),
        detailScore('Habilidades', p.puntajeHabilidades ?? 0)
      ]),
      UI.el('section', { class: 'admin-detail-grid' }, [
        detailPanel('Oferta postulada', [
          infoRow('Titulo', oferta?.titulo || '-'),
          infoRow('Area', area?.nombre || '-'),
          infoRow('Modalidad', oferta?.modalidad || '-'),
          infoRow('Ubicacion', oferta?.ubicacion || '-'),
          infoRow('Evaluacion tecnica', p.fechaEvaluacion ? UI.formatDate(p.fechaEvaluacion) : 'Pendiente')
        ]),
        detailPanel('Perfil del postulante', [
          infoRow('Anios experiencia', `${p.aniosExperiencia ?? 0}`),
          infoRow('Nivel estudios', p.nivelEstudios || '-'),
          infoRow('Carrera', p.carrera || '-'),
          infoRow('Disponibilidad', p.disponibilidad || '-'),
          infoRow('Modalidad preferida', p.modalidadPreferida || '-'),
          infoRow('Pretension salarial', p.pretensionSalarial || '-'),
          infoLink('LinkedIn', p.linkedin),
          infoLink('Portafolio', p.portafolio)
        ]),
        detailPanel('Experiencia declarada', [UI.el('p', { text: p.experiencia || 'Sin experiencia registrada.' })]),
        detailPanel('Habilidades declaradas', [UI.el('p', { text: p.habilidades || 'Sin habilidades registradas.' })]),
        detailPanel('Documentos', docs.length ? docs.map(renderAdminDocument) : [
          UI.el('p', { class: 'soft', text: 'No hay documentos subidos.' })
        ]),
        detailPanel('Proceso de entrevistas', [
          UI.el('div', { class: 'interview-actions' }, [
            actionButton('Programar entrevista', puedeProgramarEntrevista(p), motivoEntrevista(p), () => openScheduleInterview(p, 'ENTREVISTA_NORMAL')),
            actionButton('Programar evaluacion psicologica', puedeProgramarPsicologica(p), motivoPsicologica(p), () => openScheduleInterview(p, 'EVALUACION_PSICOLOGICA'))
          ]),
          ...(entrevistas.length ? entrevistas.map((item) => renderInterview(item, p)) : [UI.el('p', { class: 'soft', text: 'Sin entrevistas registradas.' })]),
          UI.el('h5', { text: 'Evaluacion psicologica', style: 'margin-top:12px' }),
          ...(psicologicas.length ? psicologicas.map(renderPsych) : [UI.el('p', { class: 'soft', text: 'Sin evaluacion psicologica registrada.' })])
        ]),
        detailPanel('Observaciones', [
          UI.el('p', { class: 'soft', text: 'Interna admin' }),
          UI.el('p', { text: p.observacionAdmin || 'Sin observacion interna.' }),
          UI.el('p', { class: 'soft', text: 'Ultimo mensaje visible para postulante' }),
          UI.el('p', { text: ultimoMensajePostulante(historial) })
        ]),
        detailPanel('Historial del proceso', [renderAdminTimeline(historial, p.estado)])
      ])
    ]);

    UI.openModal({ title: 'Ficha completa del postulante', content });
  };

  const detailScore = (label, value) => UI.el('div', { class: 'admin-score-box' }, [
    UI.el('span', { text: label }),
    UI.el('strong', { text: value ?? 0 })
  ]);

  const detailPanel = (title, children) => UI.el('article', { class: 'admin-detail-panel' }, [
    UI.el('h4', { text: title }),
    ...children
  ]);

  const infoRow = (label, value) => UI.el('p', { class: 'admin-info-row' }, [
    UI.el('span', { text: label }),
    UI.el('strong', { text: value || '-' })
  ]);

  const infoLink = (label, value) => UI.el('p', { class: 'admin-info-row' }, [
    UI.el('span', { text: label }),
    value ? UI.el('a', { href: value, target: '_blank', rel: 'noopener', text: value }) : UI.el('strong', { text: '-' })
  ]);

  const renderAdminDocument = (doc) => UI.el('p', { class: 'admin-document-row' }, [
    UI.el('span', { text: `${doc.tipoDocumento}: ${doc.nombreOriginal}` }),
    UI.el('a', { href: doc.urlDescarga || `/api/documentos/${doc.id}/descargar`, class: 'btn btn--ghost btn--sm', text: 'Descargar' })
  ]);

  const renderInterview = (item, postulante) => {
    const puedeEditar = !['ACEPTADO', 'RECHAZADO'].includes(postulante.estado) && !['CANCELADA'].includes(item.estadoEntrevista);
    const puedeResultado = puedeEditar && ['PROGRAMADA', 'REPROGRAMADA', 'REALIZADA'].includes(item.estadoEntrevista) && item.resultado === 'PENDIENTE';
    const puedeReprogramar = puedeEditar && item.estadoEntrevista === 'PROGRAMADA';
    const puedeCancelar = puedeEditar && ['PROGRAMADA', 'REPROGRAMADA'].includes(item.estadoEntrevista);
    return UI.el('article', { class: 'interview-card' }, [
      UI.el('header', { class: 'interview-card__head' }, [
        UI.el('div', {}, [
          UI.el('strong', { text: labelTipoEntrevista(item.tipoEntrevista) }),
          UI.el('p', { class: 'soft', text: `${UI.formatDate(item.fechaProgramada || item.fechaEntrevista)} - ${item.horaInicio || '--:--'} a ${item.horaFin || '--:--'}` })
        ]),
        UI.el('div', { class: 'badge-row' }, [
          renderInterviewBadge(item.estadoEntrevista),
          renderInterviewBadge(item.resultado)
        ])
      ]),
      UI.el('div', { class: 'interview-meta' }, [
        infoRow('Modalidad', item.modalidad || '-'),
        infoRow(item.modalidad === 'VIRTUAL' ? 'Enlace' : 'Lugar', item.modalidad === 'VIRTUAL' ? item.enlaceVirtual : item.lugar),
        infoRow('Entrevistador', [item.entrevistadorNombre, item.entrevistadorCargo].filter(Boolean).join(' - ') || '-'),
        infoRow('Observacion interna', item.observacionInterna || item.observacion || '-'),
        item.actualizadoEn ? infoRow('Ultima edicion', `${UI.formatDateTime(item.actualizadoEn)} - ${item.actualizadoPorAdmin || '-'}`) : null
      ].filter(Boolean)),
      UI.el('div', { class: 'row-actions interview-card__actions' }, [
        puedeEditar ? UI.el('button', { class: 'btn btn--ghost btn--sm', text: 'Editar entrevista', onClick: () => openEditInterview(postulante, item) }) : null,
        puedeResultado ? UI.el('button', { class: 'btn btn--primary btn--sm', text: 'Registrar resultado', onClick: () => openInterviewResult(postulante, item) }) : null,
        puedeReprogramar ? UI.el('button', { class: 'btn btn--ghost btn--sm', text: 'Reprogramar', onClick: () => openReprogramInterview(postulante, item) }) : null,
        puedeCancelar ? UI.el('button', { class: 'btn btn--danger btn--sm', text: 'Cancelar', onClick: () => cancelInterview(postulante, item) }) : null
      ].filter(Boolean))
    ]);
  };

  const renderPsych = (item) => UI.el('p', { class: 'admin-info-row' }, [
    UI.el('span', { text: UI.formatDate(item.fechaEvaluacion) }),
    UI.el('strong', { text: `${item.resultado || 'Pendiente'} - ${item.observacion || 'Sin observacion'}` })
  ]);

  const renderAdminTimeline = (historial, estadoActual) => {
    const estados = ['POSTULADO', 'EN_EVALUACION', 'APROBADO_TECNICO', 'ENTREVISTA', 'EVALUACION_PSICOLOGICA', 'ACEPTADO'];
    const actualIndex = estados.indexOf(estadoActual);
    return UI.el('div', { class: 'admin-mini-timeline' }, [
      ...estados.map((estado, index) => {
        const evento = historial.find((h) => h.estadoNuevo === estado);
        const done = index < actualIndex;
        const current = index === actualIndex;
        return UI.el('div', { class: `admin-mini-step ${done ? 'is-done' : ''} ${current ? 'is-current' : ''}` }, [
          UI.el('span', { text: done ? '✓' : index + 1 }),
          UI.el('strong', { text: UI.ESTADOS[estado]?.label || estado }),
          UI.el('small', { text: evento ? UI.formatDate(evento.fechaCambio) : 'Pendiente' }),
          evento?.observacionInterna ? UI.el('small', { text: evento.observacionInterna }) : UI.el('small', { text: '' })
        ]);
      }),
      estadoActual === 'RECHAZADO' ? UI.el('div', { class: 'admin-mini-step is-rejected' }, [
        UI.el('span', { text: '!' }),
        UI.el('strong', { text: 'Rechazado' }),
        UI.el('small', { text: historial.find((h) => h.estadoNuevo === 'RECHAZADO')?.observacionInterna || 'Proceso cerrado' })
      ]) : null
    ].filter(Boolean));
  };

  const ultimoMensajePostulante = (historial) => {
    const item = [...historial].reverse().find((h) => h.observacionPostulante);
    return item ? item.observacionPostulante : 'Sin mensaje visible registrado.';
  };

  const openEdit = (p) => {
    const estadoSel = UI.el('select', { class: 'select', name: 'estado' });
    Object.entries(UI.ESTADOS).forEach(([k, v]) => {
      const opt = UI.el('option', { value: k, text: v.label });
      if (k === p.estado) opt.selected = true;
      estadoSel.appendChild(opt);
    });

    const ofertaSel = UI.el('select', { class: 'select', name: 'ofertaId' });
    Ofertas.list().forEach((o) => {
      const opt = UI.el('option', { value: o.id, text: o.titulo });
      if (o.id === p.ofertaId) opt.selected = true;
      ofertaSel.appendChild(opt);
    });

    const form = UI.el('form', { class: 'form-grid' }, [
      UI.el('div', { class: 'form-grid form-grid--2' }, [
        UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Nombre completo' }), UI.el('input', { class: 'input', name: 'nombre', value: p.nombre }), UI.el('div', { class: 'error' })]),
        UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Correo' }),         UI.el('input', { class: 'input', name: 'email',  value: p.email }),  UI.el('div', { class: 'error' })])
      ]),
      UI.el('div', { class: 'form-grid form-grid--2' }, [
        UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Teléfono' }),       UI.el('input', { class: 'input', name: 'telefono', value: p.telefono })]),
        UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Puntaje (0-100)' }),UI.el('input', { class: 'input', type: 'number', min: 0, max: 100, name: 'puntaje', value: p.puntaje })])
      ]),
      UI.el('div', { class: 'form-grid form-grid--2' }, [
        UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Reasignar oferta' }), ofertaSel]),
        UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Estado' }), estadoSel])
      ]),
      UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Experiencia' }), UI.el('textarea', { class: 'textarea', name: 'experiencia', text: p.experiencia })]),
      UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Habilidades' }), UI.el('textarea', { class: 'textarea', name: 'habilidades', text: p.habilidades })]),
      UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Observacion admin' }), UI.el('textarea', { class: 'textarea', name: 'observacionAdmin', text: p.observacionAdmin || '' })]),
      UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Observacion para postulante' }), UI.el('textarea', { class: 'textarea', name: 'observacionPostulante', placeholder: 'Mensaje visible en Mi proceso' })])
    ]);

    const submit = UI.el('button', { class: 'btn btn--primary', text: 'Guardar cambios' });
    const footer = UI.el('footer', { style: 'display:flex;justify-content:flex-end;margin-top:16px;' }, [submit]);
    const m = UI.openModal({ title: 'Editar postulante', content: form, footer });

    submit.addEventListener('click', (e) => {
      e.preventDefault();
      const r = Validators.validateForm(form, {
        nombre: [{ test: Validators.required, message: 'Nombre obligatorio' }],
        email:  [{ test: Validators.required, message: 'Correo obligatorio' },
                 { test: Validators.isEmail,  message: 'Correo no válido' }]
      });
      if (!r.valid) return;
      Postulantes.update(p.id, {
        ...r.values,
        puntaje: parseInt(r.values.puntaje, 10) || 0
      });
      if (r.values.estado && r.values.estado !== p.estado) {
        Postulantes.setEstado(p.id, r.values.estado, {
          usuarioAdmin: Auth.getSession()?.email || 'Admin',
          observacionInterna: r.values.observacionAdmin || '',
          observacionPostulante: r.values.observacionPostulante || ''
        });
      }
      Postulantes.setObservacionAdmin(p.id, r.values.observacionAdmin || '');
      UI.showToast('Postulante actualizado', 'success');
      m.close();
      renderTable();
      renderPipeline();
    });
  };

  const PIPELINE = [
    ['POSTULADO', 'Postulados'],
    ['EN_EVALUACION', 'En evaluacion'],
    ['APROBADO_TECNICO', 'Aprobados tecnicos'],
    ['ENTREVISTA', 'Entrevista'],
    ['EVALUACION_PSICOLOGICA', 'Evaluacion psicologica'],
    ['ACEPTADO', 'Aceptados'],
    ['RECHAZADO', 'Rechazados']
  ];

  const renderPipeline = () => {
    const board = document.getElementById('pipeline-board');
    if (!board) return;
    UI.clear(board);
    PIPELINE.forEach(([estado, label]) => {
      const items = Postulantes.list().filter((p) => p.estado === estado);
      board.appendChild(UI.el('section', { class: 'pipeline-column' }, [
        UI.el('header', { class: 'pipeline-column__head' }, [
          UI.el('strong', { text: label }),
          UI.el('span', { class: 'badge', text: items.length })
        ]),
        UI.el('div', { class: 'pipeline-column__items' }, items.length ? items.map(renderPipelineCard) : [
          UI.el('p', { class: 'soft', text: 'Sin postulantes' })
        ])
      ]));
    });
  };

  const renderPipelineCard = (p) => {
    const oferta = Ofertas.get(p.ofertaId);
    return UI.el('article', { class: 'pipeline-card' }, [
      UI.el('strong', { text: p.nombre }),
      UI.el('span', { class: 'soft', text: oferta?.titulo || '-' }),
      UI.el('span', { class: 'soft', text: `Final ${p.puntajeFinal ?? p.puntaje} pts` }),
      UI.el('div', { class: 'row-actions', style: 'margin-top:8px' }, [
        UI.el('button', { class: 'btn btn--sm btn--ghost', text: 'Ver', onClick: () => openDetailV2(p) }),
        UI.el('button', { class: 'btn btn--sm btn--ghost', text: 'Mover', onClick: () => openEdit(p) })
      ])
    ]);
  };

  const openInterview = (p) => openScheduleInterview(p, 'ENTREVISTA_NORMAL');
  const openPsych = (p) => openScheduleInterview(p, 'EVALUACION_PSICOLOGICA');

  const actionButton = (text, enabled, reason, onClick) =>
    UI.el('button', { class: 'btn btn--ghost btn--sm', text, title: enabled ? '' : reason, disabled: enabled ? null : 'disabled', onClick: enabled ? onClick : null });

  const puedeProgramarEntrevista = (p) => ['APROBADO_TECNICO', 'ENTREVISTA'].includes(p.estado)
    && !tieneActiva(p, 'ENTREVISTA_NORMAL');
  const puedeProgramarPsicologica = (p) => (p.estado === 'EVALUACION_PSICOLOGICA' || tieneAprobada(p, 'ENTREVISTA_NORMAL'))
    && !['ACEPTADO', 'RECHAZADO'].includes(p.estado)
    && !tieneActiva(p, 'EVALUACION_PSICOLOGICA');
  const motivoEntrevista = (p) => ['POSTULADO', 'EN_EVALUACION'].includes(p.estado)
    ? 'Primero debe aprobar la evaluacion tecnica.'
    : ['ACEPTADO', 'RECHAZADO'].includes(p.estado) ? 'El proceso ya finalizo.' : 'Ya existe una entrevista activa.';
  const motivoPsicologica = (p) => ['ACEPTADO', 'RECHAZADO'].includes(p.estado)
    ? 'El proceso ya finalizo.' : 'Primero debe aprobar la entrevista.';
  const tieneActiva = (p, tipo) => (p.entrevistas || []).some((e) => e.tipoEntrevista === tipo && ['PROGRAMADA', 'REPROGRAMADA', 'REALIZADA'].includes(e.estadoEntrevista));
  const tieneAprobada = (p, tipo) => (p.entrevistas || []).some((e) => e.tipoEntrevista === tipo && e.resultado === 'APROBADO');

  const openScheduleInterview = (p, tipo) => openInterviewForm({
    title: tipo === 'ENTREVISTA_NORMAL' ? 'Programar entrevista' : 'Programar evaluacion psicologica',
    tipo,
    submitText: tipo === 'ENTREVISTA_NORMAL' ? 'Programar entrevista' : 'Programar evaluacion psicologica',
    onSave: (payload) => ContiAPI.registrarEntrevista(p.id, payload)
  });

  const openEditInterview = (_p, item) => openInterviewForm({
    title: 'Editar entrevista',
    tipo: item.tipoEntrevista,
    item,
    submitText: 'Guardar cambios',
    includeChangeReason: true,
    onSave: (payload) => ContiAPI.actualizarEntrevista(item.id, payload)
  });

  const openReprogramInterview = (_p, item) => openInterviewForm({
    title: 'Reprogramar entrevista',
    tipo: item.tipoEntrevista,
    item,
    submitText: 'Reprogramar entrevista',
    includeChangeReason: true,
    onSave: (payload) => ContiAPI.reprogramarEntrevista(item.id, payload)
  });

  const openInterviewResult = (_p, item) => {
    const form = UI.el('form', { class: 'form-grid' }, [
      UI.el('div', { class: 'field' }, [
        UI.el('label', { text: 'Resultado' }),
        UI.el('select', { class: 'select', name: 'resultado' }, [
          UI.el('option', { value: 'APROBADO', text: 'Aprobar entrevista' }),
          UI.el('option', { value: 'DESAPROBADO', text: 'Desaprobar entrevista' })
        ])
      ]),
      UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Observacion interna / motivo' }), UI.el('textarea', { class: 'textarea', name: 'observacionInterna' })]),
      UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Observacion visible para postulante' }), UI.el('textarea', { class: 'textarea', name: 'observacionPostulante' })]),
      UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Observacion del cambio' }), UI.el('textarea', { class: 'textarea', name: 'observacionCambio', placeholder: 'Registro de auditoria de la modificacion' })])
    ]);
    const submit = UI.el('button', { class: 'btn btn--primary', text: 'Registrar resultado' });
    const modal = UI.openModal({ title: 'Registrar resultado', content: form, footer: UI.el('footer', { class: 'form-actions' }, [submit]) });
    submit.addEventListener('click', async (e) => {
      e.preventDefault();
      try {
        const values = Object.fromEntries(new FormData(form).entries());
        const saved = await ContiAPI.registrarResultadoEntrevista(item.id, { ...values, usuarioAdmin: Auth.getSession()?.email || 'Admin' });
        Storage.upsert('postulantes', saved);
        await Storage.refresh('postulantes');
        UI.showToast('Resultado registrado', 'success');
        modal.close();
        renderTable();
        renderPipeline();
      } catch (err) {
        UI.showToast(err.message || 'No se pudo registrar el resultado', 'error');
      }
    });
  };

  const cancelInterview = async (_p, item) => {
    const ok = await UI.confirm({ title: 'Cancelar entrevista', message: '¿Cancelar esta entrevista?', okLabel: 'Cancelar entrevista' });
    if (!ok) return;
    try {
      await ContiAPI.cancelarEntrevista(item.id, { usuarioAdmin: Auth.getSession()?.email || 'Admin', observacionCambio: 'Cancelacion desde ficha admin' });
      await Storage.refresh('postulantes');
      UI.showToast('Entrevista cancelada', 'info');
      renderTable();
      renderPipeline();
    } catch (err) {
      UI.showToast(err.message || 'No se pudo cancelar', 'error');
    }
  };

  const openInterviewForm = ({ title, tipo, item = {}, submitText, includeChangeReason = false, onSave }) => {
    const fechaInput = UI.el('input', { class: 'input', type: 'date', name: 'fecha', value: item.fechaProgramada ? new Date(item.fechaProgramada).toISOString().slice(0, 10) : '' });
    const form = UI.el('form', { class: 'form-grid' }, [
      UI.el('div', { class: 'form-grid form-grid--2' }, [
        UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Fecha programada' }), fechaInput]),
        UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Modalidad' }), selectField('modalidad', ['VIRTUAL', 'PRESENCIAL', 'TELEFONICA'], item.modalidad || 'VIRTUAL')])
      ]),
      UI.el('div', { class: 'form-grid form-grid--2' }, [
        UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Hora inicio' }), UI.el('input', { class: 'input', type: 'time', name: 'horaInicio', value: item.horaInicio || '' })]),
        UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Hora fin' }), UI.el('input', { class: 'input', type: 'time', name: 'horaFin', value: item.horaFin || '' })])
      ]),
      UI.el('div', { class: 'form-grid form-grid--2' }, [
        UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Lugar' }), UI.el('input', { class: 'input', name: 'lugar', value: item.lugar || '' })]),
        UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Enlace virtual' }), UI.el('input', { class: 'input', name: 'enlaceVirtual', value: item.enlaceVirtual || '' })])
      ]),
      UI.el('div', { class: 'form-grid form-grid--2' }, [
        UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Entrevistador' }), UI.el('input', { class: 'input', name: 'entrevistadorNombre', value: item.entrevistadorNombre || '' })]),
        UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Cargo' }), UI.el('input', { class: 'input', name: 'entrevistadorCargo', value: item.entrevistadorCargo || '' })])
      ]),
      UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Observacion interna' }), UI.el('textarea', { class: 'textarea', name: 'observacionInterna', text: item.observacionInterna || item.observacion || '' })]),
      UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Observacion visible para postulante' }), UI.el('textarea', { class: 'textarea', name: 'observacionPostulante', text: item.observacionPostulante || '' })]),
      includeChangeReason ? UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Observacion del cambio' }), UI.el('textarea', { class: 'textarea', name: 'observacionCambio', placeholder: 'Motivo de modificacion' })]) : null
    ].filter(Boolean));
    const submit = UI.el('button', { class: 'btn btn--primary', text: submitText });
    const modal = UI.openModal({ title, content: form, footer: UI.el('footer', { class: 'form-actions' }, [submit]) });
    submit.addEventListener('click', async (e) => {
      e.preventDefault();
      const values = Object.fromEntries(new FormData(form).entries());
      if (!values.fecha) { UI.showToast('Selecciona una fecha', 'error'); return; }
      try {
        await onSave({
          ...values,
          tipoEntrevista: tipo,
          fechaProgramada: new Date(`${values.fecha}T00:00:00`).getTime(),
          usuarioAdmin: Auth.getSession()?.email || 'Admin'
        });
        await Storage.refresh('postulantes');
        UI.showToast('Entrevista guardada', 'success');
        modal.close();
        renderTable();
        renderPipeline();
      } catch (err) {
        UI.showToast(err.message || 'No se pudo guardar la entrevista', 'error');
      }
    });
  };

  const selectField = (name, options, selected) => UI.el('select', { class: 'select', name }, options.map((value) =>
    UI.el('option', { value, text: value.replaceAll('_', ' '), selected: value === selected ? 'selected' : null })
  ));

  const labelTipoEntrevista = (tipo) => tipo === 'EVALUACION_PSICOLOGICA' ? 'Evaluacion psicologica' : 'Entrevista normal';
  const renderInterviewBadge = (value) => {
    const normalized = value || 'PENDIENTE';
    const cls = ['APROBADO'].includes(normalized) ? 'badge--aceptado'
      : ['DESAPROBADO', 'CANCELADA'].includes(normalized) ? 'badge--rechazado'
      : ['PROGRAMADA', 'REPROGRAMADA', 'PENDIENTE'].includes(normalized) ? 'badge--psicologica'
      : 'badge--entrevista';
    return UI.el('span', { class: `badge ${cls}`, text: normalized.replaceAll('_', ' ') });
  };

  const onSoftDelete = async (p) => {
    const ok = await UI.confirm({ title: 'Rechazar postulante', message: `¿Marcar a ${p.nombre} como rechazado? (eliminación lógica)`, okLabel: 'Rechazar' });
    if (!ok) return;
    Postulantes.softDelete(p.id);
    UI.showToast('Postulante rechazado', 'info');
    renderTable();
    renderPipeline();
  };

  document.addEventListener('DOMContentLoaded', init);
})();
