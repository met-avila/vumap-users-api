package com.geoit.mrm.users.api.repositories;

import java.util.List;

import com.geoit.mrm.users.api.models.PermisoMenu;

import org.springframework.data.repository.CrudRepository;

public interface PermisoMenuRepository extends CrudRepository<PermisoMenu, Integer> {
    List<PermisoMenu> findByIdMenu(int idMenu);
}