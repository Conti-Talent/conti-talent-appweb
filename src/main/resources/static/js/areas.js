/* =========================================================
   areas.js — CRUD de áreas laborales
   ========================================================= */

const Areas = (() => {
  const ENTITY = 'areas';

  const list   = ()      => Storage.read(ENTITY, []);
  const get    = (id)    => list().find((a) => a.id === id) || null;

  const create = (data) => {
    const items = list();
    const newItem = {
      id: Storage.generateId(),
      nombre: data.nombre.trim(),
      descripcion: (data.descripcion || '').trim(),
      icono: data.icono || '🏢',
      color: data.color || '#6366f1'
    };
    items.push(newItem);
    Storage.write(ENTITY, items);
    return newItem;
  };

  const update = (id, data) => {
    const items = list().map((a) => a.id === id ? { ...a, ...data } : a);
    Storage.write(ENTITY, items);
  };

  const remove = (id) => {
    Storage.write(ENTITY, list().filter((a) => a.id !== id));
  };

  const countOfertasByArea = (areaId) => {
    const ofertas = Storage.read('ofertas', []);
    return ofertas.filter((o) => o.areaId === areaId).length;
  };

  return { list, get, create, update, remove, countOfertasByArea };
})();

window.Areas = Areas;
