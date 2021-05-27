package com.geoit.mrm.users.api.models;

import java.util.Date;
import java.util.List;

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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "perfiles")
@JsonInclude(value = Include.NON_EMPTY)
public class Perfil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPerfil;

    @Transient
    @NotNull(message = "{Perfil.idSistema.NotNull}")
    private Integer idSistema;

    @OneToOne
    @JoinColumn(name = "id_sistema")
    private Sistema sistema;

    @NotEmpty(message = "{Perfil.nombre.NotEmpty}")
    @Column(nullable = false)
    private String nombre;
    private String descripcion;
    
    @NotNull(message = "{Perfil.estatus.NotNull}")
    private Integer estatus;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(nullable = false)
    private Boolean isRoot;

    @Transient
    private List<Menu> menus;

    @Transient
    private int numeroUsuarios;

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
}