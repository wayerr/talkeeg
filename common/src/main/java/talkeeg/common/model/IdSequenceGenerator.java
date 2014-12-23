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

package talkeeg.common.model;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * thread safe generator of sequence ids
 * Created by wayerr on 23.12.14.
 */
public final class IdSequenceGenerator {

    private final AtomicInteger idGenerator = new AtomicInteger();

    private final int maxValue;

    public IdSequenceGenerator(int maxValue) {
        this.maxValue = maxValue;
    }

    public static IdSequenceGenerator shortIdGenerator() {
        return new IdSequenceGenerator(Short.MAX_VALUE);
    }

    public short next() {
        int nextId;
        while(true) {
            nextId = this.idGenerator.getAndIncrement();
            if(nextId > maxValue) {
                if(this.idGenerator.compareAndSet(nextId, 0)) {
                    nextId = 0;
                    break;
                }
            }
            break;
        }
        return (short)nextId;
    }
}
