package com.geoit.mrm.users.api.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuarios")
@JsonInclude(value = Include.NON_EMPTY)
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @NotEmpty(message = "{Usuario.nombrePropio.NotEmpty}")
    @Column(nullable = false)
    private String nombrePropio;
    @NotEmpty(message = "{Usuario.apellidoPaterno.NotEmpty}")
    @Column(nullable = false)
    private String apellidoPaterno;
    @NotEmpty(message = "{Usuario.apellidoPaterno.NotEmpty}")
    @Column(nullable = false)
    private String apellidoMaterno;

    @NotEmpty(message = "{Usuario.nombreUsuario.NotEmpty}")
    @Column(nullable = false)
    private String nombreUsuario;

    @JsonIgnore
    private String password;

    @Column(nullable = false)
    @NotEmpty(message = "{Usuario.correo.NotEmpty}")
    private String correo;
    private String celular;
    private String urlFoto;
    @NotNull(message = "{Usuario.estatus.NotNull}")
    private Integer estatus;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaUltimoAcceso;

    @Transient
    @NotNull(message = "{Usuario.idPerfil.NotNull}")
    private Integer idPerfil;

    @OneToOne
    @JoinColumn(name="id_perfil")
    private Perfil perfil;

    @Column(nullable = false)
    private Boolean isRoot;

    @PrePersist
    void prePersist() {
        fechaCreacion = new Date();
    }

    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (Exception e) {
            return null;
        }
    }

    public String getFullname() {
        String fullname = "";
        if (nombrePropio != null) {
            fullname += nombrePropio;
            if (apellidoPaterno != null) {
                fullname += " " + apellidoPaterno;
                if (apellidoMaterno != null) {
                    fullname += " " + apellidoMaterno;
                }
            }
        }
        return fullname;
    }
}