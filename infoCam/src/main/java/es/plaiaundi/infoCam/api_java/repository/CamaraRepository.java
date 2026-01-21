package es.plaiaundi.infoCam.api_java.repository;
import es.plaiaundi.infoCam.api_java.model.Camara;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CamaraRepository extends JpaRepository<Camara, Integer> {
    List<Camara> findByActivaTrue();
}