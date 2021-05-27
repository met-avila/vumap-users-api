package com.geoit.mrm.users.api.Utils;

import java.util.ArrayList;
import java.util.List;

import com.geoit.mrm.base.models.Menu;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

public class Utils {

    public static List<Menu> jerarquizarMenus(List<Menu> items) {
        List<Menu> menus = new ArrayList<Menu>();
        for (Menu menu : items) {
            if (menu.getIdMenuPadre() == null) {
                menu.setMenus(obtenerSubmenus(menu.getIdMenu(), items));
                menus.add(menu);
            }
        }
        return menus;
    }

    private static List<Menu> obtenerSubmenus(int idMenuPadre, List<Menu> menus) {
        List<Menu> submenus = new ArrayList<Menu>();
        for (Menu menu : menus) {
            final Integer menuPadre = menu.getIdMenuPadre();
            if (menuPadre != null && menuPadre.intValue() == idMenuPadre) {
                menu.setMenus(obtenerSubmenus(menu.getIdMenu(), menus));
                submenus.add(menu);
            }
        }
        return submenus;
    }

    public static String resourceToString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), "UTF-8")) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            return "";
        }
    }
}