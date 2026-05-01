package com.conti_talent.springboot.appweb.conti_talent_web.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    @GetMapping
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
}
