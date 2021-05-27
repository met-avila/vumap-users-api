package com.geoit.mrm.users.api.repositories;

import com.geoit.mrm.users.api.models.Sistema;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SistemasRepository extends JpaRepository<Sistema, Integer> {
    Sistema findById(int id);
}