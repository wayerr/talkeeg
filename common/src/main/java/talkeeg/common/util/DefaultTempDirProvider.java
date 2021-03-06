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

import talkeeg.common.conf.Config;
import talkeeg.common.core.CacheDirsService;

import java.io.File;

/**
 * default provider of temp directory
 *
 * Created by wayerr on 10.12.14.
 */
public final class DefaultTempDirProvider implements CacheDirsService.DirectoryProvider {

    private final String appName;
    private final File file;

    public DefaultTempDirProvider(Config config) {
        this.appName = config.getApplicationName();
        this.file = new File(System.getProperty("java.io.tmpdir"), appName);
    }

    @Override
    public File getDirectory() {
        return file;
    }

    @Override
    public void clear() {
        Fs.delete(this.file);
    }
}
