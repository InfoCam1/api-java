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

    public List<Camara> obtenerTodas() {
        return camaraRepository.findAll();
    }

    public Camara guardar(Camara c) {
        return camaraRepository.save(c);
    }

    public Camara findById(Integer id) {
        return camaraRepository.findById(id).orElse(null);
    }

    public void delete(Integer id) {
        camaraRepository.deleteById(id);
    }

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

    public List<Camara> buscarActivas() {
        return camaraRepository.findByActivaTrue();
    }
}
