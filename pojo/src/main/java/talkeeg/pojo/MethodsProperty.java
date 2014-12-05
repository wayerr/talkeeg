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

package talkeeg.pojo;

import java.lang.reflect.Method;

/**
 * property represented by getter and setter methods
 *
 * Created by wayerr on 05.12.14.
 */
final class MethodsProperty implements Property {

    static final class Builder {
        private final String name;
        private Method getter;
        private Method setter;

        Builder(String name) {
            this.name = name;
        }

        String getName() {
            return name;
        }

        Method getGetter() {
            return getter;
        }

        void setGetter(Method getter) {
            this.getter = getter;
        }

        Method getSetter() {
            return setter;
        }

        void setSetter(Method setter) {
            this.setter = setter;
        }

        public MethodsProperty build() {
            return new MethodsProperty(this);
        }
    }

    private final String name;
    private final Method getter;
    private final Method setter;

    MethodsProperty(Builder b) {
        this.name = b.name;
        this.getter = b.getter;
        if(this.getter == null) {
            throw new NullPointerException("getter is null");
        }
        this.setter = b.setter;
    }

    static Builder build(String name) {
        return new Builder(name);
    }

    @Override
    public Class<?> getType() {
        return this.getter.getReturnType();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object get(Object owner) {
        try {
            return this.getter.invoke(owner);
        } catch(ReflectiveOperationException e) {
            throw new RuntimeException("call " + this.getter + " on " + owner);
        }
    }

    @Override
    public void set(Object owner, Object value) {
        try {
            this.setter.invoke(owner, value);
        } catch(ReflectiveOperationException e) {
            throw new RuntimeException("call " + this.setter + " on " + owner);
        }
    }
}
