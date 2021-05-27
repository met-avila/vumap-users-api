package com.geoit.mrm.users.api.models;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@Entity
@Table(name = "permisos_menu")
@JsonInclude(value = Include.NON_EMPTY)
public class PermisoMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer idPermisoMenu;

    private Integer idMenu;
    
    @Transient
    private Integer idTipoPermiso;
    private String cssClase;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_permiso")
    private TipoPermisoMenu tipoPermisoMenu;
}