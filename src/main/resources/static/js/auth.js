/* =========================================================
   auth.js — Sesión, login, registro, control de roles
   ========================================================= */

const Auth = (() => {
  const SESSION_KEY = 'session';

  const getSession  = () => Storage.read(SESSION_KEY, null);
  const setSession  = (s) => Storage.write(SESSION_KEY, s);
  const clear       = () => Storage.clear(SESSION_KEY);

  const isAuthenticated = () => !!getSession();
  const isAdmin         = () => getSession()?.rol === 'admin';

  const login = (email, password) => {
    const usuarios = Storage.read('usuarios', []);
    const user = usuarios.find((u) => u.email.toLowerCase() === String(email).toLowerCase().trim() && u.password === password && u.activo);
    if (!user) return { ok: false, error: 'Credenciales inválidas o cuenta desactivada' };
    setSession({ id: user.id, nombre: user.nombre, apellido: user.apellido, email: user.email, rol: user.rol });
    return { ok: true, user };
  };

  const register = ({ nombre, apellido, email, password }) => {
    const usuarios = Storage.read('usuarios', []);
    if (usuarios.some((u) => u.email.toLowerCase() === String(email).toLowerCase().trim())) {
      return { ok: false, error: 'Ya existe una cuenta con este correo' };
    }
    const newUser = {
      id: Storage.generateId(),
      nombre: nombre.trim(),
      apellido: apellido.trim(),
      email: email.trim(),
      password,
      rol: 'postulante',
      activo: true,
      creadoEn: Date.now()
    };
    usuarios.push(newUser);
    Storage.write('usuarios', usuarios);
    setSession({ id: newUser.id, nombre: newUser.nombre, apellido: newUser.apellido, email: newUser.email, rol: newUser.rol });
    return { ok: true, user: newUser };
  };

  const logout = () => clear();

  const requireAuth = (redirect = 'login.html') => {
    if (!isAuthenticated()) {
      window.location.href = redirect;
      return false;
    }
    return true;
  };

  const requireAdmin = (redirect = 'login.html') => {
    if (!isAdmin()) {
      window.location.href = redirect;
      return false;
    }
    return true;
  };

  return { getSession, isAuthenticated, isAdmin, login, register, logout, requireAuth, requireAdmin };
})();

window.Auth = Auth;
