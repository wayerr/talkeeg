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

import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * utils for {@link Closeable}
 *
 * Created by wayerr on 10.12.14.
 */
public final class Closeables {

    public static final Logger LOG = Logger.getLogger(Closeables.class.getName());

    public static final void close(Closeable closeable) {
        if(closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch(IOException e) {
            LOG.log(Level.SEVERE, "at closing", e);
        }
    }
}
