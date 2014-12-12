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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * default implementation of message bus
 * Created by wayerr on 12.12.14.
 */
final class MessageBusImpl<T> implements MessageBus<T> {

    private final MessageBusKey<T> key;
    private final Listener<UncaughtExceptionEvent> exceptionHandler;
    private final List<Listener<? super T>> listenerList = new ArrayList<>();

    MessageBusImpl(MessageBusKey<T> key, Listener<UncaughtExceptionEvent> exceptionHandler) {
        this.key = key;
        if(this.key == null) {
            throw new NullPointerException("id is null");
        }
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public MessageBusKey<T> getKey() {
        return this.key;
    }

    @Override
    public void listen(T event) {
        //check input event type
        this.key.check(event);
        synchronized(listenerList) {
            final int size = listenerList.size();
            for(int i = 0; i < size; ++i) {
                final Listener<? super T> listener = listenerList.get(i);
                try {
                    listener.listen(event);
                } catch(InterruptedException e) {
                    Thread.currentThread().interrupt();
                    //execution must be interrupted
                    return;
                } catch(Exception e) {
                    processException(event, listener, e);
                }
            }
        }
    }

    protected void processException(T event, Listener<? super T> listener, Exception e) {
        final UncaughtExceptionEvent exceptionEvent = new UncaughtExceptionEvent(this, event, listener, e);
        if(this.exceptionHandler != null) {
            try {
                this.exceptionHandler.listen(exceptionEvent);
            } catch(Exception subException) {
                final Logger logger = getLogger();
                logger.log(Level.SEVERE, "exceptionHandler fail process uncaught exception:", e);
                logger.log(Level.SEVERE, "exception from exceptionHandler", subException);
            }
        } else {
            //
            final Logger logger = getLogger();
            logger.log(Level.SEVERE, "uncaught exception on listener: " + listener + " and event: " + event, e);
        }
    }

    private Logger getLogger() {
        //initialization of logger is blocking operation, therefore we don't init it without needing
        return Logger.getLogger(getClass().getName());
    }

    @Override
    public void register(Listener<? super T> listener) {
        synchronized(listenerList) {
            // in array list contains method used a Object.equals , but we need check identity
            final int size = listenerList.size();
            for(int i = 0; i < size; ++i) {
                Listener<? super T> oldListener = listenerList.get(i);
                if(oldListener == listener) {
                    return;
                }
            }
            listenerList.add(listener);
        }
    }

    @Override
    public void unregister(Listener<? super T> listener) {
        synchronized(listenerList) {
            // in array list contains method used a Object.equals , but we need check identity
            for(int i = 0; i < listenerList.size(); ++i) {
                Listener<? super T> oldListener = listenerList.get(i);
                if(oldListener == listener) {
                    listenerList.remove(i);
                    --i;
                }
            }
        }
    }
}
