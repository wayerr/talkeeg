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

import com.google.common.base.Charsets;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import talkeeg.bf.BinaryData;
import talkeeg.bf.Int128;
import talkeeg.common.conf.DefaultConfiguration;
import talkeeg.common.core.*;
import talkeeg.common.model.ClientAddress;
import talkeeg.common.util.TgAddress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by wayerr on 29.12.14.
 */
public class StreamSupportTest {

    private static Env secondEnv;
    private static Env firstEnv;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    @BeforeClass
    public static void beforeClass() {
        firstEnv = new Env("first", DefaultConfiguration.builder().put("net.port", 11665).build());
        secondEnv = new Env("second", DefaultConfiguration.builder().put("net.port", 11664).build());
    }

    @AfterClass
    public static void afterClass() {
        firstEnv.close();
        secondEnv.close();
    }

    @Test
    public void testStreams() throws Exception {
        System.out.println("testStreams");

        EnvUtils.acquaint(firstEnv, secondEnv);
        EnvUtils.acquaint(secondEnv, firstEnv);


        final SampleStreamProvider provider = new SampleStreamProvider();
        StreamConfig.Builder builder = StreamConfig.builder()
          .streamId((short)1);

        configureBuilder(builder, firstEnv);
        final SampleStreamConsumer consumer = new SampleStreamConsumer(provider);
        secondEnv.get(StreamSupport.class).registerConsumer(consumer, builder.build());
        configureBuilder(builder, secondEnv);
        firstEnv.get(StreamSupport.class).registerProvider(provider, builder.build());
        System.out.println("testStreams start");

        waitClose();
        assertArrayEquals(provider.array, consumer.stream.toByteArray());
    }

    protected void configureBuilder(StreamConfig.Builder builder, Env env) {
        builder.setOtherClientId(env.get(OwnedIdentityCardsService.class).getClientId());
        builder.setOtherClientAddress(new ClientAddress(false, TgAddress.to("127.0.0.1", env.get(IpcServiceManager.class).getPort())));
    }

    private  class SampleStreamProvider implements StreamProvider {

        private final String sampleData = "пример utf данных";
        private final byte[] array;
        private int count = 0;

        public SampleStreamProvider() {
            byte[] pattern = sampleData.getBytes(StandardCharsets.UTF_8);
            final int repeats = 256;
            array = new byte[256 * pattern.length];
            for(int i = 0; i < repeats; ++i) {
                System.arraycopy(pattern, 0, array, i * pattern.length, pattern.length);
            }
        }

        @Override
        public void open(StreamProviderRegistration registration) {
            System.out.println("provider: open");
        }

        @Override
        public BinaryData provide(StreamProviderRegistration registration, int size) {
            System.out.println("provider: provide " + size + " bytes");
            final long remain = getLength() - this.count;
            if(remain <= 0) {
                return new BinaryData(new byte[0]);
            }
            if(remain < size) {
                size = (int)remain;
            }
            BinaryData binaryData = new BinaryData(Arrays.copyOfRange(array, this.count, this.count + size));
            this.count += size;
            return binaryData;
        }

        @Override
        public void close(StreamProviderRegistration registration) {
            System.out.println("provider: close");
            wakeup();
        }

        @Override
        public long getLength() {
            return array.length;
        }

        @Override
        public boolean isEnded() {
            return count >= getLength();
        }
    }

    private  class SampleStreamConsumer implements StreamConsumer {
        private final SampleStreamProvider provider;
        private final ByteArrayOutputStream stream = new ByteArrayOutputStream();

        public SampleStreamConsumer(SampleStreamProvider provider) {
            this.provider = provider;
        }

        @Override
        public void open(StreamConsumerRegistration registration)  throws Exception {
            System.out.println("consumer: open");
            assertEquals(provider.getLength(), registration.getLength());
        }

        @Override
        public void consume(StreamConsumerRegistration registration, BinaryData data) throws Exception {
            System.out.println("consumer: consume");
            stream.write(data.getData());
            System.out.println(new String(data.getData(), StandardCharsets.UTF_8));
        }

        @Override
        public void close(StreamConsumerRegistration registration) throws Exception {
            System.out.println("consumer: close");
            wakeup();
        }
    }

    private void waitClose() throws InterruptedException {
        lock.lock();
        try {
            condition.await(1, TimeUnit.MINUTES);
        } finally {
            lock.unlock();
        }
    }

    private void wakeup() {
        lock.lock();
        try {
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
