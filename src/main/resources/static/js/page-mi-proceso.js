/* =========================================================
   page-mi-proceso.js - Detalle de proceso por postulacion
   ========================================================= */

(() => {
  const ESTADOS = ['POSTULADO', 'EN_EVALUACION', 'APROBADO_TECNICO', 'ENTREVISTA', 'EVALUACION_PSICOLOGICA', 'ACEPTADO'];

  const NEXT_STEP = {
    POSTULADO: 'Resolver la evaluacion tecnica cuando este disponible.',
    EN_EVALUACION: 'Esperar revision del equipo de seleccion.',
    APROBADO_TECNICO: 'Prepararte para la entrevista.',
    ENTREVISTA: 'Asistir a la entrevista programada.',
    EVALUACION_PSICOLOGICA: 'Completar la evaluacion psicologica.',
    ACEPTADO: 'Revisar la comunicacion final del administrador.',
    RECHAZADO: 'El proceso finalizo para esta oferta.'
  };

  const init = async () => {
    await Storage.ready;
    if (!Auth.requireAuth('login.html?next=' + encodeURIComponent(window.location.href))) return;
    const session = Auth.getSession();
    const wrap = document.getElementById('proceso-list');
    const postulanteId = new URLSearchParams(window.location.search).get('postulante');

    if (!postulanteId) {
      window.location.href = 'mis-postulaciones.html';
      return;
    }

    const postulante = Postulantes.get(postulanteId);
    UI.clear(wrap);
    if (!postulante || String(postulante.usuarioId) !== String(session.id)) {
      wrap.appendChild(UI.el('div', { class: 'empty-state' }, [
        UI.el('div', { class: 'empty-state__icon', text: '📄' }),
        UI.el('h3', { text: 'Proceso no disponible' }),
        UI.el('p', { class: 'muted', text: 'Selecciona una postulacion valida para ver su proceso.' }),
        UI.el('a', { href: 'mis-postulaciones.html', class: 'btn btn--primary', style: 'margin-top:12px', text: 'Mis postulaciones' })
      ]));
      return;
    }

    wrap.appendChild(renderProceso(postulante));
  };

  const renderProceso = (p) => {
    const oferta = Ofertas.get(p.ofertaId);
    const area = oferta ? Areas.get(oferta.areaId) : null;
    const documentos = p.documentos || [];
    const historial = (p.historialEstados || []).slice().reverse();
    const fechaPostulacion = p.fechaPostulacion || p.creadoEn;

    return UI.el('article', { class: 'proceso-card anim-fade-up' }, [
      UI.el('header', { class: 'proceso-head' }, [
        UI.el('div', {}, [
          UI.el('h3', { text: oferta?.titulo || 'Oferta no disponible' }),
          UI.el('p', { class: 'muted', text: `${area?.nombre || 'Area no definida'} · ${oferta?.modalidad || '-'} · ${oferta?.ubicacion || '-'}` }),
          UI.el('p', { class: 'soft', text: `Postulado el ${UI.formatDate(fechaPostulacion)} · Evaluacion: ${p.fechaEvaluacion ? UI.formatDate(p.fechaEvaluacion) : 'Pendiente'}` })
        ]),
        UI.renderEstadoBadge(p.estado)
      ]),
      renderTimeline(p.estado, historial),
      UI.el('div', { class: 'score-grid' }, [
        scoreBox('Tecnico', p.puntajeCuestionario ?? p.puntaje ?? 0),
        scoreBox('Experiencia', p.puntajeExperiencia ?? 0),
        scoreBox('Habilidades', p.puntajeHabilidades ?? 0),
        scoreBox('Final', p.puntajeFinal ?? p.puntaje ?? 0)
      ]),
      UI.el('div', { class: 'proceso-two-col' }, [
        UI.el('section', { class: 'proceso-panel' }, [
          UI.el('h4', { text: 'Documentos' }),
          ...(documentos.length ? documentos.map(renderDocumento) : [UI.el('p', { class: 'soft', text: 'Aun no hay documentos registrados.' })])
        ]),
        UI.el('section', { class: 'proceso-panel' }, [
          UI.el('h4', { text: 'Observaciones y proximo paso' }),
          UI.el('p', { text: p.observacionAdmin || 'Sin observaciones por ahora.' }),
          UI.el('p', { class: 'muted', text: NEXT_STEP[p.estado] || 'Esperar comunicacion del administrador.' })
        ])
      ]),
      UI.el('footer', { style: 'margin-top:16px' }, [
        UI.el('a', { href: 'mis-postulaciones.html', class: 'btn btn--ghost', text: 'Volver a mis postulaciones' })
      ])
    ]);
  };

  const renderTimeline = (estadoActual, historial) => {
    const rechazado = estadoActual === 'RECHAZADO';
    const indexActual = ESTADOS.indexOf(estadoActual);
    return UI.el('div', { class: 'process-timeline' }, [
      ...ESTADOS.map((estado, index) => {
        const meta = UI.ESTADOS[estado] || { label: estado };
        const realizado = !rechazado && index < indexActual;
        const actual = !rechazado && index === indexActual;
        const futuro = !rechazado && index > indexActual;
        const evento = historial.find((h) => h.estadoNuevo === estado);
        return UI.el('div', { class: `process-step ${realizado ? 'is-done' : ''} ${actual ? 'is-current' : ''} ${futuro ? 'is-locked' : ''}` }, [
          UI.el('span', { class: 'process-step__dot', text: realizado ? '✓' : (futuro ? '•' : index + 1) }),
          UI.el('strong', { text: meta.label }),
          UI.el('small', { text: evento ? UI.formatDate(evento.fechaCambio) : 'Pendiente' }),
          evento?.observacionPostulante ? UI.el('small', { text: evento.observacionPostulante }) : UI.el('small', { text: '' })
        ]);
      }),
      rechazado ? UI.el('div', { class: 'process-step is-rejected' }, [
        UI.el('span', { class: 'process-step__dot', text: '!' }),
        UI.el('strong', { text: 'Rechazado' }),
        UI.el('small', { text: historial.find((h) => h.estadoNuevo === 'RECHAZADO')?.observacionPostulante || 'Proceso finalizado' })
      ]) : null
    ].filter(Boolean));
  };

  const scoreBox = (label, value) => UI.el('div', { class: 'score-box' }, [
    UI.el('span', { text: label }),
    UI.el('strong', { text: `${value}` })
  ]);

  const renderDocumento = (doc) => UI.el('p', { class: 'document-row' }, [
    UI.el('span', { text: `${doc.tipoDocumento}: ${doc.nombreOriginal}` }),
    UI.el('a', { href: doc.urlDescarga || `/api/documentos/${doc.id}/descargar`, class: 'btn btn--ghost btn--sm', text: 'Descargar' })
  ]);

  document.addEventListener('DOMContentLoaded', init);
})();
