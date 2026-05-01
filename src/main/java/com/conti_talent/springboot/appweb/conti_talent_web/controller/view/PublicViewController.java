package com.conti_talent.springboot.appweb.conti_talent_web.controller.view;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * MVC controller: vistas publicas servidas por Thymeleaf.
 * Solo retorna nombres de plantilla; la logica vive en services y /api/*.
 */
@Controller
public class PublicViewController {

    @GetMapping({"/", "/index"})
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @GetMapping("/ofertas")
    public String ofertas() {
        return "ofertas";
    }

    @GetMapping("/detalle-oferta")
    public String detalleOferta() {
        return "detalle-oferta";
    }

    @GetMapping("/postular")
    public String postular() {
        return "postular";
    }

    @GetMapping("/evaluacion")
    public String evaluacion() {
        return "evaluacion";
    }

    @GetMapping("/areas")
    public String areas() {
        return "areas";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "contacto";
    }

    @GetMapping("/publicidad")
    public String publicidad() {
        return "publicidad";
    }

    @GetMapping("/mi-estado")
    public String miEstado() {
        return "mi-estado";
    }

    @GetMapping("/mis-respuestas")
    public String misRespuestas() {
        return "mis-respuestas";
    }

    @GetMapping({
            "/index.html", "/login.html", "/registro.html", "/ofertas.html",
            "/detalle-oferta.html", "/postular.html", "/evaluacion.html",
            "/areas.html", "/contacto.html", "/publicidad.html",
            "/mi-estado.html", "/mis-respuestas.html"
    })
    public RedirectView redirectLegacyHtml(HttpServletRequest request) {
        String cleanPath = request.getRequestURI().replaceFirst("\\.html$", "");
        return permanentRedirect(cleanPath, request.getQueryString());
    }

    private RedirectView permanentRedirect(String path, String queryString) {
        String target = queryString == null || queryString.isBlank() ? path : path + "?" + queryString;
        RedirectView redirectView = new RedirectView(target);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        redirectView.setContextRelative(true);
        return redirectView;
    }
}
