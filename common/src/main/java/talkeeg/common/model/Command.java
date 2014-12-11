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
        private int id;
        private String action;
        private final List<Object> args = new ArrayList<>();

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        /**
         * action on which is registered handler
         * @see talkeeg.common.ipc.IpcService#addIpcHandler(String, talkeeg.common.ipc.TgbfHandler)
         * @return
         */
        public String getAction() {
            return action;
        }

        /**
         * action on which is registered handler
         * @see talkeeg.common.ipc.IpcService#addIpcHandler(String, talkeeg.common.ipc.TgbfHandler)
         * @param action
         */
        public Builder action(String action) {
            setAction(action);
            return this;
        }

        /**
         * action on which is registered handler
         * @see talkeeg.common.ipc.IpcService#addIpcHandler(String, talkeeg.common.ipc.TgbfHandler)
         * @param action
         */
        public void setAction(String action) {
            this.action = action;
        }

        public List<Object> getArgs() {
            return args;
        }

        public void setArgs(List<Object> args) {
            this.args.clear();
            this.args.addAll(args);
        }

        public Builder addArg(Object arg) {
            this.args.add(arg);
            return this;
        }

        @Override
        public Command build() {
            return new Command(this);
        }
    }

    private final int id;
    private final String action;
    private final List<Object> args;

    private Command(Builder b) {
        this.id = b.id;
        this.action = b.action;
        Preconditions.checkNotNull(this.action, "action is null");
        this.args = ImmutableList.copyOf(b.args);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public int getId() {
        return id;
    }

    /**
     * action on which is registered handler
     * @see talkeeg.common.ipc.IpcService#addIpcHandler(String, talkeeg.common.ipc.TgbfHandler)
     * @return
     */
    public String getAction() {
        return action;
    }

    public List<Object> getArgs() {
        return args;
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
        if(args != null? !args.equals(command.args) : command.args != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (action != null? action.hashCode() : 0);
        result = 31 * result + (args != null? args.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Command{" +
          "id=" + id +
          ", action='" + action + '\'' +
          ", args=" + args +
          '}';
    }
}
