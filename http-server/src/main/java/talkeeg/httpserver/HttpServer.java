/*
 * Copyright (c) 2015, wayerr (radiofun@ya.ru).
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

package talkeeg.httpserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.nio.DefaultHttpServerIODispatch;
import org.apache.http.impl.nio.DefaultNHttpServerConnection;
import org.apache.http.impl.nio.DefaultNHttpServerConnectionFactory;
import org.apache.http.impl.nio.SSLNHttpServerConnectionFactory;
import org.apache.http.impl.nio.reactor.DefaultListeningIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.NHttpConnectionFactory;
import org.apache.http.nio.protocol.HttpAsyncService;
import org.apache.http.nio.protocol.UriHttpAsyncRequestHandlerMapper;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.ListeningIOReactor;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import talkeeg.common.core.CryptoService;
import talkeeg.common.core.OwnedKeyType;
import talkeeg.common.core.OwnedKeysManager;
import talkeeg.common.core.OwnedCertManager;

import javax.inject.Inject;
import javax.net.ssl.*;

/**
 * HTTP server based on hc.apache.org components <p/>
 * <p/>
 * Created by wayerr on 30.01.15.
 */
public final class HttpServer {

    private final Logger logger = Logger.getLogger(getClass().getName());

    // Create HTTP protocol processing chain
    private final HttpProcessor httpproc = HttpProcessorBuilder.create()
      .add(new ResponseDate())
      .add(new ResponseServer("Talkeeg HTTP/1.1"))
      .add(new ResponseContent())
      .add(new ResponseConnControl()).build();

    private final IOReactorConfig reactorConfig = IOReactorConfig.custom()
      .setIoThreadCount(1)
      .setSoTimeout(3000)
      .setConnectTimeout(3000)
      .build();

    private final UriHttpAsyncRequestHandlerMapper registry = new UriHttpAsyncRequestHandlerMapper();
    private final HttpAsyncService protocolHandler = new HttpAsyncService(httpproc, registry);
    private final HttpServerConfig config;
    private final OwnedKeysManager ownedKeysManager;
    private final OwnedCertManager certManager;

    @Inject
    HttpServer(HttpServerConfig config, CryptoService cryptoService, OwnedCertManager certManager) {
        this.config = config;
        this.ownedKeysManager = cryptoService.getOwnedKeysManager();
        this.certManager = certManager;
    }

    public UriHttpAsyncRequestHandlerMapper getRegistry() {
        return registry;
    }

    public void run() {
        // Create HTTP connection factory
        NHttpConnectionFactory<DefaultNHttpServerConnection> connFactory;
        connFactory = createConnectionFactory();
        // Create server-side I/O event dispatch
        IOEventDispatch ioEventDispatch = new DefaultHttpServerIODispatch(protocolHandler, connFactory);
        // Create server-side I/O reactor
        try {
            ListeningIOReactor ioReactor = new DefaultListeningIOReactor(reactorConfig);
            int portNumber = config.getPortNumber();
            ioReactor.listen(new InetSocketAddress(portNumber));
            ioReactor.execute(ioEventDispatch);
        } catch(IOException e) {
            logger.log(Level.SEVERE, "", e);
        }
    }

    private NHttpConnectionFactory<DefaultNHttpServerConnection> createConnectionFactory() {
        NHttpConnectionFactory<DefaultNHttpServerConnection> connFactory;
        if(config.isUseTLS()) {
            try {
                KeyStore keystore = KeyStore.getInstance("jks");
                char[] password = new char[0];
                keystore.load(null, password);
                final X509Certificate certificate = certManager.getCertificate(OwnedKeyType.USER);
                KeyStore.PrivateKeyEntry entry = new KeyStore.PrivateKeyEntry(ownedKeysManager.getPrivateKey(OwnedKeyType.USER), new Certificate[] {
                    certificate
                });

                keystore.setEntry("", entry, new KeyStore.PasswordProtection(password));
                KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                kmfactory.init(keystore, password);
                final KeyManager[] keymanagers = kmfactory.getKeyManagers();
                SSLContext sslcontext = SSLContext.getInstance("TLS");
                sslcontext.init(keymanagers, null, null);
                connFactory = new SSLNHttpServerConnectionFactory(sslcontext, null, ConnectionConfig.DEFAULT);
            } catch(Exception e) {
                throw new RuntimeException("Can not initialise SSL.", e);
            }
        } else {
            connFactory = new DefaultNHttpServerConnectionFactory(ConnectionConfig.DEFAULT);
        }
        return connFactory;
    }

}
