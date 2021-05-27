package com.geoit.mrm.users.api.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bitacora")
@JsonInclude(value = Include.NON_EMPTY)
public class Bitacora {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBitacora;

    @Column(nullable = false)
    private Long idUsuario;

    @NotNull
    private Integer idMenu;

    @NotEmpty
    private String accion;

    private String descripcion;

    @Column(columnDefinition = "TEXT")
    private String datoNuevo;

    @Column(columnDefinition = "TEXT")
    private String datoAnterior;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    @PrePersist
    void prePersist() {
        fecha = new Date();
    }
}