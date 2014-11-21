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

package talkeeg.common.model;

import talkeeg.common.util.Int128;

/**
 * Created by wayerr on 21.11.14.
 */
public abstract class AbstractSingleMessage {

    /**
     * `id(T02)`:  циклический идентификатор (используется для фильтрации дублей, уникален для каждого src)
     */
    protected final short id;
    /**
     * `scr(T16)`: отпечаток CPubK клиента отправителя
     */
    protected final Int128 src;
    /**
     *  `dst(T16)`: отпечаток CPubK клиента адресата
     */
    protected final Int128 dst;

    public AbstractSingleMessage(short id, Int128 src, Int128 dst) {
        this.id = id;
        this.src = src;
        this.dst = dst;
    }

    public Int128 getDst() {
        return dst;
    }

    public short getId() {
        return id;
    }

    public Int128 getSrc() {
        return src;
    }
}
