package com.geoit.mrm.users.api.repositories;

import java.util.List;

import com.geoit.mrm.base.models.Menu;

import org.springframework.data.repository.CrudRepository;

public interface MenusRepository extends CrudRepository<Menu, Integer> {
    Menu findById(int id);

    List<Menu> findByIdSistema(int idSistema);
}