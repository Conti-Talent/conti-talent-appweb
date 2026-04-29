/* =========================================================
   storage.js — Capa de persistencia (simulación de backend)
   Encapsula localStorage con namespacing por entidad.
   ========================================================= */

const NAMESPACE = 'conti-talent';

const Storage = (() => {
  const buildKey = (entity) => `${NAMESPACE}:${entity}`;

  const read = (entity, fallback = []) => {
    try {
      const raw = localStorage.getItem(buildKey(entity));
      return raw ? JSON.parse(raw) : fallback;
    } catch (_err) {
      return fallback;
    }
  };

  const write = (entity, value) => {
    localStorage.setItem(buildKey(entity), JSON.stringify(value));
  };

  const clear = (entity) => localStorage.removeItem(buildKey(entity));

  const generateId = () => 'id_' + Math.random().toString(36).slice(2, 10) + Date.now().toString(36);

  return { read, write, clear, generateId };
})();

window.Storage = Storage;
