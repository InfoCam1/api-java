package es.plaiaundi.infoCam.api_java.controller;

import es.plaiaundi.infoCam.api_java.model.Incidencia;
import es.plaiaundi.infoCam.api_java.service.IncidenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/incidencias")
public class IncidenciaController {

    @Autowired private IncidenciaService incidenciaService;

    @GetMapping
    public List<Incidencia> getAll() {
        return incidenciaService.obtenerTodas();
    }
}