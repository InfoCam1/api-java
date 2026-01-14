package es.plaiaundi.infoCam.api_java.repository;
import es.plaiaundi.infoCam.api_java.model.Incidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {}