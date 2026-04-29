/* =========================================================
   ofertas.js — CRUD de ofertas laborales
   ========================================================= */

const Ofertas = (() => {
  const ENTITY = 'ofertas';

  const list   = ()       => Storage.read(ENTITY, []);
  const get    = (id)     => list().find((o) => o.id === id) || null;

  const filterByArea = (areaId) => list().filter((o) => o.areaId === areaId);
  const featured     = ()       => list().filter((o) => !!o.destacada);

  const TIPOS = ['Práctica', 'Trabajo'];

  const create = (data) => {
    const items = list();
    const newItem = {
      id: Storage.generateId(),
      titulo: data.titulo.trim(),
      tipo: data.tipo || 'Trabajo',
      areaId: data.areaId,
      modalidad: data.modalidad,
      ubicacion: data.ubicacion || '',
      vacantes: parseInt(data.vacantes, 10) || 1,
      destacada: !!data.destacada,
      descripcion: (data.descripcion || '').trim(),
      requisitos: data.requisitos || [],
      beneficios: data.beneficios || [],
      creadaEn: Date.now()
    };
    items.push(newItem);
    Storage.write(ENTITY, items);
    return newItem;
  };

  const update = (id, data) => {
    const items = list().map((o) => o.id === id ? { ...o, ...data, vacantes: parseInt(data.vacantes ?? o.vacantes, 10) } : o);
    Storage.write(ENTITY, items);
  };

  const remove = (id) => {
    Storage.write(ENTITY, list().filter((o) => o.id !== id));
    Storage.write('preguntas', Storage.read('preguntas', []).filter((q) => q.ofertaId !== id));
  };

  return { list, get, filterByArea, featured, create, update, remove, TIPOS };
})();

window.Ofertas = Ofertas;
