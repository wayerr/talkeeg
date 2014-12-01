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
    private final List<Service> services = new ArrayList<>();
    private final Runnable resolveFastestService = new Runnable() {
        @Override
        public void run() {
            final List<Service> sorted = Collections.synchronizedList(new ArrayList<>());
            final List<Future<?>> tasks = new ArrayList<>();
            final Lock lock = new ReentrantLock();
            final Condition condition = lock.newCondition();
            try {
                for(final String serviceUrlString : knowedServices) {
                    try {
                        final Service service = new Service(serviceUrlString);
                        tasks.add(executor.submit(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    service.run();
                                    sorted.add(service);
                                } catch(Exception e) {
                                    //errors in this is a usual thing
                                } finally {
                                    condition.signalAll();
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
                condition.await(1, TimeUnit.MINUTES);
                //stop all other tasks
                for(Future<?> future: tasks) {
                    future.cancel(true);
                }
            } catch(InterruptedException e) {
                //what we do there&
            }
            synchronized(services) {
                services.clear();
                services.addAll(sorted);
            }
        }
    };

    PublicIpService(Config config) {
        this.knowedServices = Collections.unmodifiableList(StringUtils.splitTo(new ArrayList<>(),
                config.getRoot().<String>getNode("net").getValue("publicIpServices", null), ' '));
        final ThreadFactoryBuilder builder = new ThreadFactoryBuilder();
        builder.setDaemon(true);
        builder.setNameFormat(getClass().getSimpleName() + "-pool-%d");
        this.executor = Executors.newCachedThreadPool(builder.build());

        reloadServices();
    }

    private void reloadServices() {
        this.executor.execute(resolveFastestService);
    }

    @Override
    public InetAddress apply(InetAddress input) {
        Service service;
        synchronized(services) {
            if(services.size() > 0) {
                service = services.get(0);
            } else {
                service = null;
            }
        }
        if(service == null) {
            //maybe we need to wait before services was reloaded?
            reloadServices();
        } else {

            String externalIpString = service.ip;
            try {
                return InetAddress.getByName(externalIpString);
            } catch(UnknownHostException e) {
                LOG.log(Level.SEVERE, "for external ip: " + externalIpString, e);
            }
        }
        return null;
    }

    @Override
    public void close() {
        this.executor.shutdownNow();
    }
}
