package es.plaiaundi.infoCam.api_java.controller;

import es.plaiaundi.infoCam.api_java.model.Camara;
import es.plaiaundi.infoCam.api_java.service.CamaraService;
import es.plaiaundi.infoCam.api_java.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/camaras")
public class CamaraController {

    @Autowired private CamaraService camaraService;
    @Autowired private UsuarioService usuarioService;

    @GetMapping
    public List<Camara> getAll() {
        return camaraService.obtenerTodas();
    }

    @GetMapping("/cercanas")
    public List<Camara> getCercanas(@RequestParam double lat, @RequestParam double lon, @RequestParam double distancia) {
        return camaraService.buscarCercanas(lat, lon, distancia);
    }

    @GetMapping("/{id}")
    public Camara getOne(@PathVariable Integer id) {
        return camaraService.findById(id);
    }

    @PostMapping("/{id}/favorita")
    public void toggleFavorita(@PathVariable Integer id, @RequestParam Long usuarioId) {
        usuarioService.toggleFavoritoCamara(usuarioId, id);
    }

    @PostMapping
    public Camara create(@RequestBody Camara c) {
        return camaraService.guardar(c);
    }

    @PutMapping("/{id}")
    public Camara update(@PathVariable Integer id, @RequestBody Camara info) {
        Camara ex = camaraService.findById(id);
        if (ex != null) {
            ex.setNombre(info.getNombre());
            ex.setLatitud(info.getLatitud());
            ex.setLongitud(info.getLongitud());
            ex.setImagen(info.getImagen());
            ex.setActiva(info.isActiva());
            return camaraService.guardar(ex);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        camaraService.delete(id);
    }
}
