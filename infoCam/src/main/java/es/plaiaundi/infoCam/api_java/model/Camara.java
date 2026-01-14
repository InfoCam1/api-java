package es.plaiaundi.infoCam.api_java.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "camaras")
@Getter @Setter // Usamos esto en lugar de @Data para evitar bucles infinitos
@NoArgsConstructor
public class Camara {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;
    private double latitud;
    private double longitud;
    private String imagen;
    private boolean activa;

    // RELACIÓN N:M (Lado inverso)
    // Una cámara no necesita listar a todos los usuarios que la siguen cada vez que se carga
    @ManyToMany(mappedBy = "favoritos")
    @JsonIgnore
    private Set<Usuario> usuariosQueMeSiguen = new HashSet<>();

    public Camara(String nombre, double latitud, double longitud, String imagen, boolean activa) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.imagen = imagen;
        this.activa = activa;
    }
}