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

package talkeeg.common.ipc;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

/**
 * result of message verification
 * Created by wayerr on 19.12.14.
 */
public class VerifyResult<T> {

    public static class Builder<T> {
        private T message;
        private final List<String> errors = new ArrayList<>();


        public T getMessage() {
            return message;
        }

        public void setMessage(T message) {
            this.message = message;
        }

        public void addError(String error) {
            this.errors.add(error);
        }

        public List<String> getErrors() {
            return errors;
        }

        public VerifyResult<T> build() {
            return new VerifyResult<>(this);
        }

    }

    private final T message;
    private final List<String> errors;

    private VerifyResult(Builder<T> b) {
        this.message = b.message;
        this.errors = ImmutableList.copyOf(b.errors);
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public T getMessage() {
        return message;
    }

    public boolean isVerified() {
        return this.errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "VerifyResult{" +
          "message=" + message +
          ", errors=" + errors +
          '}';
    }
}
