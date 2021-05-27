package com.geoit.mrm.users.api.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.geoit.mrm.base.entities.ErrorResponse;
import com.geoit.mrm.base.models.Perfil;
import com.geoit.mrm.base.models.Sistema;
import com.geoit.mrm.base.models.Usuario;
import com.geoit.mrm.users.api.Utils.Utils;
import com.geoit.mrm.users.api.models.ConfirmacionToken;
import com.geoit.mrm.users.api.repositories.PerfilesRepository;
import com.geoit.mrm.users.api.repositories.UsuariosRepository;
import com.geoit.mrm.users.api.services.JwtTokenService;
import com.geoit.mrm.users.api.services.NotificacionsApiService;
import com.geoit.mrm.users.api.services.SecurityApiService;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/usuarios")
public class UsuariosController {
    private final String AUTH_HEADER_PREFIX = "Authorization";
    private final String AUTH_JWT_PREFIX = "Bearer ";

    @Value("${mrm.resources.path}")
    private String pathResources;

    @Value("${mrm.resources.url}")
    private String urlResources;

    @Value("${new.account.email.subject}")
    private String subject;

    @Value("${new.account.email.fromTitle}")
    private String fromTitle;

    @Value("${remtys.app.url}")
    private String appUrl;

    @Value("${new.account.email.nombre-sistema}")
    private String nombreSistema;

    @Value("classpath:/templates/registro.html")
    private Resource registroHtmlTemplate;

    @Autowired
    private UsuariosRepository uRepository;
    @Autowired
    private PerfilesRepository pRepository;

    @Autowired
    private NotificacionsApiService notificacionsApiService;

    @Autowired
    private SecurityApiService securityApiService;

    @Autowired
    private JwtTokenService jwtService;

    @PostMapping
    public ResponseEntity<?> postUsuarios(@Valid @RequestBody Usuario usuario,
            @RequestHeader(AUTH_HEADER_PREFIX) String auth) {
        String nombreUsuario = usuario.getNombreUsuario();

        Usuario sUsuario = uRepository.findByNombreUsuario(nombreUsuario);

        if (sUsuario != null) {
            String errorMessage = MessageFormat.format("El usuario con nombreUsuario={0} ya existe", nombreUsuario);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessage, "usuario_ya_existe"));
        }

        String correo = usuario.getCorreo();
        sUsuario = uRepository.findByCorreo(correo);
        if (sUsuario != null) {
            String errorMessage = MessageFormat.format("El usuario con correo={0} ya existe", correo);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessage, "usuario_ya_existe_correo"));
        }

        Integer idPerfil = usuario.getIdPerfil();
        Perfil sPerfil = pRepository.findById(idPerfil).orElse(null);
        if (sPerfil == null) {
            String errorMessage = MessageFormat.format("El perfil con idPerfil={0} no existe", idPerfil);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessage, "perfil_no_existe"));
        }

        usuario.setPerfil(sPerfil);
        usuario.setIdPerfil(sPerfil.getIdPerfil());
        usuario.setIsRoot(false);
        usuario = uRepository.save(usuario);

        // lApiComponent.log(ID_MENU, Accion.AGREGAR, usuario.toJson(), null);
        enviarNotificacionNuevaCuenta(usuario.getCorreo(), usuario.getIdUsuario(), usuario.getFullname(), auth);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    public void enviarNotificacionNuevaCuenta(String email, long idUsuario, String nombreUsuario, String jwt) {
        ConfirmacionToken ct = securityApiService.obtenerNuevoUsuarioConfirmacionToken(idUsuario);
        if (ct != null) {
            final String url = appUrl + "/crear-cuenta?t=" + ct.getTokenConfirmacion();
            String content = Utils.resourceToString(registroHtmlTemplate);
            content = content.replace("[NOMBRE_COMPLETO_USUARIO]", nombreUsuario);
            content = content.replace("[URL_REGISTRO]", url);
            content = content.replace("[URL_LOGIN]", appUrl + "/login");
            content = content.replace("[CORREO_USUARIO]", email);
            content = content.replace("[SISTEMA]", nombreSistema);
            notificacionsApiService.sendMail(email, subject, fromTitle, content, jwt, true);
        }
    }

    @PutMapping("/{idUsuario}")
    public ResponseEntity<?> putUsuarios(@PathVariable(required = true) Long idUsuario, @RequestBody Usuario usuario) {
        String errorMessage = null;
        Usuario sUsuario = uRepository.findById(idUsuario).orElse(null);
        if (sUsuario == null) {
            errorMessage = MessageFormat.format("Usuario {0} no existe", idUsuario);
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessage));
        }

        String nombrePropio = usuario.getNombrePropio();
        if (nombrePropio != null && nombrePropio.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST, "El nombrePropio no puede estar vacio"));
        }

        String apellidoPaterno = usuario.getApellidoPaterno();
        if (apellidoPaterno != null && apellidoPaterno.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST, "El apellidoPaterno no puede estar vacio"));
        }

        String apellidoMaterno = usuario.getApellidoMaterno();
        if (apellidoMaterno != null && apellidoMaterno.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST, "El apellidoMaterno no puede estar vacio"));
        }

        String nombreUsuario = usuario.getNombreUsuario();
        if (nombreUsuario != null && nombreUsuario.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST, "El nombreUsuario no puede estar vacio"));
        }

        Usuario tUsuario = uRepository.findByNombreUsuario(nombreUsuario);

        if (tUsuario != null && tUsuario.getIdUsuario() != sUsuario.getIdUsuario()) {
            errorMessage = MessageFormat.format("Usuario con nombreUsuario={0} ya existe", nombreUsuario);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessage, "usuario_ya_existe"));
        }

        String correo = usuario.getCorreo();
        tUsuario = uRepository.findByCorreo(correo);
        if (tUsuario != null && tUsuario.getIdUsuario() != sUsuario.getIdUsuario()) {
            errorMessage = MessageFormat.format("El usuario con correo={0} ya existe", correo);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessage, "usuario_ya_existe_correo"));
        }

        Integer idPerfil = usuario.getIdPerfil();
        if (idPerfil != null) {
            Perfil sPerfil = pRepository.findById(idPerfil).orElse(null);
            if (sPerfil == null) {
                errorMessage = MessageFormat.format("Perfil {0} no existe", idPerfil);
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessage, "perfil_no_existe"));
            }
            sUsuario.setPerfil(sPerfil);
            sUsuario.setIdPerfil(sPerfil.getIdPerfil());
        }

        // String hUsuario = sUsuario.toJson();

        if (nombrePropio != null)
            sUsuario.setNombrePropio(nombrePropio);
        if (apellidoPaterno != null)
            sUsuario.setApellidoPaterno(apellidoPaterno);
        if (apellidoMaterno != null)
            sUsuario.setApellidoMaterno(apellidoMaterno);
        if (nombreUsuario != null)
            sUsuario.setNombreUsuario(nombreUsuario);

        if (usuario.getCorreo() != null)
            sUsuario.setCorreo(usuario.getCorreo());
        if (usuario.getCelular() != null)
            sUsuario.setCelular(usuario.getCelular());
        if (usuario.getUrlFoto() != null)
            sUsuario.setUrlFoto(usuario.getUrlFoto());
        if (usuario.getEstatus() != null)
            sUsuario.setEstatus(usuario.getEstatus());
        if (usuario.getFechaUltimoAcceso() != null)
            sUsuario.setFechaUltimoAcceso(usuario.getFechaUltimoAcceso());

        sUsuario.setIdPerfil(sUsuario.getPerfil().getIdPerfil());
        sUsuario = uRepository.save(sUsuario);

        // lApiComponent.log(ID_MENU, Accion.MODIFICAR, sUsuario.toJson(), hUsuario);

        return ResponseEntity.ok().body(sUsuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable Long id) {
        String errorMessage = null;
        Usuario sUsuario = uRepository.findById(id).orElse(null);
        if (sUsuario == null) {
            errorMessage = MessageFormat.format("Usuario con id={0} no existe", id);
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessage));
        }

        if (sUsuario.getIsRoot()) {
            errorMessage = MessageFormat.format("Usuario root no se puede eliminar", "");
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessage));
        }
        uRepository.deleteById(id);
        // lApiComponent.log(ID_MENU, Accion.ELIMINAR, null, sUsuario.toJson());

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<?> getUsuarios(HttpServletRequest request,
            @RequestParam(name = "ids", required = false) Integer idSistema,
            @RequestParam(name = "idp", required = false) Integer idPerfil,
            @RequestParam(name = "np", required = false) String nombrePropio,
            @RequestParam(name = "ap", required = false) String apellidoPaterno,
            @RequestParam(name = "am", required = false) String apellidoMaterno,
            @RequestParam(name = "nu", required = false) String nombreUsuario,
            @RequestParam(name = "c", required = false) String correo,
            @RequestParam(name = "e", required = false) Integer estatus,
            @RequestParam(name = "p", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "ps", required = false, defaultValue = "10") Integer pageSize) {

        String authorization = request.getHeader("Authorization");
        String token = authorization.substring("Bearer ".length()).trim();
        Boolean isRootUser = Boolean.parseBoolean(jwtService.getJwtLoginTokenClaims(token).get("ir").toString());
        if (isRootUser)
            isRootUser = null;
        Perfil perfil = null;
        if (idPerfil != null) {
            perfil = new Perfil();
            perfil.setIdPerfil(idPerfil);
        } else

        if (idSistema != null) {
            perfil = new Perfil();
            Sistema sistema = new Sistema();
            sistema.setIdSistema(idSistema);
            perfil.setSistema(sistema);
        }

        Usuario usuario = Usuario.builder().perfil(perfil).nombrePropio(nombrePropio).isRoot(isRootUser)
                .apellidoPaterno(apellidoPaterno).apellidoMaterno(apellidoMaterno).nombreUsuario(nombreUsuario)
                .correo(correo).estatus(estatus).build();
        Example<Usuario> example = Example.of(usuario, ExampleMatcher.matchingAll().withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("nombrePropio"));
        Page<Usuario> usuarios = uRepository.findAll(example, pageable);
        return ResponseEntity.ok().body(usuarios);
    }

    @GetMapping("/{id}")
    private ResponseEntity<?> getUsuario(@PathVariable(required = true) Long id) {
        Usuario sUsuario = uRepository.findById(id).orElse(null);

        if (sUsuario == null) {
            String errorMessage = MessageFormat.format("Usuario con id={0} no existe", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND, errorMessage));
        }

        return ResponseEntity.ok().body(sUsuario);
    }

    @PostMapping("/{id}/foto")
    public ResponseEntity<?> postUsuarioFoto(@PathVariable(required = true) Long id,
            @RequestParam(name = "file", required = true) MultipartFile file) {
        Usuario sUsuario = uRepository.findById(id).orElse(null);

        if (file.getOriginalFilename().isEmpty() || file.getSize() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND, "Especifique un archivo válido"));
        }

        if (sUsuario == null) {
            String errorMessage = MessageFormat.format("Usuario {0} no existe", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND, errorMessage));
        }

        String folderName = "perfil";
        String path = pathResources + folderName + File.separator;
        String fileName = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
        String url = urlResources + folderName + "/" + fileName;
        try {
            Path p = Paths.get(path);
            if (!Files.exists(p)) {
                Files.createDirectories(p);
            }

            Files.copy(file.getInputStream(), Paths.get(path + fileName), StandardCopyOption.REPLACE_EXISTING);
            sUsuario.setUrlFoto(url);
            sUsuario.setIdPerfil(sUsuario.getPerfil().getIdPerfil());
            sUsuario = uRepository.save(sUsuario);
            return ResponseEntity.ok().body(sUsuario);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    @GetMapping("/search")
    private ResponseEntity<?> getUsuarioByUserNameOrEmail(
            @RequestParam(required = true, name = "username") String username) {
        Usuario sUsuario = uRepository.findByNombreUsuarioOrCorreo(username, username);
        return ResponseEntity.ok().body(sUsuario);
    }

}