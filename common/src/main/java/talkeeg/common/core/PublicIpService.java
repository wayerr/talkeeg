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

package talkeeg.common.core;

import com.google.common.base.Function;
import com.google.common.io.CharStreams;
import com.google.common.util.concurrent.SettableFuture;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import talkeeg.common.conf.Config;
import talkeeg.common.util.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * retrieve public (external) ip for specified local ip, <p/>
 * we don`t have need this service after implementing external server
 *
 * Created by wayerr on 30.11.14.
 */
final class PublicIpService implements Function<InetAddress, InetAddress>, Closeable {
    private static final Logger LOG = Logger.getLogger(PublicIpService.class.getName());

    private final class Service implements Runnable {
        private final URL url;
        private long started;
        private volatile String ip;
        private boolean end;

        private Service(String uriString) throws MalformedURLException {
            this.url = new URL(uriString);
        }

        @Override
        public void run() {
            synchronized(this) {
                this.started = System.currentTimeMillis();
            }
            try {
                final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                try(InputStreamReader r = new InputStreamReader(connection.getInputStream())) {
                    this.ip = CharStreams.toString(r);
                }
            } catch(IOException e) {
                //we don`t need full stack trace
                LOG.log(Level.SEVERE, "error with " + url + ": " + e.toString());
            } finally {
                synchronized(this) {
                   this.end = true;
                }
            }
        }

        /**
         * time after execution start, if already executed, then return -1
         * @return
         */
        long getExecutionTime() {
            synchronized(this) {
                if(this.end) {
                    return -1;
                }
                return this.started;
            }
        }
    }

    /**
     * list of public http services which return ours external ip
     */
    private final List<String> knowedServices;
    private final ExecutorService executor;
    private final Callable<String> resolveFastestService = new Callable<String>() {

        @Override
        public String call() {
            final List<Future<?>> tasks = new ArrayList<>();
            final SettableFuture<String> settableFuture =  SettableFuture.create();
            String ip = null;
            try {
                for(final String serviceUrlString : knowedServices) {
                    try {
                        final Service service = new Service(serviceUrlString);
                        tasks.add(executor.submit(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    service.run();
                                    String ip = service.ip;
                                    if(ip != null) {
                                        settableFuture.set(ip);
                                    }
                                } catch(Exception e) {
                                    //errors in this is a usual thing
                                }
                            }
                        }));
                    } catch(MalformedURLException e) {
                        LOG.log(Level.SEVERE, "onr url " + serviceUrlString, e);
                    }
                    //yes, here we can use scheduled thread pool, but for what?
                    Thread.sleep(100);
                }
                //wait first executed thread
                ip = settableFuture.get(1, TimeUnit.MINUTES);
                //stop all other tasks
                for(Future<?> future: tasks) {
                    future.cancel(true);
                }
            } catch(InterruptedException e) {
                //nothing
            } catch(ExecutionException | TimeoutException e) {
                LOG.log(Level.SEVERE, "", e);
            }
            return ip;
        }
    };
    private volatile Future<String> externalIpTask;

    PublicIpService(Config config) {
        final String servicesString = config.getRoot().<String>getNode("net").getValue("publicIpServices", null);
        if(servicesString == null) {
            throw new NullPointerException(" 'net.publicIpServices' is null, but we need space delimited list of urls");
        }
        this.knowedServices = Collections.unmodifiableList(StringUtils.splitTo(new ArrayList<>(),
                servicesString, ' '));
        final ThreadFactoryBuilder builder = new ThreadFactoryBuilder();
        builder.setDaemon(true);
        builder.setNameFormat(getClass().getSimpleName() + "-pool-%d");
        this.executor = Executors.newCachedThreadPool(builder.build());

        reload();
    }

    private Future<String> reload() {
        return this.externalIpTask = this.executor.submit(resolveFastestService);
    }

    @Override
    public InetAddress apply(InetAddress input) {
        Future<String> externalIpTask = this.externalIpTask;
        if(externalIpTask == null) {
            //maybe we need to wait before services was reloaded?
            externalIpTask = reload();
        }

        String externalIpString = null;
        try {
            externalIpString = externalIpTask.get(2, TimeUnit.MINUTES);
            return InetAddress.getByName(externalIpString);
        } catch(Exception e) {
            LOG.log(Level.SEVERE, "for external ip: " + externalIpString, e);
        }
        return input;
    }

    @Override
    public void close() {
        this.executor.shutdownNow();
    }
}
