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

package talkeeg.android;

import android.app.Application;
import android.content.Context;
import talkeeg.common.core.CacheDirsService;
import talkeeg.common.util.Fs;

import java.io.File;

/**
* Created by wayerr on 10.12.14.
*/
final class AndroidCacheDirectoryProvider implements CacheDirsService.DirectoryProvider {
    private static final String DIR_TMP = "tmp";

    private final Context context;
    private final Application app;

    public AndroidCacheDirectoryProvider(Application app) {
        this.app = app;
        context = app.getApplicationContext();
    }

    @Override
    public File getDirectory() {
        File cacheDir = this.context.getExternalCacheDir();
        if(cacheDir == null) {
            cacheDir = this.context.getCacheDir();
        }
        return cacheDir;
    }

    @Override
    public void clear() {
        Fs.delete(this.context.getExternalCacheDir());
        Fs.delete(this.context.getCacheDir());
    }

    CacheDirsService.DirectoryProvider getTempProvider() {
        return new CacheDirsService.DirectoryProvider() {

            @Override
            public File getDirectory() {
                return new File(getDirectory(), DIR_TMP);
            }

            @Override
            public void clear() {
                clearTempDir(context.getExternalCacheDir());
                clearTempDir(context.getCacheDir());
            }

            protected void clearTempDir(File cacheDir) {
                final File tempDir = new File(cacheDir, DIR_TMP);
                Fs.delete(tempDir);
            }
        };
    }
}
