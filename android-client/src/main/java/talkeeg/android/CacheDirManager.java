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

import android.content.Context;
import talkeeg.common.util.Fs;

import java.io.File;
import java.util.Random;

/**
 * tool for managing by cache and tmp dirs
 *
 * Created by wayerr on 05.12.14.
 */
public final class CacheDirManager {

    private static final String DIR_TMP = "tmp";
    private static final String RND_SRC = "1234567890QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm";

    private final Random random;

    private final Context context;

    CacheDirManager(Context context) {
        this.context = context;
        this.random = new Random();
    }

    /**
     * create temporary file in `context.getCacheDir`/tmp/ dir <p/>
     * file can be deleted at exit by invoking {@link }
     * @param prefix
     * @return
     */
    public File createTempFile(String prefix) {
        File dirTmp = getTmpDir();
        dirTmp.mkdirs();

        StringBuilder fileName = new StringBuilder(prefix);
        if(!prefix.endsWith("-")) {
            fileName.append('-');
        }
        fileName.append(getRandomString());
        fileName.append(".tmp");
        File file = new File(dirTmp, fileName.toString());
        return file;
    }

    private File getTmpDir() {
        final File cacheDir = getCacheDir();
        final File file = new File(cacheDir, DIR_TMP);
        return file;
    }

    private File getCacheDir() {
        File cacheDir = this.context.getExternalCacheDir();
        if(cacheDir == null) {
            cacheDir = this.context.getCacheDir();
        }
        return cacheDir;
    }

    /**
     * remove temp files
     */
    public void clearTmp() {
        File cacheExtDir = this.context.getExternalCacheDir();
        Fs.delete(cacheExtDir);
        File cacheDir = this.context.getCacheDir();
        Fs.delete(cacheDir);
    }

    char[] getRandomString() {
        char rs[] = new char[10];
        int length = RND_SRC.length();
        for(int i = 0; i < rs.length; ++i) {
            int value = Math.abs(random.nextInt() % length);
            rs[i] = RND_SRC.charAt(value % length);
        }
        return rs;
    }
}
