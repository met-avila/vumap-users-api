package com.geoit.mrm.users.api.controllers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.geoit.mrm.users.api.models.ErrorResponse;
import com.geoit.mrm.users.api.Utils.Utils;
import com.geoit.mrm.users.api.models.Menu;
import com.geoit.mrm.users.api.models.PermisoMenu;
import com.geoit.mrm.users.api.models.Sistema;
import com.geoit.mrm.users.api.repositories.MenusRepository;
import com.geoit.mrm.users.api.repositories.PermisoMenuRepository;
import com.geoit.mrm.users.api.repositories.SistemasRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sistemas")
public class SistemasController {

    @Autowired
    private SistemasRepository repository;

    @Autowired
    private MenusRepository mRepository;
    @Autowired
    private PermisoMenuRepository pmRepository;

    @GetMapping
    public ResponseEntity<?> getSistemas(@RequestParam(name = "n", required = false) String nombre) {

        Sistema sistema = Sistema.builder().nombre(nombre).build();

        Example<Sistema> example = Example.of(sistema, ExampleMatcher.matchingAll().withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        List<Sistema> sistemas = repository.findAll(example, Sort.by("nombre"));

        List<Menu> menus = (List<Menu>) mRepository.findAll();
        List<PermisoMenu> permisosMenu = (List<PermisoMenu>) pmRepository.findAll();

        for (Sistema s : sistemas) {
            s.setMenus(obtenerMenusSistema(s.getIdSistema(), menus, permisosMenu));
        }

        return ResponseEntity.ok().body(sistemas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSistemasId(@PathVariable int id) {
        Sistema sistema = repository.findById(id);
        if (sistema == null) {
            String errorMessage = MessageFormat.format("Sistema con id={0} no existe", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND, errorMessage));
        }

        List<Menu> menus = (List<Menu>) mRepository.findAll();
        List<PermisoMenu> permisosMenu = (List<PermisoMenu>) pmRepository.findAll();

        sistema.setMenus(obtenerMenusSistema(id, menus, permisosMenu));
        return ResponseEntity.ok().body(sistema);
    }

    private List<Menu> obtenerMenusSistema(int idSistema, List<Menu> menus, List<PermisoMenu> permisosMenu) {
        List<PermisoMenu> permisos;
        menus = menus.stream().filter(m -> m.getIdSistema() == idSistema).collect(Collectors.toList());
        for (Menu menu : menus) {
            permisos = new ArrayList<PermisoMenu>();
            final List<PermisoMenu> pmItems = permisosMenu.stream().filter(pm -> pm.getIdMenu() == menu.getIdMenu())
                    .collect(Collectors.toList());
            for (PermisoMenu permisoMenu : pmItems) {
                permisos.add(permisoMenu);
            }

            Collections.sort(permisos, (d1, d2) -> d1.getIdPermisoMenu() - d2.getIdPermisoMenu());
            menu.setPermisosMenu(permisos);
        }
        return Utils.jerarquizarMenus(menus);
    }
}