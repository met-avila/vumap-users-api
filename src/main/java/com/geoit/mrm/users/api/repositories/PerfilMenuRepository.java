package com.geoit.mrm.users.api.repositories;

import java.util.List;

import com.geoit.mrm.base.models.PerfilMenu;

import org.springframework.data.repository.CrudRepository;

public interface PerfilMenuRepository extends CrudRepository<PerfilMenu, Integer> {
    List<PerfilMenu> findByIdPerfil(int idPerfil);

}