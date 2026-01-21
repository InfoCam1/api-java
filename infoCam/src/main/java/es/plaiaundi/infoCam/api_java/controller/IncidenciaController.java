package es.plaiaundi.infoCam.api_java.controller;

import es.plaiaundi.infoCam.api_java.model.Incidencia;
import es.plaiaundi.infoCam.api_java.service.IncidenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Date;

@RestController
@RequestMapping("/api/incidencias")
public class IncidenciaController {

    @Autowired private IncidenciaService incidenciaService;

    @GetMapping
    public List<Incidencia> getAll() {
        return incidenciaService.obtenerTodas();
    }

    @GetMapping("/cercanas")
    public List<Incidencia> getCercanas(@RequestParam double lat, @RequestParam double lon, @RequestParam double distancia) {
        return incidenciaService.buscarCercanas(lat, lon, distancia);
    }

    @GetMapping("/activas")
    public List<Incidencia> getActivas(@RequestParam String fecha){
        return incidenciaService.buscarActivas(fecha);
    }

    @GetMapping("/tipos")
    public List<String> getTiposIncidencia() {
        return incidenciaService.obtenerTipos();
    }

    @PostMapping
    public Incidencia create(@RequestBody Incidencia i) {
        if (i.getFecha_inicio() == null) {
            i.setFecha_inicio(new Date());
        }
        return incidenciaService.guardar(i);
    }

    @PutMapping("/{id}")
    public Incidencia update(@PathVariable Long id, @RequestBody Incidencia info) {
        Incidencia ex = incidenciaService.findById(id);
        if (ex != null) {
            ex.setTipoIncidencia(info.getTipoIncidencia());
            ex.setCausa(info.getCausa());
            ex.setLatitud(info.getLatitud());
            ex.setLongitud(info.getLongitud());
            ex.setFecha_inicio(info.getFecha_inicio());
            ex.setFecha_fin(info.getFecha_fin());
            ex.setNombre(info.getNombre());
            return incidenciaService.guardar(ex);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        incidenciaService.delete(id);
    }
}
