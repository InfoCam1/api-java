package es.plaiaundi.infoCam.api_java.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "incidencias")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Incidencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //id externa para guardar la id de opendata
    @Column(unique = true)
    private String externalId;

    private String nombre;

    @Column(name = "tipo_incidencia")
    private String tipoIncidencia;

    private String causa;
    private Date fecha_inicio;
    private Date fecha_fin;
    private String latitud;
    private String longitud;

    // RELACIÓN N:1 (Muchos reportes -> Un usuario)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonBackReference // Corta el bucle: Al ver incidencia, no recargamos todo el usuario
    private Usuario usuario;
}