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

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import talkeeg.bf.StructInfo;
import talkeeg.bf.StructureBuilder;
import talkeeg.common.ipc.IpcEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * the command of ipc
 * Created by wayerr on 11.12.14.
 */
@StructInfo(id = 16)
public class Command implements IpcEntry {

    public static final Supplier<StructureBuilder> STRUCT_BUILDER_FACTORY = new Supplier<StructureBuilder>() {
        @Override
        public StructureBuilder get() {
            return new ImmutableStructureBuilder(new Builder());
        }
    };

    public static class Builder implements BuilderInterface {
        private short id;
        private short sequenceId;
        private String action;
        private Object arg;

        public short getId() {
            return id;
        }

        public Builder id(short id) {
            setId(id);
            return this;
        }

        public void setId(short id) {
            this.id = id;
        }

        /**
         * id of command sequence
         * @return
         */
        public short getSequenceId() {
            return sequenceId;
        }

        /**
         * id of command sequence
         * @param sequenceId
         * @return
         */
        public Builder sequenceId(short sequenceId) {
            setSequenceId(sequenceId);
            return this;
        }

        /**
         * id of command sequence
         * @param sequenceId
         */
        public void setSequenceId(short sequenceId) {
            this.sequenceId = sequenceId;
        }

        /**
         * action on which is registered handler
         * @see talkeeg.common.ipc.IpcService#addIpcHandler(String, talkeeg.common.ipc.IpcEntryHandler)
         * @return
         */
        public String getAction() {
            return action;
        }

        /**
         * action on which is registered handler
         * @see talkeeg.common.ipc.IpcService#addIpcHandler(String, talkeeg.common.ipc.IpcEntryHandler)
         * @param action
         */
        public Builder action(String action) {
            setAction(action);
            return this;
        }

        /**
         * action on which is registered handler
         * @see talkeeg.common.ipc.IpcService#addIpcHandler(String, talkeeg.common.ipc.IpcEntryHandler)
         * @param action
         */
        public void setAction(String action) {
            this.action = action;
        }

        public Object getArg() {
            return arg;
        }

        public Builder arg(Object arg) {
            setArg(arg);
            return this;
        }

        public void setArg(Object arg) {
            this.arg = arg;
        }

        @Override
        public Command build() {
            return new Command(this);
        }
    }

    private final short id;
    private final short sequenceId;
    private final String action;
    private final Object arg;

    private Command(Builder b) {
        this.id = b.id;
        this.sequenceId = b.sequenceId;
        this.action = b.action;
        Preconditions.checkNotNull(this.action, "action is null");
        this.arg = b.arg;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public short getId() {
        return id;
    }

    @Override
    public short getSequenceId() {
        return sequenceId;
    }

    /**
     * action on which is registered handler
     * @see talkeeg.common.ipc.IpcService#addIpcHandler(String, talkeeg.common.ipc.IpcEntryHandler)
     * @return
     */
    @Override
    public String getAction() {
        return action;
    }

    public Object getArg() {
        return arg;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof Command)) {
            return false;
        }

        Command command = (Command)o;

        if(id != command.id) {
            return false;
        }
        if(action != null? !action.equals(command.action) : command.action != null) {
            return false;
        }
        if(arg != null? !arg.equals(command.arg) : command.arg != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (action != null? action.hashCode() : 0);
        result = 31 * result + (arg != null? arg.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Command{" +
          "id=" + id +
          ", action='" + action + '\'' +
          ", arg=" + arg +
          '}';
    }
}
