/*
 * Copyright (c) 2014, wayerr (radiofun@ya.ru).
 *
 *     This file is part of talkeeg.
 *
 *     talkeeg is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     talkeeg is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with talkeeg.  If not, see <http://www.gnu.org/licenses/>.
 */

package talkeeg.bf;

/**
 * Factory for objects (messages) mapped to {@link talkeeg.bf.EntryType#STRUCT }
 * Created by wayerr on 17.11.14.
 */
public interface StructureBuilder {
    /**
     * set structure property to mapped object
     * @param name
     * @param value
     */
    void set(String name, Object value) throws Exception;

    /**
     * create instance of structure with previously specified property values
     * @return
     */
    Object create() throws Exception;
}
