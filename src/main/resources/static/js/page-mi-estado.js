/* =========================================================
   page-mi-estado.js — Estado de postulaciones del usuario
   ========================================================= */

(() => {
  const init = async () => {
    await Storage.ready;
    if (!Auth.requireAuth()) return;
    const session = Auth.getSession();
    const wrap    = document.getElementById('estado-list');
    if (!wrap) return;

    const items = Postulantes.byUsuario(session.id);
    UI.clear(wrap);

    if (items.length === 0) {
      wrap.appendChild(UI.el('div', { class: 'empty-state' }, [
        UI.el('div', { class: 'empty-state__icon', text: '📭' }),
        UI.el('h3',  { text: 'Aún no tienes postulaciones' }),
        UI.el('p',   { class: 'muted', text: 'Explora ofertas que se ajusten a tu perfil y postula en un click.' }),
        UI.el('a',   { href: 'ofertas.html', class: 'btn btn--primary', style: 'margin-top:12px', text: 'Ver ofertas' })
      ]));
      return;
    }

    items.forEach((p) => {
      const oferta    = Ofertas.get(p.ofertaId);
      const yaRindio  = Postulantes.hasRespuestas(p);
      const ctaButton = yaRindio
        ? UI.el('a', { href: `mis-respuestas.html?postulante=${p.id}`, class: 'btn btn--ghost', text: 'Ver mis respuestas' })
        : UI.el('a', { href: `evaluacion.html?postulante=${p.id}`,    class: 'btn btn--primary', text: 'Realizar evaluación' });

      const card = UI.el('article', { class: 'card' }, [
        UI.el('header', { class: 'flex between gap-3' }, [
          UI.el('div', {}, [
            UI.el('h3', { class: 'card__title', text: oferta?.titulo || '—' }),
            UI.el('p',  { class: 'muted', text: `Postulado el ${UI.formatDate(p.creadoEn)}` })
          ]),
          UI.renderEstadoBadge(p.estado)
        ]),
        renderStepper(p.estado),
        UI.el('div', { class: 'flex between gap-3', style: 'margin-top: 16px;' }, [
          UI.el('div', {}, [
            UI.el('div', { class: 'muted', text: 'Tu puntaje' }),
            UI.el('div', { class: 'kpi__value', style: 'font-size: 1.6rem;', text: `${p.puntaje} pts` })
          ]),
          ctaButton
        ])
      ]);
      wrap.appendChild(card);
    });
  };

  const ORDER = ['POSTULADO', 'EN_EVALUACION', 'APROBADO_TECNICO', 'ENTREVISTA', 'EVALUACION_PSICOLOGICA', 'ACEPTADO'];

  const renderStepper = (estado) => {
    const idx = ORDER.indexOf(estado);
    const finalRechazado = estado === 'RECHAZADO';
    const stepper = UI.el('div', { class: 'stepper', style: 'margin-top: 16px;' });
    ORDER.forEach((st, i) => {
      const meta = UI.ESTADOS[st];
      let cls = 'step';
      if (i < idx) cls += ' is-done';
      if (i === idx) cls += ' is-current';
      stepper.appendChild(UI.el('div', { class: cls }, [
        UI.el('span', { class: 'step__num', text: i + 1 }),
        UI.el('span', { text: meta.label })
      ]));
    });
    if (finalRechazado) {
      stepper.appendChild(UI.el('div', { class: 'step', style: 'background: rgba(239,68,68,0.15); color: #fca5a5; border-color: rgba(239,68,68,0.3);' }, [
        UI.el('span', { text: 'Rechazado' })
      ]));
    }
    return stepper;
  };

  document.addEventListener('DOMContentLoaded', init);
})();
