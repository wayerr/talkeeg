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

package talkeeg.common.conf;

import com.google.common.base.Function;
import talkeeg.common.util.OS;

import java.io.File;

/**
 * default implementation for function which give config dir by app name
 * this function support only linux and windows OSes
 * Created by wayerr on 27.11.14.
 */
public final class DefaultConfigDirFunction implements Function<String, File> {

    public static final DefaultConfigDirFunction INSTANCE = new DefaultConfigDirFunction();

    @Override
    public File apply(final String applicationName) {
        final String dirName;
        final char separator = File.separatorChar;
        final OS.Family family = OS.getIntance().getFamily();
        switch(family) {
            case LINUX: {
                //see http://standards.freedesktop.org/basedir-spec/basedir-spec-latest.html
                final String userConfigDir = System.getenv("XDG_CONFIG_HOME");
                if(userConfigDir == null) {
                    final String userHome = System.getProperty("user.home");
                    dirName = userHome + separator + ".config" + separator + applicationName;
                } else {
                    dirName = userConfigDir + separator + applicationName;
                }
            }
            break;
            case WINDOWS: {
                final String userConfigDir = System.getenv("APPDATA");
                dirName = userConfigDir + separator + applicationName;
            }
            break;
            default:
                throw new RuntimeException("Resolve config dir for os family: " + family + " is unsupported");
        }
        final File dir = new File(dirName);
        dir.mkdirs();// path may be not exist
        return dir;
    }
}
