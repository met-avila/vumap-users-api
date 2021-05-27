--sistemas
insert into sistemas(id_sistema,nombre) values 
(1,'Administración'),
(2,'Remtys'),
(3,'Vumtys');


--perfiles
insert into perfiles(id_perfil,id_sistema, nombre,is_root, estatus,fecha_creacion)values
(1,1,'Administradores',true,1,now()),
(2,1,'Mineria de Datos',false,1,now())
;

--menus
insert into menus(id_menu,id_sistema,nombre,href,id_menu_padre, icono,descripcion)values
(1,1,'Administración','#',null,'mdi-action-account-circle','Gestion del Sistema'),
(2,1,'Perfiles','Perfiles',1,'mdi-social-group','Perfiles'),
(3,1,'Usuarios','Usuarios',1,'mdi-navigation-apps','Usuarios'),
(4,1,'Mineria de Datos','#',null,'mdi-av-web','Mineria de Datos'),
(5,1,'Gestion Reportes','GReportes',4,'mdi-content-forward','Gestion Reportes')
;

--perfil_menus

insert into perfil_menus(id_perfil_menu,id_perfil,id_menu)values
(1,1,1),
(2,1,2),
(3,1,3),
(4,1,4),
(5,1,5),
(6,2,4),
(7,2,5)
;

--tipo_permiso_menu
insert into tipo_permiso_menu(id_tipo_permiso, nombre) values
(1,'Agregar'),
(2,'Modificar'),
(3,'Eliminar');

--permisos_menu

insert into permisos_menu(id_permiso_menu,id_menu,id_tipo_permiso,css_clase)values
(1,2,1,'css_perfiles_agregar'),
(2,2,2,'css_perfiles_modificar'),
(3,2,3,'css_perfiles_eliminar'),

(4,3,1,'css_usuarios_agregar'),
(5,3,2,'css_usuarios_modificar'),
(6,3,3,'css_usuarios_eliminar'),

(7,5,1,'css_reportes_agregar'),
(8,5,2,'css_reportes_modificar'),
(9,5,3,'css_reportes_eliminar')
;

insert into perfil_permisos_menu(id, id_perfil_menu, id_permiso_menu)values
(1,2,1),
(2,2,2);


--tipos_confirmacion_token
insert into tipos_confirmacion_token(id_tipo_confirmacion, nombre)values
(1,'Alta usuario'),
(2,'Cambio de contraseña')
;

--usuarios
insert into usuarios(nombre_propio, apellido_paterno, apellido_materno,nombre_usuario,correo,celular,url_foto,estatus,fecha_creacion,id_perfil, is_root, password) values
('root','root', 'root','root','mtienda@geoit.com.mx','5552144864','root.png',1,now(),-1, true,'sha1:1000:04e84ac91422ff656296dff9f3835d0ca79bcb782ea0778b:2f388e1ec8c99bff1a8851cbf72d2e6f9bb25f42550ee14c');