package es.plaiaundi.infoCam.api_java.service;

import es.plaiaundi.infoCam.api_java.model.Incidencia;
import es.plaiaundi.infoCam.api_java.repository.IncidenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncidenciaService {
    @Autowired private IncidenciaRepository incidenciaRepository;

    //método jpa para ver todas las incidencias
    public List<Incidencia> obtenerTodas() {
        return incidenciaRepository.findAll();
    }

    //método jpa que recibe un objeto y lo guarda en la BBDD
    public Incidencia guardar(Incidencia i) {
        return incidenciaRepository.save(i);
    }

    //método jpa que busca incidencias por id
    public Incidencia findById(Long id) {
        return incidenciaRepository.findById(id).orElse(null);
    }

    //método jpa que elimina incidencias de la BBDD por id
    public void delete(Long id) {
        incidenciaRepository.deleteById(id);
    }

    //método que recibe "latitud", "longitud" y distancia y devuelve listado de cámaras en ese rango
    public List<Incidencia> buscarCercanas(double lat, double lon, double distancia) {
        //recoge todas las incidencias
        List<Incidencia> todas = incidenciaRepository.findAll();
        return todas.stream()
                .filter(i -> {
                    try {
                        //primero verifica que la incidencia tenga latitud y longitud != null
                        if (i.getLatitud() == null || i.getLongitud() == null) return false;
                        double iLat = Double.parseDouble(i.getLatitud());
                        double iLon = Double.parseDouble(i.getLongitud());

                        //resta los puntos del mapa
                        double dLat = iLat - lat;
                        double dLon = iLon - lon;
                        //hace el calculo de distancia entre los puntos de cada incidencia y los valores entrados en el método
                        double dist = Math.sqrt(dLat*dLat + dLon*dLon);
                        //devuelve únicamente las incidencias que esten a menos de esa distancia
                        return dist <= distancia;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    public List<Incidencia> buscarActivas(String fechaStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date fechaConsulta = sdf.parse(fechaStr);

            // Definimos el límite de "actualidad" para incidencias sin fecha fin
            long hace48Horas = System.currentTimeMillis() - (48 * 60 * 60 * 1000);
            Date limiteActualidad = new Date(hace48Horas);

            List<Incidencia> todas = incidenciaRepository.findAll();

            return todas.stream()
                    .filter(i -> {
                        Date inicio = i.getFecha_inicio();
                        Date fin = i.getFecha_fin();
                        String tipo = (i.getTipoIncidencia() != null) ? i.getTipoIncidencia() : "";

                        if (inicio == null) return false;

                        // Verificación básica de rango (si tiene fecha fin)
                        boolean haComenzado = !fechaConsulta.before(inicio);

                        // 2. Lógica especial para incidencias sin fecha de fin
                        if (fin == null) {
                            // Si es OBRA o MANTENIMIENTO, la dejamos pasar aunque sea vieja (suelen durar más)
                            if (tipo.equalsIgnoreCase("Obras") || tipo.equalsIgnoreCase("Mantenimiento")) {
                                return haComenzado;
                            }

                            // Si es un ACCIDENTE/AVERÍA sin fecha fin, solo la mostramos si empezó hoy o hace menos de 24h
                            // Esto elimina las "incidencias fantasma" antiguas de OpenData
                            return haComenzado && inicio.after(limiteActualidad);
                        }

                        // 3. Si tiene fecha fin, simplemente comprobamos que estemos dentro del rango
                        return haComenzado && !fechaConsulta.after(fin);
                    })
                    .collect(Collectors.toList());

        } catch (ParseException e) {
            System.err.println("Formato de fecha inválido: " + e.getMessage());
            return List.of();
        }
    }

    public List<String> obtenerTipos() {
        return incidenciaRepository.findDistinctTipoIncidencia();
    }
}
