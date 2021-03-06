/*
 * Copyright (c) 2015, wayerr (radiofun@ya.ru).
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

package talkeeg.httpserver.fs;

/**
 * virtual file system <p/>
 * Created by wayerr on 30.01.15.
 */
public interface VirtualFileSystem<T extends VirtualFile> {

    /**
     * retrieve file by relatively this VFS root file name
     * @param name
     * @return
     */
    T fromPath(String name) throws Exception ;

    /**
     * get relatively this VFS root file name
     * @param childFile
     * @return
     */
    String toPath(T childFile) throws Exception;
}
