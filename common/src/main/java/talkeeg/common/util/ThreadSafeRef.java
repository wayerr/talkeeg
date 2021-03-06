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

import com.google.common.base.Preconditions;

/**
 * class like {@link java.util.concurrent.atomic.AtomicReference } but with allowing custom compare operation
 * and {@link #valueChanged(Object, Object)}  on change value hook}. <p/>
 * currently it`s use 'synchronize' blocks, but in future we can rewrite it to unblocking behaviour
 * Created by wayerr on 23.12.14.
 */
public class ThreadSafeRef<T> {

    private final Object lock = new Object();
    private volatile T value;
    private final EqualFunction<T> function;

    private ThreadSafeRef(T value, EqualFunction<T> function) {
        this.value = value;
        Preconditions.checkNotNull(function, "function is null");
        this.function = function;
    }

    /**
     * create ref object with {@link EqualFunctions#functionEquality()}  }
     * @param <T>
     * @return
     */
    public static <T> ThreadSafeRef<T> create() {
        return create(null);
    }

    /**
     * create ref object with {@link EqualFunctions#functionEquality()}  }
     * @param value
     * @param <T>
     * @return
     */
    public static <T> ThreadSafeRef<T> create(T value) {
        return new ThreadSafeRef<>(value, EqualFunctions.<T>functionEquality());
    }

    public static <T> ThreadSafeRef<T> create(T value, EqualFunction<T> function) {
        return new ThreadSafeRef<>(value, function);
    }

    public static <T> ThreadSafeRef<T> createWithCallback(ChangeValueCallback<ThreadSafeRef<T>, T> callback) {
        return new ThreadSafeRefWithCallback<>(null, EqualFunctions.<T>functionEquality(), callback);
    }

    /**
     * get value
     * @return
     */
    public T get() {
        synchronized(this.lock) {
            return this.value;
        }
    }

    /**
     * set new value and return old
     * @param value
     * @return
     */
    public final T set(T value) {
        T old;
        synchronized(this.lock) {
            old = this.value;
            this.value = value;
        }
        if(old != value) {
            valueChanged(old, value);
        }
        return old;
    }

    /**
     * method will be called only if value has been changed, and it can be used for custom actions on value updating, <p/>
     * be careful, method will be executed out of 'synchronized' block
     * @param oldValue
     * @param newValue
     */
    protected void valueChanged(T oldValue, T newValue) {
    }

    /**
     * update value if <p/>
     * <code>this.function.equal(current, expected) == true<code/>
     * @param expected expected value
     * @param value new value
     * @return
     */
    public final boolean compareAndSet(T expected, T value) {
        return compareAndSet(expected, value, this.function);
    }

    /**
     * update value if <p/>
     * <code>function.equal(current, expected) == true<code/>
     * @param expected expected value
     * @param value new value
     * @param function function which used for comparing
     * @return
     */
    public final boolean compareAndSet(T expected, T value, EqualFunction<T> function) {
        final T old;
        final boolean ok;
        synchronized(this.lock) {
            old = this.value;
            if(function.equal(old, expected)) {
                ok = true;
                this.value = value;
            } else {
                ok = false;
            }
        }
        if(ok) {
            valueChanged(old, value);
        }
        return ok;
    }

    private final static class ThreadSafeRefWithCallback<T> extends ThreadSafeRef<T> {
        private final ChangeValueCallback<ThreadSafeRef<T>, T> callback;

        private ThreadSafeRefWithCallback(T value, EqualFunction<T> function, ChangeValueCallback<ThreadSafeRef<T>, T> callback) {
            super(value, function);
            Preconditions.checkNotNull(callback, "callback is null");
            this.callback = callback;
        }

        @Override
        protected void valueChanged(T oldValue, T newValue) {
            this.callback.valueChanged(this, oldValue, newValue);
        }
    }
}
