/* =========================================================
   usuarios.js — CRUD de usuarios (admin)
   ========================================================= */

const Usuarios = (() => {
  const ENTITY = 'usuarios';

  const list   = ()    => Storage.read(ENTITY, []);
  const get    = (id)  => list().find((u) => u.id === id) || null;

  const create = (data) => {
    const items = list();
    if (items.some((u) => u.email.toLowerCase() === data.email.toLowerCase())) {
      throw new Error('Ya existe un usuario con ese correo.');
    }
    const newItem = {
      id: Storage.generateId(),
      nombre: data.nombre.trim(),
      apellido: data.apellido.trim(),
      email: data.email.trim(),
      password: data.password,
      rol: data.rol || 'postulante',
      activo: data.activo !== false,
      creadoEn: Date.now()
    };
    items.push(newItem);
    Storage.write(ENTITY, items);
    return newItem;
  };

  const update = (id, data) => {
    const items = list().map((u) => u.id === id ? { ...u, ...data } : u);
    Storage.write(ENTITY, items);
  };

  const remove = (id) => {
    Storage.write(ENTITY, list().filter((u) => u.id !== id));
  };

  return { list, get, create, update, remove };
})();

window.Usuarios = Usuarios;
