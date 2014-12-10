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

package talkeeg.common.core;

import talkeeg.common.util.Fs;

import java.io.File;
import java.util.Random;

/**
 * service of cache and temp directories
 *
 * Created by wayerr on 10.12.14.
 */
public final class CacheDirsService {
    public interface DirectoryProvider {
        File getDirectory();
        void clear();
    }

    private static final String RND_SRC = "1234567890QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm";

    private final Random random;
    private final DirectoryProvider tempDirProvider;
    private final DirectoryProvider cacheDirProvider;


    public CacheDirsService(DirectoryProvider tempDirectoryProvider, DirectoryProvider cacheDirectoryProvider) {
        this.random = new Random();
        this.tempDirProvider = tempDirectoryProvider;
        this.cacheDirProvider = cacheDirectoryProvider;
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
        return tempDirProvider.getDirectory();
    }

    private File getCacheDir() {
        return cacheDirProvider.getDirectory();
    }



    /**
     * remove temp files
     */
    public void clearTmp() {
        this.tempDirProvider.clear();
    }

    private char[] getRandomString() {
        char rs[] = new char[10];
        int length = RND_SRC.length();
        for(int i = 0; i < rs.length; ++i) {
            int value = Math.abs(random.nextInt() % length);
            rs[i] = RND_SRC.charAt(value % length);
        }
        return rs;
    }
}
