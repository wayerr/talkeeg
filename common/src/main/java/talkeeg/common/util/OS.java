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
    public static OS getIntance() {
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
     * operting system family
     * @return
     */
    public Family getFamily() {
        return family;
    }
}
