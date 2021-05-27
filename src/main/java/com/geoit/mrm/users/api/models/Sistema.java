package com.geoit.mrm.users.api.models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sistemas")
@JsonInclude(value = Include.NON_EMPTY)
public class Sistema {
    @Id
    private Integer idSistema;
    @Column(nullable = false)
    private String nombre;

    @Transient
    private List<Menu> menus;
}