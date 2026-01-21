package es.plaiaundi.infoCam.api_java.repository;
import es.plaiaundi.infoCam.api_java.model.Incidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {
    Optional<Incidencia> findByExternalId(String externalId);
    @Query("SELECT DISTINCT i.tipoIncidencia FROM Incidencia i WHERE i.tipoIncidencia IS NOT NULL")
    List<String> findDistinctTipoIncidencia();
}