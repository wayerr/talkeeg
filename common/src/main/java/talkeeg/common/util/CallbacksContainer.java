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

import java.util.Arrays;

/**
 * unordered container for callbacks <p/>
 * thread safe
 * Created by wayerr on 18.12.14.
 */
public final class CallbacksContainer<E> implements Callback<E> {

    private final Object lock = new Object();
    private Callback<E> callbacks[];

    public CallbacksContainer() {

    }

    /**
     * add callback, multiple invocation of this method on one argument will add only single record
     * @param callback
     */
    public void add(Callback<E> callback) {
        if(callback == null) {
            throw new IllegalArgumentException("callback is null");
        }
        synchronized(this.lock) {
            if(this.callbacks == null) {
                this.callbacks = new Callback[]{callback};
            } else {
                for(Callback<E> old : this.callbacks) {
                    if(old == callback) {
                        return;
                    }
                }
                final int end = this.callbacks.length;
                this.callbacks = Arrays.copyOf(this.callbacks, end + 1);
                this.callbacks[end] = callback;
            }
        }
    }

    /**
     * remove callback <p/>
     * note than on removing callbacks will be shuffled
     * @param callback
     */
    public void remove(Callback<E> callback) {
        if(callback == null) {
            throw new IllegalArgumentException("callback is null");
        }
        synchronized(this.lock) {
            if(this.callbacks == null) {
                return;
            }
            for(int i = 0; i < this.callbacks.length; ++i) {
                if(this.callbacks[i] == callback) {
                    final int end = this.callbacks.length - 1;
                    if(end <= 0) {
                        this.callbacks = null;
                        break;
                    }
                    final Callback<E> last = this.callbacks[end];
                    this.callbacks = Arrays.copyOf(this.callbacks, end - 1);
                    this.callbacks[this.callbacks.length - 1] = last;
                    --i;
                }
            }
        }
    }

    /**
     * invoke {@link talkeeg.common.util.Callback#call(Object)} for each stored callback in unspecified order
     * @param value
     */
    @Override
    public void call(E value) {
        Callback<E>[] copy;
        synchronized(this.lock) {
            copy = this.callbacks;
        }
        if(copy == null) {
            return;
        }
        for(int i = 0; i < copy.length; ++i) {
            copy[i].call(value);
        }
    }

    /**
     * count of stored callbacks
     * @return
     */
    public int size() {
        synchronized(this.lock) {
            if(this.callbacks == null) {
                return 0;
            }
            return this.callbacks.length;
        }
    }
}
