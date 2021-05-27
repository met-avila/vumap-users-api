package com.geoit.mrm.users.api.models;

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
@Table(name = "perfil_menus")
@JsonInclude(value = Include.NON_EMPTY)
public class PerfilMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer idPerfilMenu;

    private Integer idPerfil;
    private Integer idMenu;

    public PerfilMenu(Integer idPerfil, Integer idMenu) {
        this.idPerfil = idPerfil;
        this.idMenu = idMenu;
    }
}