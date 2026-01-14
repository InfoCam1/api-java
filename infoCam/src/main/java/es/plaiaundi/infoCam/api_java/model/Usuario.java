package es.plaiaundi.infoCam.api_java.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "usuarios")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_admin")
    private Boolean is_admin;

    @Column(nullable = false, unique = true)
    private String username;

    @JsonIgnore // Seguridad: nunca devolver password
    @Column(nullable = false)
    private String password;

    private String email;
    private int telefono;
    private String nombre;
    private String apellido;

    // RELACIÓN 1:N (Usuario -> Incidencias)
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonManagedReference // Muestra las incidencias dentro del usuario
    private List<Incidencia> incidencias = new ArrayList<>();

    // RELACIÓN N:M (Usuario -> Cámaras Favoritas)
    @ManyToMany(fetch = FetchType.EAGER) // EAGER ayuda a evitar problemas al serializar favoritos pequeños
    @JoinTable(
            name = "usuario_favoritos",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "camara_id")
    )
    private Set<Camara> favoritos = new HashSet<>();
}