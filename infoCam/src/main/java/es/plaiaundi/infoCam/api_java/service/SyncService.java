package es.plaiaundi.infoCam.api_java.service;

import org.springframework.stereotype.Service;

@Service
public class SyncService {

    private final es.plaiaundi.infoCam.api_java.repository.IncidenciaRepository incidenciaRepository;
    private final org.springframework.web.client.RestTemplate restTemplate;

    public SyncService(es.plaiaundi.infoCam.api_java.repository.IncidenciaRepository incidenciaRepository) {
        this.incidenciaRepository = incidenciaRepository;
        this.restTemplate = new org.springframework.web.client.RestTemplate();
    }

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SyncService.class);
    
    // ... existing fields ...

    public String sincronizarDatos() {
        String url = "https://api.euskadi.eus/traffic/v1.0/incidences/byYear/2025?_page=1";
        logger.info("Iniciando sincronización desde {}", url);
        
        try {
            TrafficResponse response = restTemplate.getForObject(url, TrafficResponse.class);
            logger.info("Respuesta recibida: {}", response);
            
            if (response != null && response.incidences() != null) {
                logger.info("Incidencias encontradas: {}", response.incidences().size());
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                int savedCount = 0;
                
                for (TrafficIncidence ti : response.incidences()) {
                    try {
                        es.plaiaundi.infoCam.api_java.model.Incidencia incidencia = new es.plaiaundi.infoCam.api_java.model.Incidencia();
                        
                        incidencia.setTipoIncidencia(ti.incidenceType());
                        incidencia.setCausa(ti.cause());
                        incidencia.setLatitud(ti.latitude());
                        incidencia.setLongitud(ti.longitude());
                        
                        String name = (ti.incidenceType() != null ? ti.incidenceType() : "Incidencia") + 
                                      (ti.road() != null ? " - " + ti.road() : "") + 
                                      (ti.direction() != null ? " (" + ti.direction() + ")" : "");
                        incidencia.setNombre(name);

                        if (ti.startDate() != null) {
                             incidencia.setFecha_inicio(sdf.parse(ti.startDate()));
                        }
                        
                        // NOTE: If validation fails here due to missing Usuario, it will throw exception
                        incidenciaRepository.save(incidencia);
                        savedCount++;
                    } catch (Exception e) {
                       logger.error("Error guardando incidencia {}: {}", ti.incidenceId(), e.getMessage(), e);
                    }
                }
                String msg = "Sincronización completada. " + savedCount + " incidencias guardadas de " + response.incidences().size() + " encontradas.";
                logger.info(msg);
                return msg;
            } else {
                logger.warn("Respuesta vacía o nula de la API.");
                return "No se obtuvieron datos de la API.";
            }
        } catch (Exception e) {
            logger.error("Error general en sincronización", e);
            return "Error durante la sincronización: " + e.getMessage();
        }
    }

    // DTOs for API Response
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    private record TrafficResponse(
        int totalItems,
        int totalPages,
        int currentPage,
        java.util.List<TrafficIncidence> incidences
    ) {}

    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    private record TrafficIncidence(
        String incidenceId,
        String sourceId,
        String incidenceType,
        String autonomousRegion,
        String province,
        String carRegistration,
        String cause,
        String cityTown,
        String startDate,
        String incidenceLevel,
        String road,
        String pkStart,
        String pkEnd,
        String direction,
        String latitude,
        String longitude
    ) {}
}
