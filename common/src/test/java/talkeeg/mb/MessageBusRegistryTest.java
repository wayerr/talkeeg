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

package talkeeg.mb;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class MessageBusRegistryTest {

    private static final String KEY_ID = "one";

    @Test
    public void testMessageBus() throws Exception {
        final List<String> strings = new ArrayList<>();

        final MessageBusRegistry registry = new MessageBusRegistry();
        final MessageBusKey<String> stringKey = MessageBusKey.create(KEY_ID, String.class);
        final MessageBus<String> stringBus = registry.getOrCreateBus(stringKey);
        stringBus.register(new Listener<String>() {

            @Override
            public void listen(String event) throws Exception {
                System.out.println("string event" + event);
                strings.add(event);
            }
        });

        assertEquals(stringBus, registry.getOrCreateBus(stringKey));
        assertEquals(stringBus, registry.getOrCreateBus(MessageBusKey.create("one", String.class)));

        registry.getOrCreateBus(MessageBusKey.create(KEY_ID, Integer.class)).listen(1);

        final List<String> sample = new ArrayList<>(Arrays.asList("test1", "test2", "test3"));
        for(String event : sample) {
            stringBus.listen(event);
        }

        assertEquals(sample, strings);

    }
}