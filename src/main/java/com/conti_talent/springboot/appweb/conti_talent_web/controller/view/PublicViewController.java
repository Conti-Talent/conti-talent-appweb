package com.conti_talent.springboot.appweb.conti_talent_web.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicViewController {

    @GetMapping("/")
    public String index() {
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

    @GetMapping("/areas")
    public String areas() {
        return "areas";
    }

    @GetMapping("/ofertas")
    public String ofertas() {
        return "ofertas";
    }

    @GetMapping("/oferta")
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

    @GetMapping("/mis-respuestas")
    public String misRespuestas() {
        return "mis-respuestas";
    }

    @GetMapping("/mi-estado")
    public String miEstado() {
        return "mi-estado";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "contacto";
    }

    @GetMapping("/publicidad")
    public String publicidad() {
        return "publicidad";
    }
}
