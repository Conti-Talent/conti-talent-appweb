/* =========================================================
   page-admin-postulantes.js — Gestión completa de postulantes
   - Filtra, edita, cambia estado, ajusta puntaje
   - Eliminación lógica (rechazo) y eliminación física
   ========================================================= */

(() => {
  const init = () => {
    if (!Auth.requireAdmin('/login')) return;
    populateFilters();
    document.getElementById('search-postulante').addEventListener('input', renderTable);
    document.getElementById('filter-oferta-postulante').addEventListener('change', renderTable);
    document.getElementById('filter-estado-postulante').addEventListener('change', renderTable);
    const newBtn = document.getElementById('btn-nuevo-postulante');
    if (newBtn) newBtn.addEventListener('click', openCreate);
    renderTable();
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
            UI.el('div', { style: 'font-weight:500', text: `${p.puntaje} pts` }),
            UI.el('div', { class: 'progress' }, [UI.el('div', { class: 'progress__bar', style: `width:${Math.min(100, p.puntaje)}%` })])
          ])
        ]),
        UI.el('td', { text: UI.formatDate(p.creadoEn) }),
        UI.el('td', {}, [
          UI.el('div', { class: 'row-actions' }, [
            UI.el('button', { class: 'btn btn--sm btn--ghost',  text: 'Ver',     onClick: () => openDetail(p) }),
            UI.el('button', { class: 'btn btn--sm btn--ghost',  text: 'Editar',  onClick: () => openEdit(p) }),
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

    const content = UI.el('div', { class: 'applicant-grid' }, [
      UI.el('aside', { class: 'applicant-profile' }, [
        UI.el('div', { class: 'applicant-profile__avatar', text: initials }),
        UI.el('h3', { text: p.nombre }),
        UI.el('p',  { class: 'muted', text: p.email }),
        UI.el('p',  { class: 'soft',  text: p.telefono }),
        UI.el('div', { style: 'margin-top:16px' }, [UI.renderEstadoBadge(p.estado)]),
        UI.el('div', { style: 'margin-top:16px' }, [
          UI.el('div', { class: 'kpi__label', text: 'Puntaje' }),
          UI.el('div', { class: 'kpi__value', text: p.puntaje })
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
          UI.el('p',  { text: p.experiencia || '—' })
        ]),
        UI.el('div', { class: 'card' }, [
          UI.el('h4', { text: 'Habilidades' }),
          UI.el('p',  { text: p.habilidades || '—' })
        ])
      ])
    ]);

    UI.openModal({ title: 'Detalle del postulante', content });
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
      UI.el('div', { class: 'field' }, [UI.el('label', { text: 'Habilidades' }), UI.el('textarea', { class: 'textarea', name: 'habilidades', text: p.habilidades })])
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
      UI.showToast('Postulante actualizado', 'success');
      m.close();
      renderTable();
    });
  };

  const onSoftDelete = async (p) => {
    const ok = await UI.confirm({ title: 'Rechazar postulante', message: `¿Marcar a ${p.nombre} como rechazado? (eliminación lógica)`, okLabel: 'Rechazar' });
    if (!ok) return;
    Postulantes.softDelete(p.id);
    UI.showToast('Postulante rechazado', 'info');
    renderTable();
  };

  document.addEventListener('DOMContentLoaded', init);
})();
