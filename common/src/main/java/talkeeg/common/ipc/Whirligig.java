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

import com.google.common.base.Preconditions;
import talkeeg.common.conf.Config;
import talkeeg.common.conf.Node;

import java.net.*;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * event driven cycle
 *
 * Created by wayerr on 26.11.14.
 */
final class Whirligig implements Runnable {
    private static final Logger LOG = Logger.getLogger(Whirligig.class.getName());
    private final Config config;
    private final Io io;
    private final Node configNode;
    private final int port;
    private final IpcServiceImpl ipcService;
    private volatile State state;

    private class State {
        private Selector selector;
        private DatagramChannel channel4;
        private DatagramChannel channel6;

        private State() throws Exception {
            this.selector = Selector.open();



            InetSocketAddress socketAddress6 = getInetSocketAddress(port, configNode.<String>getValue("listenIp6", null));
            boolean supportProtocolFamilies;
            try {
                //in android no this class
                Class.forName("java.net.StandardProtocolFamily");
                supportProtocolFamilies = true;
            } catch(Exception e) {
                supportProtocolFamilies = false;
            }
            if(socketAddress6 != null && supportProtocolFamilies) {
                this.channel6 = DatagramChannel.open(StandardProtocolFamily.INET6);
                // check that it ipv6 address
                if(!(socketAddress6.getAddress() instanceof Inet6Address)) {
                    throw new RuntimeException(socketAddress6 + " must be an IPv6 address");
                }
                configureChannel(this.channel6, socketAddress6);
            }
            // we can not bind on both protocols
            if(socketAddress6 == null) {
                InetSocketAddress socketAddress4 = getInetSocketAddress(port, configNode.getValue("listenIp4", "0.0.0.0"));
                if(socketAddress4 != null) {
                    this.channel4 = DatagramChannel.open();
                    // check that it ipv4 address
                    if(!(socketAddress4.getAddress() instanceof Inet4Address)) {
                        throw new RuntimeException(socketAddress4 + " must be an IPv4 address");
                    }
                    configureChannel(this.channel4, socketAddress4);
                }
            }

        }

        private void configureChannel(final DatagramChannel channel, InetSocketAddress socketAddress) throws Exception {
            channel.configureBlocking(false);
            Preconditions.checkNotNull(socketAddress, "socketAddress is null");
            //android does not have an channel.bind()
            channel.socket().bind(socketAddress);
            channel.register(this.selector, SelectionKey.OP_READ);
        }
    }

    Whirligig(Config config, Io io, IpcServiceImpl ipcService) {
        this.config = config;
        this.io = io;
        this.ipcService = ipcService;

        this.configNode = config.getRoot().getNode("net");

        this.port = configNode.getValue("port", 11662);
    }

    private InetSocketAddress getInetSocketAddress(int port, String stringAddr) throws Exception {
        if(stringAddr == null) {
            return null;
        }
        final InetAddress address = InetAddress.getByName(stringAddr);
        return new InetSocketAddress(address, port);
    }


    @Override
    public void run() {
        final Thread currentThread = Thread.currentThread();
        try {
            final State state = this.state = new State();
            final Selector selector = state.selector;
            while(!currentThread.isInterrupted()) {
                final int select = selector.select();
                if(select == 0) {
                    //it`s possible error or whirligig was interrupted
                    System.out.println("select zero keys");
                }
                final Set<SelectionKey> keys = selector.selectedKeys();
                final Iterator<SelectionKey> iterator = keys.iterator();
                while(iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    processKey(state, key);
                }
            }
        } catch(InterruptedException e) {
            LOG.log(Level.INFO, "exiting by interruption", e);
        } catch(Exception e) {
            LOG.log(Level.SEVERE, "whirligig is killed by error: ", e);
        }
    }

    /**
     * key processor <p/>
     * @param state
     * @param key
     * @throws Exception
     */
    private void processKey(State state, SelectionKey key) {
        DatagramChannel channel = (DatagramChannel)key.channel();
        if(key.isReadable()) {
            try {
                final IoObject ioObject = io.read(channel);
                this.ipcService.accept(ioObject);
            } catch(Exception e) {
                LOG.log(Level.SEVERE, "error on key " + key, e);
            }
        }
    }

    void push(IoObject ioObject) {
        State state = this.state;
        if(state == null) {
            throw new NullPointerException("state of whirligig is null");
        }
        DatagramChannel channel = state.channel6;
        if(channel == null) {
            channel = state.channel4;
        }
        try {
            this.io.write(ioObject, channel);
        } catch(Exception e) {
            throw new RuntimeException("at ioObject " + ioObject, e);
        }
    }

    int getPort() {
        return port;
    }

    boolean isIpv6Supported() {
        State state = this.state;
        return state != null && state.channel6 != null;
    }
}
