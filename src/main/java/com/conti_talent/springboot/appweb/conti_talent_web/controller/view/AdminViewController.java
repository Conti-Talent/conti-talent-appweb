package com.conti_talent.springboot.appweb.conti_talent_web.controller.view;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Area;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Oferta;
import com.conti_talent.springboot.appweb.conti_talent_web.service.AreaService;
import com.conti_talent.springboot.appweb.conti_talent_web.service.OfertaService;
import com.conti_talent.springboot.appweb.conti_talent_web.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    private final AreaService areaService;
    private final OfertaService ofertaService;
    private final UsuarioService usuarioService;

    public AdminViewController(AreaService areaService, OfertaService ofertaService, UsuarioService usuarioService) {
        this.areaService = areaService;
        this.ofertaService = ofertaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalAreas", areaService.listar().size());
        model.addAttribute("totalOfertas", ofertaService.listar().size());
        model.addAttribute("totalUsuarios", usuarioService.listar().size());
        return "admin/dashboard";
    }

    @GetMapping("/areas")
    public String areas(Model model) {
        model.addAttribute("areas", areaService.listar());
        return "admin/admin-areas";
    }

    @PostMapping("/areas")
    public String crearArea(@RequestParam String nombre, @RequestParam String descripcion) {
        Area a = new Area(); a.setNombre(nombre); a.setDescripcion(descripcion);
        areaService.crear(a);
        return "redirect:/admin/areas";
    }

    @PostMapping("/areas/eliminar")
    public String eliminarArea(@RequestParam Long id) {
        areaService.eliminar(id);
        return "redirect:/admin/areas";
    }

    @GetMapping("/ofertas")
    public String ofertas(Model model) {
        model.addAttribute("ofertas", ofertaService.listar());
        model.addAttribute("areas", areaService.listar());
        return "admin/admin-ofertas";
    }

    @PostMapping("/ofertas")
    public String crearOferta(@RequestParam String titulo, @RequestParam String descripcion, @RequestParam Long areaId) {
        Oferta o = new Oferta(); o.setTitulo(titulo); o.setDescripcion(descripcion); o.setAreaId(areaId);
        ofertaService.crear(o);
        return "redirect:/admin/ofertas";
    }

    @PostMapping("/ofertas/eliminar")
    public String eliminarOferta(@RequestParam Long id) {
        ofertaService.eliminar(id);
        return "redirect:/admin/ofertas";
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        return "admin/admin-usuarios";
    }

    @GetMapping("/postulantes")
    public String postulantes() { return "admin/admin-postulantes"; }

    @GetMapping("/preguntas")
    public String preguntas() { return "admin/admin-preguntas"; }

    @GetMapping("/metricas")
    public String metricas(Model model) {
        model.addAttribute("totalAreas", areaService.listar().size());
        model.addAttribute("totalOfertas", ofertaService.listar().size());
        model.addAttribute("totalUsuarios", usuarioService.listar().size());
        return "admin/admin-metricas";
    }
}
