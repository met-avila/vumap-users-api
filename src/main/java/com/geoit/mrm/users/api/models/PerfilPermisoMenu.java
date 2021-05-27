package com.geoit.mrm.users.api.models;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "perfil_permisos_menu")
@JsonInclude(value = Include.NON_EMPTY)
public class PerfilPermisoMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer idPerfilMenu;
    @Column(nullable = false)
    private Integer idPermisoMenu;

    public PerfilPermisoMenu(Integer idPerfilMenu, Integer idPermisoMenu) {
        this.idPerfilMenu = idPerfilMenu;
        this.idPermisoMenu = idPermisoMenu;
    }
}