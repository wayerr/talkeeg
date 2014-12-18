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
import talkeeg.bf.StructInfo;
import talkeeg.bf.StructureBuilder;
import talkeeg.common.ipc.IpcEntry;

/**
 * result for command of ipc
 * Created by wayerr on 11.12.14.
 */
@StructInfo(id = 17)
public class CommandResult implements IpcEntry {

    public static final Supplier<StructureBuilder> STRUCT_BUILDER_FACTORY = new Supplier<StructureBuilder>() {
        @Override
        public StructureBuilder get() {
            return new ImmutableStructureBuilder(new Builder());
        }
    };

    public static class Builder implements BuilderInterface {
        private int id;
        private ResponseCode code;
        private Object value;

        public int getId() {
            return id;
        }

        public Builder id(int id) {
            setId(id);
            return this;
        }

        public void setId(int id) {
            this.id = id;
        }

        public ResponseCode getCode() {
            return code;
        }

        public void setCode(ResponseCode code) {
            this.code = code;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public CommandResult build() {
            return new CommandResult(this);
        }
    }

    private final int id;
    private final ResponseCode code;
    private final Object value;

    private CommandResult(Builder b) {
        this.id = b.id;
        this.code = b.code;
        this.value = b.value;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public int getId() {
        return id;
    }

    public ResponseCode getCode() {
        return code;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof CommandResult)) {
            return false;
        }

        CommandResult that = (CommandResult)o;

        if(id != that.id) {
            return false;
        }
        if(code != that.code) {
            return false;
        }
        if(value != null? !value.equals(that.value) : that.value != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (code != null? code.hashCode() : 0);
        result = 31 * result + (value != null? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CommandResult{" +
          "id=" + id +
          ", code=" + code +
          ", value=" + value +
          '}';
    }
}
