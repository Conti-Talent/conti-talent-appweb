/* =========================================================
   evaluacion.js — Cuestionarios y calificación automática
   ========================================================= */

const Evaluacion = (() => {
  const ENTITY = 'preguntas';

  const list           = ()         => Storage.read(ENTITY, []);
  const byOferta       = (ofertaId) => list().filter((q) => q.ofertaId === ofertaId);
  const get            = (id)       => list().find((q) => q.id === id) || null;

  const create = (data) => {
    const items = list();
    const newItem = {
      id: Storage.generateId(),
      ofertaId: data.ofertaId,
      pregunta: data.pregunta.trim(),
      opciones: data.opciones,
      correcta: parseInt(data.correcta, 10)
    };
    items.push(newItem);
    Storage.write(ENTITY, items);
    return newItem;
  };

  const update = (id, data) => {
    const items = list().map((q) => q.id === id ? { ...q, ...data } : q);
    Storage.write(ENTITY, items);
  };

  const remove = (id) => Storage.write(ENTITY, list().filter((q) => q.id !== id));

  /**
   * Calcula puntaje sobre 100 dadas las respuestas del postulante.
   * @param {string} ofertaId
   * @param {Record<string, number>} respuestas  preguntaId -> índice elegido
   */
  const calificar = (ofertaId, respuestas) => {
    const preguntas = byOferta(ofertaId);
    if (preguntas.length === 0) return { puntaje: 0, correctas: 0, total: 0 };
    const correctas = preguntas.reduce((acc, q) => acc + (respuestas[q.id] === q.correcta ? 1 : 0), 0);
    const puntaje = Math.round((correctas / preguntas.length) * 100);
    return { puntaje, correctas, total: preguntas.length };
  };

  return { list, byOferta, get, create, update, remove, calificar };
})();

window.Evaluacion = Evaluacion;
