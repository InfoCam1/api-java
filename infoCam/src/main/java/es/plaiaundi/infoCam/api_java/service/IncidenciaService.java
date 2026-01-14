package es.plaiaundi.infoCam.api_java.service;

import es.plaiaundi.infoCam.api_java.model.Incidencia;
import es.plaiaundi.infoCam.api_java.repository.IncidenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class IncidenciaService {
    @Autowired private IncidenciaRepository incidenciaRepository;

    public List<Incidencia> obtenerTodas() {
        return incidenciaRepository.findAll();
    }

    public Incidencia guardar(Incidencia i) {
        return incidenciaRepository.save(i);
    }
}