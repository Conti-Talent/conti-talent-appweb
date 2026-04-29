/* =========================================================
   postulantes.js — Gestión de postulantes
   ========================================================= */

const Postulantes = (() => {
  const ENTITY = 'postulantes';

  const list   = ()      => Storage.read(ENTITY, []);
  const get    = (id)    => list().find((p) => p.id === id) || null;
  const byOferta = (ofertaId) => list().filter((p) => p.ofertaId === ofertaId);
  const byUsuario = (usuarioId) => list().filter((p) => p.usuarioId === usuarioId);

  const create = (data) => {
    const items = list();
    const newItem = {
      id: Storage.generateId(),
      usuarioId: data.usuarioId || null,
      ofertaId: data.ofertaId,
      nombre: data.nombre.trim(),
      email: data.email.trim(),
      telefono: data.telefono || '',
      experiencia: data.experiencia || '',
      habilidades: data.habilidades || '',
      cv: data.cv || '',
      estado: 'POSTULADO',
      puntaje: 0,
      creadoEn: Date.now()
    };
    items.push(newItem);
    Storage.write(ENTITY, items);
    return newItem;
  };

  const update = (id, data) => {
    const items = list().map((p) => p.id === id ? { ...p, ...data } : p);
    Storage.write(ENTITY, items);
  };

  const setEstado = (id, estado) => {
    update(id, { estado });
  };

  const setPuntaje = (id, puntaje, autoEstado = true) => {
    const updates = { puntaje };
    if (autoEstado) {
      updates.estado = puntaje >= 70 ? 'APROBADO_TECNICO' : 'EN_EVALUACION';
    }
    update(id, updates);
  };

  /**
   * Guarda el resultado completo de una evaluación: puntaje + respuestas marcadas.
   * Esto permite revisar después qué respondió el postulante (vista solo lectura).
   */
  const saveEvaluation = (id, puntaje, respuestas) => {
    update(id, {
      puntaje,
      respuestas,
      estado: puntaje >= 70 ? 'APROBADO_TECNICO' : 'EN_EVALUACION'
    });
  };

  const hasRespuestas = (postulante) =>
    postulante && postulante.respuestas && Object.keys(postulante.respuestas).length > 0;

  const remove = (id) => {
    Storage.write(ENTITY, list().filter((p) => p.id !== id));
  };

  // Eliminación lógica → marca como rechazado
  const softDelete = (id) => setEstado(id, 'RECHAZADO');

  /** Ranking ordenado por puntaje desc */
  const ranking = (ofertaId = null) => {
    const data = ofertaId ? byOferta(ofertaId) : list();
    return [...data].sort((a, b) => b.puntaje - a.puntaje);
  };

  return { list, get, byOferta, byUsuario, create, update, setEstado, setPuntaje, saveEvaluation, hasRespuestas, remove, softDelete, ranking };
})();

window.Postulantes = Postulantes;
