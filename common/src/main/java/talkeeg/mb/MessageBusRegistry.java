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

package talkeeg.mb;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * registry of message buses
 *
 * Created by wayerr on 12.12.14.
 */
public final class MessageBusRegistry {
    private static final String CLASS_NAME = MessageBusRegistry.class.getName();
    public static final MessageBusKey<UncaughtExceptionEvent> BUS_UNCAUGHT_EXCEPTION =
      MessageBusKey.create(CLASS_NAME + ".uncaughtExceptionBus", UncaughtExceptionEvent.class);

    private final ConcurrentMap<MessageBusKey<?>, MessageBus<?>> busesMap = new ConcurrentHashMap<>();
    private final MessageBus<UncaughtExceptionEvent> uncaughtExceptionBus = new MessageBusImpl<>(BUS_UNCAUGHT_EXCEPTION, null);

    public MessageBusRegistry() {
        this.busesMap.put(this.uncaughtExceptionBus.getKey(), this.uncaughtExceptionBus);
    }

    @SuppressWarnings("unchecked")
    public <T> MessageBus<T> getOrCreateBus(final MessageBusKey<T> key) {
        final MessageBus<T> newBus = new MessageBusImpl<>(key, this.uncaughtExceptionBus);
        final MessageBus<T> oldBus = (MessageBus<T>)this.busesMap.putIfAbsent(key, newBus);
        if(oldBus != null) {
            return oldBus;
        }
        return newBus;
    }
}
