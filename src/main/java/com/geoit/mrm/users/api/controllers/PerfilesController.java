package com.geoit.mrm.users.api.controllers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.geoit.mrm.users.api.Utils.Utils;
import com.geoit.mrm.users.api.models.ErrorResponse;
import com.geoit.mrm.users.api.models.Menu;
import com.geoit.mrm.users.api.models.Perfil;
import com.geoit.mrm.users.api.models.PerfilMenu;
import com.geoit.mrm.users.api.models.PerfilPermisoMenu;
import com.geoit.mrm.users.api.models.PermisoMenu;
import com.geoit.mrm.users.api.models.Sistema;
import com.geoit.mrm.users.api.models.TipoPermisoMenu;
import com.geoit.mrm.users.api.repositories.MenusRepository;
import com.geoit.mrm.users.api.repositories.PerfilMenuRepository;
import com.geoit.mrm.users.api.repositories.PerfilPermisoMenuRepository;
import com.geoit.mrm.users.api.repositories.PerfilesRepository;
import com.geoit.mrm.users.api.repositories.PermisoMenuRepository;
import com.geoit.mrm.users.api.repositories.SistemasRepository;
import com.geoit.mrm.users.api.repositories.TipoPermisoMenuRepository;
import com.geoit.mrm.users.api.repositories.UsuariosRepository;
import com.geoit.mrm.users.api.services.JwtTokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/perfiles")
public class PerfilesController {
    // final private int ID_MENU = 2;
    @Autowired
    private SistemasRepository sRepository;

    @Autowired
    private PerfilesRepository pRepository;
    @Autowired
    private PerfilMenuRepository pmRepository;

    @Autowired
    private PerfilPermisoMenuRepository ppmRepository;
    @Autowired
    private PermisoMenuRepository pMenuRepository;
    @Autowired
    private TipoPermisoMenuRepository tpmRepository;

    @Autowired
    private UsuariosRepository uRepository;

    // @Autowired
    // private LogsApiComponent logsApiComponent;

    @Autowired
    private JwtTokenService jwtService;

    @Autowired
    private MenusRepository mRepository;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> postPerfiles(@Valid @RequestBody Perfil perfil) {
        int idSistema = perfil.getIdSistema();
        String nombre = perfil.getNombre();
        Sistema sistema = sRepository.findById(idSistema);

        if (sistema == null) {
            String errorMessage = MessageFormat.format("Sistema con idSistema={0} no encontrado", idSistema);
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessage));
        }

        Perfil sPerfil = pRepository.findBySistemaAndNombre(sistema, nombre);
        if (sPerfil != null) {
            String errorMessage = MessageFormat
                    .format("Perfil con nombre={0} ya existe dentro del sistema idSistema={1}", nombre, idSistema);
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessage));
        }

        perfil.setSistema(sistema);
        perfil.setIsRoot(false);
        sPerfil = pRepository.save(perfil);

        // logsApiComponent.log(ID_MENU, Accion.AGREGAR, sPerfil.toJson(), null);

        PerfilMenu sPerfilMenu;
        List<PermisoMenu> permisosMenu;
        List<Menu> menus = perfil.getMenus();
        if (menus != null && menus.size() > 0) {
            for (Menu menu : menus) {
                sPerfilMenu = pmRepository.save(new PerfilMenu(sPerfil.getIdPerfil(), menu.getIdMenu()));
                permisosMenu = menu.getPermisosMenu();
                if (permisosMenu != null && permisosMenu.size() > 0) {
                    for (PermisoMenu permisoMenu : permisosMenu) {
                        ppmRepository.save(
                                new PerfilPermisoMenu(sPerfilMenu.getIdPerfilMenu(), permisoMenu.getIdPermisoMenu()));
                    }
                }
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(sPerfil);
    }

    @PutMapping(path = "/{idPerfil}", consumes = "application/json")
    public ResponseEntity<?> putPerfiles(@PathVariable int idPerfil, @Valid @RequestBody Perfil perfil) {
        int idSistema = perfil.getIdSistema();
        String nombre = perfil.getNombre();
        Perfil sPerfil = pRepository.findById(idPerfil);
        if (sPerfil == null) {
            String errorMessage = MessageFormat.format("Perfil con id={0} no existe", idPerfil);
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessage));
        }

        Sistema sistema = new Sistema();
        sistema.setIdSistema(idSistema);
        Perfil tPerfil = pRepository.findBySistemaAndNombre(sistema, nombre);
        if (tPerfil != null && tPerfil.getIdPerfil() != sPerfil.getIdPerfil()) {
            String errorMessage = MessageFormat
                    .format("Perfil con nombre={0} ya existe dentro del sistema idSistema={1}", nombre, idSistema);
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessage));
        }

        // String hPerfil = sPerfil.toJson();

        // Elimina los menus y permisos del perfil
        final List<PerfilMenu> perfilMenus = pmRepository.findByIdPerfil(idPerfil);
        if (perfilMenus != null && perfilMenus.size() > 0) {
            final List<Integer> ids = perfilMenus.stream().map((pm) -> pm.getIdPerfilMenu())
                    .collect(Collectors.toList());
            if (ids != null && ids.size() > 0) {
                ppmRepository.deleteWithIdsPerfilMenu(ids);
            }
            pmRepository.deleteAll(perfilMenus);
        }

        // Agregar menus y permisos al perfil
        PerfilMenu sPerfilMenu;
        List<PermisoMenu> permisosMenu;
        List<Menu> menus = perfil.getMenus();
        if (menus != null && menus.size() > 0) {
            for (Menu menu : menus) {
                sPerfilMenu = pmRepository.save(new PerfilMenu(sPerfil.getIdPerfil(), menu.getIdMenu()));
                permisosMenu = menu.getPermisosMenu();
                if (permisosMenu != null && permisosMenu.size() > 0) {
                    for (PermisoMenu permisoMenu : permisosMenu) {
                        ppmRepository.save(
                                new PerfilPermisoMenu(sPerfilMenu.getIdPerfilMenu(), permisoMenu.getIdPermisoMenu()));
                    }
                }
            }
        }

        sPerfil.setSistema(sistema);
        sPerfil.setIdSistema(perfil.getIdSistema());
        sPerfil.setNombre(perfil.getNombre());
        sPerfil.setDescripcion(perfil.getDescripcion());
        sPerfil.setEstatus(perfil.getEstatus());

        sPerfil = pRepository.save(sPerfil);

        // logsApiComponent.log(ID_MENU, Accion.MODIFICAR, sPerfil.toJson(), hPerfil);

        return ResponseEntity.ok().body(sPerfil);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePerfil(@PathVariable int id) {
        Perfil sPerfil = pRepository.findById(id);
        if (sPerfil == null) {
            String errorMessage = MessageFormat.format("Perfil con id={0} no existe", id);
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessage));
        }

        final List<PerfilMenu> perfilMenus = pmRepository.findByIdPerfil(id);
        if (perfilMenus != null && perfilMenus.size() > 0) {
            final List<Integer> ids = perfilMenus.stream().map((pm) -> pm.getIdPerfilMenu())
                    .collect(Collectors.toList());
            if (ids != null && ids.size() > 0) {
                ppmRepository.deleteWithIdsPerfilMenu(ids);
            }
            pmRepository.deleteAll(perfilMenus);
        }

        pRepository.deleteById(id);
        // logsApiComponent.log(ID_MENU, Accion.ELIMINAR, null, sPerfil.toJson());

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<?> getPerfiles(HttpServletRequest request,
            @RequestParam(name = "ids", required = false) Integer idSistema,
            @RequestParam(name = "n", required = false) String nombre,
            @RequestParam(name = "e", required = false) Integer estatus,
            @RequestParam(name = "p", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "ps", required = false, defaultValue = "10") Integer pageSize) {

        String authorization = request.getHeader("Authorization");
        String token = authorization.substring("Bearer ".length()).trim();
        Boolean isRootUser = Boolean.parseBoolean(jwtService.getJwtLoginTokenClaims(token).get("ir").toString());
        if (isRootUser == true)
            isRootUser = null;

        Sistema sistema = null;
        if (idSistema != null) {
            sistema = new Sistema();
            sistema.setIdSistema(idSistema);
        }
        Perfil perfil = Perfil.builder().isRoot(isRootUser).nombre(nombre).estatus(estatus).sistema(sistema).build();
        Example<Perfil> example = Example.of(perfil, ExampleMatcher.matchingAll().withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("idPerfil"));
        Page<Perfil> perfiles = pRepository.findAll(example, pageable);

        for (Perfil p : perfiles) {
            p.setNumeroUsuarios(uRepository.countByPerfil(p));
        }

        return ResponseEntity.ok().body(perfiles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPerfil(@PathVariable(required = true) int id) {

        Perfil sPerfil = pRepository.findById(id);
        if (sPerfil == null) {
            String errorMessage = MessageFormat.format("Perfil con id={0} no existe", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND, errorMessage));
        }

        sPerfil.setMenus(obtenerMenusPerfil(sPerfil.getIdPerfil()));

        return ResponseEntity.ok().body(sPerfil);
    }

    private List<Menu> obtenerMenusPerfil(int idPerfil) {
        List<PermisoMenu> permisos;
        final List<PerfilMenu> perfilMenus = pmRepository.findByIdPerfil(idPerfil);
        final List<Integer> idsMenus = perfilMenus.stream().map((pm) -> pm.getIdMenu()).collect(Collectors.toList());

        final List<Menu> menus = (List<Menu>) mRepository.findAllById(idsMenus);

        Collections.sort(menus, (d1, d2) -> d1.getOrden() - d2.getOrden());

        final List<PerfilPermisoMenu> ppmsAll = ppmRepository.findAll();
        final List<PermisoMenu> permisosMenus = (List<PermisoMenu>) pMenuRepository.findAll();

        final List<TipoPermisoMenu> tpms = (List<TipoPermisoMenu>) tpmRepository.findAll();

        for (PerfilMenu pm : perfilMenus) {
            permisos = new ArrayList<PermisoMenu>();
            final List<PerfilPermisoMenu> ppms = ppmsAll.stream()
                    .filter((item) -> item.getIdPerfilMenu().intValue() == pm.getIdPerfilMenu().intValue())
                    .collect(Collectors.toList());
            for (PerfilPermisoMenu ppm : ppms) {
                final PermisoMenu permisoMenu = permisosMenus.stream()
                        .filter((item) -> item.getIdPermisoMenu().intValue() == ppm.getIdPermisoMenu().intValue())
                        .findFirst().orElse(null);
                if (permisoMenu != null) {
                    final TipoPermisoMenu tpm = tpms.stream().filter(
                            (item) -> item.getIdTipoPermiso().intValue() == permisoMenu.getTipoPermisoMenu().getIdTipoPermiso().intValue())
                            .findFirst().orElse(null);
                    permisoMenu.setTipoPermisoMenu(tpm);
                    permisos.add(permisoMenu);
                }
            }
            final Menu menu = menus.stream().filter((m) -> m.getIdMenu() == pm.getIdMenu()).findFirst().orElse(null);
            if (menu != null) {
                Collections.sort(permisos, (d1, d2) -> d1.getIdPermisoMenu() - d2.getIdPermisoMenu());
                menu.setPermisosMenu(permisos);
            }
        }

        List<Menu> menusJerarquizados = Utils.jerarquizarMenus(menus);

        return menusJerarquizados;
    }

}