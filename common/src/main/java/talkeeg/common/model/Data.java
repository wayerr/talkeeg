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

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import talkeeg.bf.BinaryData;
import talkeeg.bf.StructInfo;
import talkeeg.bf.StructureBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * a structure which represent abstract data
 *
 * Created by wayerr on 16.12.14.
 */
@StructInfo(id = 18)
public final class Data {

    public static final Supplier<StructureBuilder> STRUCT_BUILDER_FACTORY = new Supplier<StructureBuilder>() {
        @Override
        public StructureBuilder get() {
            return new ImmutableStructureBuilder(new Builder());
        }
    };

    public static final class Builder implements BuilderInterface {
        private String action;
        private final Map<String, Object> attrs = new HashMap<>();
        private BinaryData data;

        public String getAction() {
            return action;
        }

        public Builder action(String action) {
            setAction(action);
            return this;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public Builder attrs(Map<String, Object> map) {
            setAttrs(map);
            return this;
        }

        public void setAttrs(Map<String, Object> map) {
            this.attrs.clear();
            this.attrs.putAll(map);
        }

        public Map<String, Object> getAttrs() {
            return attrs;
        }

        public BinaryData getData() {
            return data;
        }

        public Builder data(BinaryData data) {
            setData(data);
            return this;
        }

        public void setData(BinaryData data) {
            this.data = data;
        }

        @Override
        public Data build() {
            return new Data(this);
        }
    }

    private final String action;
    private final Map<String, Object> attrs;
    private final BinaryData data;

    private Data(Builder b) {
        this.action = b.action;
        this.attrs = ImmutableMap.copyOf(b.attrs);
        this.data = b.data;
    }

    public String getAction() {
        return action;
    }

    public Map<String, Object> getAttrs() {
        return attrs;
    }

    public BinaryData getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof Data)) {
            return false;
        }

        Data data1 = (Data)o;

        if(action != null? !action.equals(data1.action) : data1.action != null) {
            return false;
        }
        if(attrs != null? !attrs.equals(data1.attrs) : data1.attrs != null) {
            return false;
        }
        if(data != null? !data.equals(data1.data) : data1.data != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = action != null? action.hashCode() : 0;
        result = 31 * result + (attrs != null? attrs.hashCode() : 0);
        result = 31 * result + (data != null? data.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Data{" +
          "action='" + action + '\'' +
          ", attrs=" + attrs +
          ", data=" + data +
          '}';
    }
}
