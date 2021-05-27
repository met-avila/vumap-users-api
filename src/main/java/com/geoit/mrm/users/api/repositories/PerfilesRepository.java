package com.geoit.mrm.users.api.repositories;

import com.geoit.mrm.users.api.models.Perfil;
import com.geoit.mrm.users.api.models.Sistema;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfilesRepository extends JpaRepository<Perfil, Integer> {

   Perfil findById(int id);

   Perfil findBySistemaAndNombre(Sistema sistema, String nombre);
}