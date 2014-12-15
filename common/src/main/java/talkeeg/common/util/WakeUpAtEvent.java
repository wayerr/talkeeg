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

import dagger.ObjectGraph;
import talkeeg.mb.Listener;

/**
 * service which wake up specified class from object graph at event
 * Created by wayerr on 15.12.14.
 */
public final class WakeUpAtEvent<T> implements Listener<T> {
    private final Class<?> clazz;
    private final ObjectGraph graph;
    private final Object lock = new Object();
    private volatile Object instance;

    public WakeUpAtEvent(ObjectGraph graph, Class<?> clazz) {
        this.clazz = clazz;
        this.graph = graph;
    }

    @Override
    public void listen(T event) throws Exception {
        if(this.instance == null) {
            synchronized(lock) {
                if(this.instance == null) {
                    this.instance = this.graph.get(this.clazz);
                }
            }
        }
    }

}
