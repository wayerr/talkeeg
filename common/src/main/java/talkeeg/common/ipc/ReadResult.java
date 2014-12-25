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
import talkeeg.common.model.StatusCode;

import java.util.ArrayList;
import java.util.List;

/**
 * result of message verification
 * Created by wayerr on 19.12.14.
 */
public class ReadResult<T> {


    public static class Builder<T> {
        private T message;
        private final List<String> errors = new ArrayList<>();
        private Object arg;
        private StatusCode statusCode;

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

        public boolean isVerified() {
            return errors.isEmpty() && (this.statusCode == null || this.statusCode == StatusCode.OK);
        }

        public Object getArg() {
            return arg;
        }

        public void setArg(Object arg) {
            this.arg = arg;
        }

        /**
         * status code, which sent in {@link talkeeg.common.model.Command}
         * @return
         */
        public StatusCode getStatusCode() {
            return statusCode;
        }

        /**
         * status code, which sent in {@link talkeeg.common.model.Command}
         * @param statusCode
         * @return
         */
        public Builder responseCode(StatusCode statusCode) {
            setStatusCode(statusCode);
          return this;
        }

        /**
         * status code, which sent in {@link talkeeg.common.model.Command}
         * @param statusCode
         */
        public void setStatusCode(StatusCode statusCode) {
            this.statusCode = statusCode;
        }

        public ReadResult<T> build() {
            return new ReadResult<>(this);
        }
    }

    private final T message;
    private final List<String> errors;
    private final Object arg;
    private final StatusCode statusCode;

    private ReadResult(Builder<T> b) {
        this.message = b.message;
        this.errors = ImmutableList.copyOf(b.errors);
        this.arg = b.arg;
        this.statusCode = b.statusCode;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public T getMessage() {
        return message;
    }

    public boolean isVerified() {
        return errors.isEmpty() && (this.statusCode == null || this.statusCode == StatusCode.OK);
    }

    public List<String> getErrors() {
        return errors;
    }

    public Object getArg() {
        return this.arg;
    }

    /**
     * status code, which sent in {@link talkeeg.common.model.Command}
     * @return
     */
    public StatusCode getStatusCode() {
        return statusCode;
    }

    @Override
    public String toString() {
        return "VerifyResult{" +
          "message=" + message +
          ", errors=" + errors +
          '}';
    }
}
