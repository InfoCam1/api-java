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

    //Enpoint que lista todas las incidencias de la BBDD
    @GetMapping
    public List<Incidencia> getAll() {
        return incidenciaService.obtenerTodas();
    }

    //Enpoint que recibe "latitud", "longitud" y distancia y devuelve las incidencias en ese rango de distancia
    @GetMapping("/cercanas")
    public List<Incidencia> getCercanas(@RequestParam double lat, @RequestParam double lon, @RequestParam double distancia) {
        return incidenciaService.buscarCercanas(lat, lon, distancia);
    }

    //Enpoint que recibe una fecha y devuelve la lista de incidencias que esten activas o en un rango de 48H
    @GetMapping("/activas")
    public List<Incidencia> getActivas(@RequestParam String fecha){
        return incidenciaService.buscarActivas(fecha);
    }

    //Enpoint que devuelve todos los tipos de incidencia que hay actuales
    @GetMapping("/tipos")
    public List<String> getTiposIncidencia() {
        return incidenciaService.obtenerTipos();
    }

    //Enpoint que recibe un objeto incidencia y lo inserta en la BBDD
    @PostMapping
    public Incidencia create(@RequestBody Incidencia i) {
        if (i.getFecha_inicio() == null) {
            i.setFecha_inicio(new Date());
        }
        return incidenciaService.guardar(i);
    }

    //Enpoint con un id que recibe un objeto incidencia y lo actualiza en la BBDD
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

    //Enpoint con un id que elimina el registro de la BBDD
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        incidenciaService.delete(id);
    }
}
