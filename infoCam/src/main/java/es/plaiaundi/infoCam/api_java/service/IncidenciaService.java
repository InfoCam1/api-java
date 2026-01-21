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

    public List<Incidencia> buscarActivas(String fechaStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaConsulta = sdf.parse(fechaStr);

            // Definimos el límite de "actualidad" para incidencias sin fecha fin (ej: 24 horas atrás)
            long hace24Horas = System.currentTimeMillis() - (48 * 60 * 60 * 1000);
            Date limiteActualidad = new Date(hace24Horas);

            List<Incidencia> todas = incidenciaRepository.findAll();

            return todas.stream()
                    .filter(i -> {
                        Date inicio = i.getFecha_inicio();
                        Date fin = i.getFecha_fin();
                        String tipo = (i.getTipoIncidencia() != null) ? i.getTipoIncidencia() : "";

                        if (inicio == null) return false;

                        // 1. Verificación básica de rango (si tiene fecha fin)
                        boolean haComenzado = !fechaConsulta.before(inicio);

                        // 2. Lógica especial para incidencias sin fecha de fin
                        if (fin == null) {
                            // Si es OBRA o MANTENIMIENTO, la dejamos pasar aunque sea vieja (son de larga duración)
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
