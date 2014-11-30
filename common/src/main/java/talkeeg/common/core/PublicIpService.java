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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * retrieve public (external) ip for specified local ip, <p/>
 * we don`t have need this service after implementing external server
 *
 * TODO we must add watchdog which will be kill tasks with hanged requests
 *
 * Created by wayerr on 30.11.14.
 */
final class PublicIpService implements Function<InetAddress, InetAddress>, Closeable {
    private static final Logger LOG = Logger.getLogger(PublicIpService.class.getName());

    private class Service {
        private final URL url;
        private long timeout;

        private Service(String uriString) throws MalformedURLException {
            this.url = new URL(uriString);
        }

        String getIp() throws IOException {
            final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            try(InputStreamReader r = new InputStreamReader(connection.getInputStream())) {
                return CharStreams.toString(r);
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
            final List<Service> sorted = new ArrayList<>();
            for(String serviceUrlString : knowedServices) {
                try {
                    sorted.add(new Service(serviceUrlString));
                } catch(MalformedURLException e) {
                    LOG.log(Level.WARNING, "for string: " + serviceUrlString, e);
                }
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
        String externalIpString = null;
        while(externalIpString == null) {
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
                break;
            }
            try {
                externalIpString = service.getIp();
            } catch(IOException e) {
                //service was broken, disable service and try next service
                LOG.log(Level.SEVERE, "service " + service.url, e);
                synchronized(services) {
                    services.remove(service);
                }
            }
        }
        try {
            return InetAddress.getByName(externalIpString);
        } catch(UnknownHostException e) {
            LOG.log(Level.SEVERE, "for external ip: " + externalIpString, e);
        }
        return null;
    }

    @Override
    public void close() {
        this.executor.shutdownNow();
    }
}
