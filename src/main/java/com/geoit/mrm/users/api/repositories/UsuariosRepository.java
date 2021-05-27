package com.geoit.mrm.users.api.repositories;

import com.geoit.mrm.base.models.Perfil;
import com.geoit.mrm.base.models.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuariosRepository extends JpaRepository<Usuario, Long> {
    Usuario findByNombreUsuario(String nombreUsuario);

    Usuario findByCorreo(String correo);

    Usuario findByNombreUsuarioOrCorreo(String nombreUsuario, String correo);

    int countByPerfil(Perfil perfil);
}