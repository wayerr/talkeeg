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

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class CallbacksContainerTest {

    @Test
    public void testContainer() throws Exception {
        CallbacksContainer<String> container = new CallbacksContainer<>();
        call(container);

        StringCallback one = new StringCallback();
        container.add(one);
        call(container);

        StringCallback two = new StringCallback();
        container.add(two);
        call(container);

        StringCallback three = new StringCallback();
        container.add(three);
        call(container);

        container.remove(one);
        call(container);
        container.remove(two);
        call(container);
        container.remove(three);
        call(container);
        container.remove(one);
        call(container);
        container.add(one);
        call(container);
        container.remove(one);
        call(container);
        container.add(one);
        call(container);
        container.add(one);
        call(container);
        container.add(one);
        call(container);
        container.remove(one);
        call(container);
    }

    protected void call(CallbacksContainer<String> container) {
        container.call("test");
        check(container);
        container.call(null);
        check(container);
    }

    private void check(CallbacksContainer<String> container) {
        final int uniqueInvocations = StringCallback.getUniqueInvocationsAndReset();
        final int invocations = StringCallback.getInvocationsAndReset();
        assertEquals(invocations, uniqueInvocations);
        assertEquals(container.size(), invocations);
    }

    private static class StringCallback implements Callback<String> {
        public static List<StringCallback> invocations = new ArrayList<>();
        public static Set<StringCallback> uniqueInvocations = new HashSet<>();

        @Override
        public void call(String value) {
            invocations.add(this);
            uniqueInvocations.add(this);
            System.out.println(Integer.toHexString(this.hashCode()) + " : " + value);
        }

        public static int getUniqueInvocationsAndReset() {
            final int size = uniqueInvocations.size();
            uniqueInvocations.clear();
            return size;
        }

        public static int getInvocationsAndReset() {
            final int size = invocations.size();
            invocations.clear();
            return size;
        }
    }
}