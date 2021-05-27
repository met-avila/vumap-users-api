package com.geoit.mrm.users.api.repositories;

import java.util.List;

import javax.transaction.Transactional;

import com.geoit.mrm.users.api.models.PerfilPermisoMenu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PerfilPermisoMenuRepository extends JpaRepository<PerfilPermisoMenu, Integer> {
    List<PerfilPermisoMenu> findByIdPerfilMenu(Integer idPerfilMenu);

    @Transactional
    @Modifying
    @Query("delete from PerfilPermisoMenu ppm where ppm.idPerfilMenu in ?1")
    void deleteWithIdsPerfilMenu(List<Integer> ids);
}