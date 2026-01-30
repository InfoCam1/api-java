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

    //Enpoint para listar todas las camaras de la BBDD
    @GetMapping
    public List<Camara> getAll() {
        return camaraService.obtenerTodas();
    }

    //Enpoint que recibe "latitud", "longitud" y "distancia" y devuelve las cámaras que esten en ese rango de distancia
    @GetMapping("/cercanas")
    public List<Camara> getCercanas(@RequestParam double lat, @RequestParam double lon, @RequestParam double distancia) {
        return camaraService.buscarCercanas(lat, lon, distancia);
    }

    //Enpoint que devuelve las cámaras que estén activas y que tengan una imagen válida
    @GetMapping("/activas")
    public List<Camara> getActivas() {
        return camaraService.buscarActivas();
    }

    //Enpoint con id y devuelve la cámara en cuestión
    @GetMapping("/{id}")
    public Camara getOne(@PathVariable Integer id) {
        return camaraService.findById(id);
    }

    //Enpoint con id de cámara que recibe un usuario_id y añade esa cámara a la lista de favoritos del usuario
    //Si esa cámara ya es favorita de ese usuario la elimina de favoritos (TOGGLE)
    @PostMapping("/{id}/favorita")
    public void toggleFavorita(@PathVariable Integer id, @RequestParam Long usuarioId) {
        usuarioService.toggleFavoritoCamara(usuarioId, id);
    }

    //Enpoint que recibe un objeto cámara y lo inserta en la BBDD
    @PostMapping
    public Camara create(@RequestBody Camara c) {
        return camaraService.guardar(c);
    }

    //Enpoint con un id que recibe un objeto cámara y lo actualiza en la BBDD
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

    //Enpoint con un id de cámara que elimina el registro de la BBDD
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        camaraService.delete(id);
    }
}
