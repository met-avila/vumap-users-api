package com.geoit.mrm.users.api.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tipos_confirmacion_token")
public class TipoConfirmacionToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTipoConfirmacion;

    @Column(nullable = false)
    private String nombre;
}