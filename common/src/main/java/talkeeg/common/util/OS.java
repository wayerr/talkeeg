/*
 * Copyright (c) 2014, wayerr (radiofun@ya.ru).
 *
 *      This file is part of talkeeg-parent.
 *
 *      talkeeg-parent is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      talkeeg-parent is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with talkeeg-parent.  If not, see <http://www.gnu.org/licenses/>.
 */

package talkeeg.common.util;

import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * utility for OS specific things
 *
 * Created by wayerr on 27.11.14.
 */
public final class OS {
    /**
     * operating system family
     */
    public static enum Family {
        WINDOWS, LINUX, MAC, UNKNOWN, ANDROID
    }

    private static final OS OS = new OS();
    private final String userHome;
    private final Family family;

    private OS() {
        this.userHome = System.getProperty("user.home");
        this.family = detectOsFamily();
    }

    private static Family detectOsFamily() {
        final String kernelName = System.getProperty("os.name").toLowerCase();
        final Family family;
        if(kernelName.contains("linux")) {
            final String vmname = System.getProperty("java.vm.vendor").toLowerCase();
            if(vmname.contains("android")) {
                family = Family.ANDROID;
            } else {
                family = Family.LINUX;
            }
        } else if(kernelName.contains("win")) {
            family = Family.WINDOWS;
        } else if(kernelName.contains("mac")) {
            family = Family.MAC;
        } else {
            family = Family.UNKNOWN;
        }
        return family;
    }

    /**
     * instance of this tool
     * @return
     */
    public static OS getInstance() {
        return OS;
    }

    /**
     * user home dir, may be null (on android)
     * @return
     */
    public String getUserHome() {
        return userHome;
    }

    /**
     * hostname of current machine
     * @return
     */
    public String getHostName() {
        String name = null;
        try {
            name = InetAddress.getLocalHost().getHostName();
        } catch(Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "", e);
        }
        if(name == null) {
            if(family == Family.WINDOWS) {
                name = System.getenv("COMPUTERNAME");
            } else if(family == Family.LINUX) {
                name = System.getenv("HOSTNAME");
                if(name == null) {
                    name = System.getenv("HOST");
                }

            }
        }
        // obliviously, we don`t need names like `localhost`
        if(name != null && (name.equalsIgnoreCase("localhost") || name.equalsIgnoreCase("localhost.localdomain"))) {
            name = null;
        }
        return name;
    }

    /**
     * operting system family
     * @return
     */
    public Family getFamily() {
        return family;
    }
}
