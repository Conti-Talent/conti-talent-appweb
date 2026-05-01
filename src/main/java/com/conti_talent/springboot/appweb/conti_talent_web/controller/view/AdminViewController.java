package com.conti_talent.springboot.appweb.conti_talent_web.controller.view;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Vistas del panel de administracion en templates/admin/.
 */
@Controller
@RequestMapping("/admin")
public class AdminViewController {

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/areas")
    public String areas() {
        return "admin/admin-areas";
    }

    @GetMapping("/ofertas")
    public String ofertas() {
        return "admin/admin-ofertas";
    }

    @GetMapping("/postulantes")
    public String postulantes() {
        return "admin/admin-postulantes";
    }

    @GetMapping("/preguntas")
    public String preguntas() {
        return "admin/admin-preguntas";
    }

    @GetMapping("/usuarios")
    public String usuarios() {
        return "admin/admin-usuarios";
    }

    @GetMapping("/metricas")
    public String metricas() {
        return "admin/admin-metricas";
    }

    @GetMapping({
            "/dashboard.html", "/admin-areas.html", "/admin-ofertas.html",
            "/admin-postulantes.html", "/admin-preguntas.html",
            "/admin-usuarios.html", "/admin-metricas.html"
    })
    public RedirectView redirectLegacyHtml(HttpServletRequest request) {
        String path = request.getRequestURI();
        String cleanPath = path
                .replace("/dashboard.html", "/dashboard")
                .replace("/admin-areas.html", "/areas")
                .replace("/admin-ofertas.html", "/ofertas")
                .replace("/admin-postulantes.html", "/postulantes")
                .replace("/admin-preguntas.html", "/preguntas")
                .replace("/admin-usuarios.html", "/usuarios")
                .replace("/admin-metricas.html", "/metricas");
        String target = request.getQueryString() == null || request.getQueryString().isBlank()
                ? cleanPath
                : cleanPath + "?" + request.getQueryString();
        RedirectView redirectView = new RedirectView(target);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        redirectView.setContextRelative(true);
        return redirectView;
    }
}
