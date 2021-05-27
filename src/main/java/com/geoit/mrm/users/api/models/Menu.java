package com.geoit.mrm.users.api.models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;

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
@Table(name = "menus")
@JsonInclude(value = Include.NON_EMPTY)
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMenu;
    @NotEmpty
    @Column(nullable = false)
    private String nombre;
    private String descripcion;
    @NotEmpty
    private String href;
    private Integer idMenuPadre;
    private String icono;

    private Integer orden;

    @Column(nullable = false)
    private Integer idSistema;

    @Transient
    List<PermisoMenu> permisosMenu;

    @Transient
    List<Menu> menus;
}