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

import java.io.InputStream;
import java.util.List;

/**
 * virtual file representation <p/>
 *
 * Created by wayerr on 30.01.15.
 */
public interface VirtualFile {
    public String getName() throws Exception;
    public long getSize() throws Exception;

    /**
     * return mime type string of file
     * @return
     */
    public String getMimeType() throws Exception;
    public boolean isDirectory() throws Exception;

    /**
     * list child elements of 'element'.
     */
    void getChilds(List<VirtualFile> childs) throws Exception;

    /**
     * retrieve parent of element.
     * @return parent or null if this element placed in root
     */
    VirtualFile getParent() throws Exception;

    InputStream openInputStream() throws Exception;
}
