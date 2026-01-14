package es.plaiaundi.infoCam.api_java.controller;

import es.plaiaundi.infoCam.api_java.model.Camara;
import es.plaiaundi.infoCam.api_java.service.CamaraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/camaras")
public class CamaraController {

    @Autowired private CamaraService camaraService;

    @GetMapping
    public List<Camara> getAll() {
        return camaraService.obtenerTodas();
    }
}