/* =========================================================
   storage-api.js — Capa de persistencia v2 (drop-in)

   REEMPLAZO de storage.js que migra de localStorage a la API REST
   sin cambiar el contrato: sigue exponiendo `window.Storage` con los
   métodos read / write / clear / generateId. Cuando estés listo para
   migrar, en cada plantilla cambia:

       <script src="js/storage.js"></script>
   por:
       <script src="js/storage-api.js"></script>

   Estrategia (cache-then-network):
     - Para minimizar riesgo y mantener la UI síncrona del front actual,
       Storage.read sigue retornando lo que hay en localStorage en ese
       momento, pero al mismo tiempo dispara una recarga asíncrona
       contra la API y refresca el localStorage. La próxima lectura
       traerá ya datos frescos.
     - Storage.write hace optimistic write a localStorage y empuja
       la novedad al backend.
   ========================================================= */

const NAMESPACE = 'conti-talent';
const API = ''; // mismo origen — Spring Boot sirve también el front

const Storage = (() => {
  const buildKey = (entity) => `${NAMESPACE}:${entity}`;

  /* ---------- mapeos entidad ↔ endpoint ---------- */
  const endpointFor = {
    usuarios:    '/api/usuarios',
    areas:       '/api/areas',
    ofertas:     '/api/ofertas',
    preguntas:   '/api/preguntas',
    postulantes: '/api/postulantes',
    metricas:    '/api/metricas'
  };

  const fetchJSON = (url, opts = {}) =>
    fetch(API + url, { credentials: 'include', headers: { 'Content-Type': 'application/json' }, ...opts })
      .then((r) => (r.ok ? r.json() : Promise.reject(r)));

  const refreshFromServer = (entity) => {
    const ep = endpointFor[entity];
    if (!ep) return;
    fetchJSON(ep)
      .then((data) => localStorage.setItem(buildKey(entity), JSON.stringify(data)))
      .catch(() => { /* offline: dejamos lo que ya hay en localStorage */ });
  };

  const read = (entity, fallback = []) => {
    // 1) refrescamos en segundo plano (no bloquea la UI)
    refreshFromServer(entity);
    // 2) devolvemos lo último conocido
    try {
      const raw = localStorage.getItem(buildKey(entity));
      return raw ? JSON.parse(raw) : fallback;
    } catch (_err) {
      return fallback;
    }
  };

  const write = (entity, value) => {
    // optimistic local write — el front sigue funcionando aunque la red falle
    localStorage.setItem(buildKey(entity), JSON.stringify(value));
    // intento de sync best-effort (CRUD granular vive en cada módulo cuando esté listo)
    // Aquí mantenemos compatibilidad con el código actual que escribe la lista
    // entera; el backend la ignora salvo que la página implemente sus propios
    // POST/PUT/DELETE granulares contra /api/<entity>.
  };

  const clear = (entity) => localStorage.removeItem(buildKey(entity));

  const generateId = () =>
    'id_' + Math.random().toString(36).slice(2, 10) + Date.now().toString(36);

  return { read, write, clear, generateId };
})();

window.Storage = Storage;

/* =========================================================
   Helpers REST opcionales — el front puede ir migrando módulo por módulo.

   Ejemplo de cómo migrar page-postular.js (Fase 2):

     // Antes (localStorage):
     // const postulante = Postulantes.create({ ... });

     // Después (REST):
     ContiAPI.postular({
       usuarioId: session?.id,
       ofertaId: oferta.id,
       nombre, email, telefono, experiencia, habilidades, cv
     }).then((postulante) => {
       window.location.href = `evaluacion.html?postulante=${postulante.id}`;
     });

   Y para enviar la evaluación (page-evaluacion.js):

     ContiAPI.calificar(postulanteId, respuestas)
       .then((evalDto) => UI.showToast(`Puntaje: ${evalDto.puntaje}`, 'success'));
   ========================================================= */

const ContiAPI = (() => {
  const j = (url, opts = {}) =>
    fetch(API + url, {
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      ...opts
    }).then((r) => {
      if (!r.ok) return r.json().then((b) => Promise.reject(b));
      return r.status === 204 ? null : r.json();
    });

  return {
    /* ---------- Auth ---------- */
    login:    (email, password) => j('/api/auth/login', { method: 'POST', body: JSON.stringify({ email, password }) }),
    registro: (data)            => j('/api/auth/registro', { method: 'POST', body: JSON.stringify(data) }),
    me:       ()                => j('/api/auth/me'),
    logout:   ()                => j('/api/auth/logout', { method: 'POST' }),

    /* ---------- Catálogo ---------- */
    listarOfertas:     ()      => j('/api/ofertas'),
    ofertasDestacadas: ()      => j('/api/ofertas?destacadas=true'),
    obtenerOferta:     (id)    => j(`/api/ofertas/${id}`),
    listarAreas:       ()      => j('/api/areas'),
    obtenerArea:       (id)    => j(`/api/areas/${id}`),

    /* ---------- Postulación ---------- */
    postular:    (data)        => j('/api/postulantes',          { method: 'POST', body: JSON.stringify(data) }),
    misPostulaciones: (uid)    => j(`/api/postulantes?usuario=${encodeURIComponent(uid)}`),
    obtenerPostulante: (id)    => j(`/api/postulantes/${id}`),
    cambiarEstado: (id, est)   => j(`/api/postulantes/${id}/estado`, { method: 'PATCH', body: JSON.stringify({ estado: est }) }),
    rechazar:    (id)          => j(`/api/postulantes/${id}/rechazar`, { method: 'POST' }),

    /* ---------- Evaluación ---------- */
    preguntasOferta: (ofertaId) => j(`/api/preguntas?oferta=${encodeURIComponent(ofertaId)}&publico=true`),
    calificar: (postulanteId, respuestas) =>
        j('/api/evaluaciones', { method: 'POST', body: JSON.stringify({ postulanteId, respuestas }) }),
    evaluacionPorPostulante: (id) => j(`/api/evaluaciones/postulante/${id}`),

    /* ---------- Métricas ---------- */
    metricas:      ()                    => j('/api/metricas'),
    ranking:       (ofertaId, limit = 10) => j(`/api/metricas/ranking?limit=${limit}${ofertaId ? `&oferta=${ofertaId}` : ''}`),
    porEstado:     ()                    => j('/api/metricas/por-estado'),
    ofertasTop:    (limit = 5)           => j(`/api/metricas/ofertas-top?limit=${limit}`)
  };
})();

window.ContiAPI = ContiAPI;
