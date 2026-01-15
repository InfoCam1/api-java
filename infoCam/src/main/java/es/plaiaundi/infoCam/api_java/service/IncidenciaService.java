package es.plaiaundi.infoCam.api_java.service;

import es.plaiaundi.infoCam.api_java.model.Incidencia;
import es.plaiaundi.infoCam.api_java.repository.IncidenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncidenciaService {
    @Autowired private IncidenciaRepository incidenciaRepository;

    public List<Incidencia> obtenerTodas() {
        return incidenciaRepository.findAll();
    }

    public Incidencia guardar(Incidencia i) {
        return incidenciaRepository.save(i);
    }

    public Incidencia findById(Long id) {
        return incidenciaRepository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        incidenciaRepository.deleteById(id);
    }

    public List<Incidencia> buscarCercanas(double lat, double lon, double distancia) {
        List<Incidencia> todas = incidenciaRepository.findAll();
        return todas.stream()
                .filter(i -> {
                    try {
                        if (i.getLatitud() == null || i.getLongitud() == null) return false;
                        double iLat = Double.parseDouble(i.getLatitud());
                        double iLon = Double.parseDouble(i.getLongitud());
                        
                        double dLat = iLat - lat;
                        double dLon = iLon - lon;
                        double dist = Math.sqrt(dLat*dLat + dLon*dLon);
                        return dist <= distancia;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }
}
