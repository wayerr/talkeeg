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

/**
 * event which generated on uncaught exception from subscribed from listeners
 * Created by wayerr on 12.12.14.
 */
public final class UncaughtExceptionEvent {
    private final MessageBus<?> bus;
    private final Object event;
    private final Listener<?> listener;
    private final Throwable exception;

    /**
     * a create exception event
     * @param bus bus where exception happened
     * @param event event which cause exception
     * @param listener listened which throw exception
     * @param exception
     */
    public UncaughtExceptionEvent(MessageBus<?> bus, Object event, Listener<?> listener, Throwable exception) {
        this.bus = bus;
        this.event = event;
        this.listener = listener;
        this.exception = exception;
    }

    public MessageBus<?> getBus() {
        return bus;
    }

    public Object getEvent() {
        return event;
    }

    public Listener<?> getListener() {
        return listener;
    }

    public Throwable getException() {
        return exception;
    }
}
