package es.plaiaundi.infoCam.api_java.service;

import es.plaiaundi.infoCam.api_java.model.Camara;
import es.plaiaundi.infoCam.api_java.repository.CamaraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CamaraService {
    @Autowired private CamaraRepository camaraRepository;

    //método de jpa para obtener todos
    public List<Camara> obtenerTodas() {
        return camaraRepository.findAll();
    }

    //método de jpa para guardar cámara
    public Camara guardar(Camara c) {
        return camaraRepository.save(c);
    }

    //método de jpa para buscar cámara por id
    public Camara findById(Integer id) {
        return camaraRepository.findById(id).orElse(null);
    }

    //método de jpa para eliminar cámaras por id
    public void delete(Integer id) {
        camaraRepository.deleteById(id);
    }

    //método que recoje "latitud", "longitud" y distancia y devuelve las cámaras en ese rango de distancia
    public List<Camara> buscarCercanas(double lat, double lon, double distancia) {
        List<Camara> todas = camaraRepository.findAll();
        return todas.stream()
                .filter(c -> {
                    double dLat = c.getLatitud() - lat;
                    double dLon = c.getLongitud() - lon;
                    double dist = Math.sqrt(dLat*dLat + dLon*dLon);
                    return dist <= distancia;
                })
                .collect(Collectors.toList());
    }

    //método jpa que devuelve las cámaras con "activa" = true
    public List<Camara> buscarActivas() {
        return camaraRepository.findByActivaTrue();
    }
}
