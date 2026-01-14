package es.plaiaundi.infoCam.api_java.service;

import es.plaiaundi.infoCam.api_java.model.Camara;
import es.plaiaundi.infoCam.api_java.repository.CamaraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CamaraService {
    @Autowired private CamaraRepository camaraRepository;

    public List<Camara> obtenerTodas() {
        return camaraRepository.findAll();
    }

    public Camara guardar(Camara c) {
        return camaraRepository.save(c);
    }
}