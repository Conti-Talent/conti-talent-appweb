package com.conti_talent.springboot.appweb.conti_talent_web.seed;

import com.conti_talent.springboot.appweb.conti_talent_web.model.Area;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Oferta;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Usuario;
import com.conti_talent.springboot.appweb.conti_talent_web.service.AreaService;
import com.conti_talent.springboot.appweb.conti_talent_web.service.OfertaService;
import com.conti_talent.springboot.appweb.conti_talent_web.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final AreaService areaService;
    private final OfertaService ofertaService;
    private final UsuarioService usuarioService;

    public DataLoader(AreaService areaService, OfertaService ofertaService, UsuarioService usuarioService) {
        this.areaService = areaService;
        this.ofertaService = ofertaService;
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) {
        Area a1 = new Area(); a1.setNombre("Ingenieria"); a1.setDescripcion("Facultad de Ingenieria");
        Area a2 = new Area(); a2.setNombre("Ciencias"); a2.setDescripcion("Facultad de Ciencias");
        Area a3 = new Area(); a3.setNombre("Negocios"); a3.setDescripcion("Facultad de Negocios");
        areaService.crear(a1); areaService.crear(a2); areaService.crear(a3);

        Oferta o1 = new Oferta(); o1.setTitulo("Docente de Programacion"); o1.setDescripcion("Se busca docente con experiencia en Java."); o1.setAreaId(1L);
        Oferta o2 = new Oferta(); o2.setTitulo("Practicante de Sistemas"); o2.setDescripcion("Practicante para soporte de sistemas."); o2.setAreaId(1L);
        Oferta o3 = new Oferta(); o3.setTitulo("Asistente de Laboratorio"); o3.setDescripcion("Asistente para laboratorio de quimica."); o3.setAreaId(2L);
        ofertaService.crear(o1); ofertaService.crear(o2); ofertaService.crear(o3);

        Usuario admin = new Usuario();
        admin.setNombre("Admin"); admin.setApellido("Sistema");
        admin.setEmail("admin@conti.pe"); admin.setPassword("admin123");
        admin.setRol("admin"); admin.setActivo(true);
        usuarioService.guardar(admin);
    }
}
