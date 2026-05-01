/* =========================================================
   page-postular.js — Formulario de postulación a oferta
   ========================================================= */

(() => {
  const params = new URLSearchParams(window.location.search);
  const ofertaId = params.get('oferta');

  const init = () => {
    if (!Auth.requireAuth('/login?next=' + encodeURIComponent(window.location.href))) return;

    const oferta = Ofertas.get(ofertaId);
    if (!oferta) {
      window.location.href = '/ofertas';
      return;
    }

    UI.$('#postular-title').textContent = `Postular a: ${oferta.titulo}`;
    UI.$('#postular-area').textContent  = Areas.get(oferta.areaId)?.nombre || '—';

    const session = Auth.getSession();
    if (session) {
      UI.$('input[name=nombre]').value = `${session.nombre} ${session.apellido}`;
      UI.$('input[name=email]').value  = session.email;
    }

    setupFileDrop();

    const form = document.getElementById('postular-form');
    form.addEventListener('submit', (e) => onSubmit(e, oferta));
  };

  const setupFileDrop = () => {
    const drop  = document.getElementById('cv-drop');
    const input = document.getElementById('cv-input');
    const label = document.getElementById('cv-label');

    drop.addEventListener('click', () => input.click());
    input.addEventListener('change', () => {
      if (input.files[0]) {
        label.textContent = input.files[0].name;
        drop.classList.add('is-active');
      }
    });
    drop.addEventListener('dragover', (e) => { e.preventDefault(); drop.classList.add('is-active'); });
    drop.addEventListener('dragleave', () => drop.classList.remove('is-active'));
    drop.addEventListener('drop', (e) => {
      e.preventDefault();
      if (e.dataTransfer.files[0]) {
        input.files = e.dataTransfer.files;
        label.textContent = e.dataTransfer.files[0].name;
      }
    });
  };

  const onSubmit = (e, oferta) => {
    e.preventDefault();
    const form = e.currentTarget;
    const result = Validators.validateForm(form, {
      nombre:      [{ test: Validators.required, message: 'Nombre obligatorio' }],
      email:       [{ test: Validators.required, message: 'Correo obligatorio' },
                    { test: Validators.isEmail,  message: 'Correo no válido' }],
      telefono:    [{ test: Validators.required, message: 'Teléfono obligatorio' },
                    { test: Validators.isPhone,  message: 'Teléfono no válido' }],
      experiencia: [{ test: Validators.required, message: 'Cuéntanos sobre tu experiencia' }],
      habilidades: [{ test: Validators.required, message: 'Indica tus principales habilidades' }]
    });
    if (!result.valid) {
      UI.showToast('Revisa los campos del formulario', 'error');
      return;
    }

    const cvName = document.getElementById('cv-input').files[0]?.name || 'cv.pdf';
    const session = Auth.getSession();

    const postulante = Postulantes.create({
      usuarioId: session?.id || null,
      ofertaId: oferta.id,
      nombre:      result.values.nombre,
      email:       result.values.email,
      telefono:    result.values.telefono,
      experiencia: result.values.experiencia,
      habilidades: result.values.habilidades,
      cv: cvName
    });

    UI.showToast('Postulación registrada con éxito', 'success');
    setTimeout(() => {
      window.location.href = `/evaluacion?postulante=${postulante.id}`;
    }, 700);
  };

  document.addEventListener('DOMContentLoaded', init);
})();
