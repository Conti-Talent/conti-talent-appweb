package com.conti_talent.springboot.appweb.conti_talent_web.controller.view;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Usuario;
import com.conti_talent.springboot.appweb.conti_talent_web.service.AreaService;
import com.conti_talent.springboot.appweb.conti_talent_web.service.OfertaService;
import com.conti_talent.springboot.appweb.conti_talent_web.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class PublicViewController {

    private final AreaService areaService;
    private final OfertaService ofertaService;
    private final UsuarioService usuarioService;

    public PublicViewController(AreaService areaService, OfertaService ofertaService, UsuarioService usuarioService) {
        this.areaService = areaService;
        this.ofertaService = ofertaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("areas", areaService.listar());
        model.addAttribute("ofertas", ofertaService.listar());
        return "index";
    }

    @GetMapping("/areas")
    public String areas(Model model) {
        model.addAttribute("areas", areaService.listar());
        return "areas";
    }

    @GetMapping("/ofertas")
    public String ofertas(Model model) {
        model.addAttribute("ofertas", ofertaService.listar());
        return "ofertas";
    }

    @GetMapping("/contacto")
    public String contacto() { return "contacto"; }

    @GetMapping("/publicidad")
    public String publicidad() { return "publicidad"; }

    @GetMapping("/login")
    public String loginForm() { return "login"; }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session, Model model) {
        Optional<Usuario> usuario = usuarioService.login(email, password);
        if (usuario.isPresent()) {
            session.setAttribute("usuario", usuario.get());
            return "admin".equals(usuario.get().getRol()) ? "redirect:/admin" : "redirect:/";
        }
        model.addAttribute("error", "Credenciales invalidas o cuenta desactivada");
        return "login";
    }

    @GetMapping("/registro")
    public String registroForm() { return "registro"; }

    @PostMapping("/registro")
    public String registro(@RequestParam String nombre,
                           @RequestParam String apellido,
                           @RequestParam String email,
                           @RequestParam String password,
                           HttpSession session) {
        Usuario u = new Usuario();
        u.setNombre(nombre); u.setApellido(apellido);
        u.setEmail(email); u.setPassword(password);
        session.setAttribute("usuario", usuarioService.registrar(u));
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/mi-estado")
    public String miEstado(HttpSession session, Model model) {
        model.addAttribute("usuario", session.getAttribute("usuario"));
        return "mi-estado";
    }
}
