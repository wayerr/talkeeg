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

import java.util.HashMap;
import java.util.Map;

/**
 * thread safe registry
 * Created by wayerr on 11.12.14.
 */
public final class HandlersRegistry<T> {

    private final class Deregisterer implements Closeable {
        private final String key;
        private final T handler;

        private Deregisterer(String key, T handler) {
            this.key = key;
            this.handler = handler;
        }

        @Override
        public void close() {
            synchronized(HandlersRegistry.this.handlers) {
                final T oldHandler = HandlersRegistry.this.handlers.get(key);
                if(oldHandler == handler) {
                    HandlersRegistry.this.handlers.remove(key);
                }
            }
        }
    }

    private final Map<String, T> handlers = new HashMap<>();

    /**
     * perform a registration of handler to specified key, if specified key already has registration, and registered
     * handler not identity with specified, then exception will be thrown
     * @param key
     * @param handler
     * @return
     */
    public Closeable register(final String key, final T handler) {
        synchronized(this.handlers) {
            T oldHandler = this.handlers.get(key);
            if(oldHandler != null && oldHandler != handler) {
                throw new RuntimeException("can not replace " + oldHandler + " with " + handler + " on key " + key);
            }
            this.handlers.put(key, handler);
            return new Deregisterer(key, handler);
        }
    }

    public T get(String key) {
        synchronized(this.handlers) {
            return this.handlers.get(key);
        }
    }
}
