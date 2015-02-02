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

package talkeeg.common.core;

import com.google.common.io.Files;
import org.bouncycastle.x509.X509V1CertificateGenerator;
import talkeeg.bf.*;
import talkeeg.common.conf.Config;

import javax.inject.Inject;
import javax.security.auth.x500.X500Principal;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.*;
import java.util.logging.Logger;

/**
 * certificate manager and generator
 *
 * Created by wayerr on 02.02.15.
 */
public final class OwnedCertManager {

    private final CryptoService cryptoService;
    private final File certsDir;
    private final Object lock = new Object();
    private boolean notLoaded = true;
    private final Map<PublicKey, X509Certificate> map = new HashMap<>();
    private final Logger logger = Logger.getLogger(getClass().getName());

    @Inject
    public OwnedCertManager(Config config, CryptoService cryptoService) {
        this.cryptoService = cryptoService;

        this.certsDir = new File(config.getConfigDir(), "certs");
        this.certsDir.mkdirs();
    }

    /**
     * return serticate for specified key type
     * @param keyType
     * @return
     */
    public X509Certificate getCertificate(OwnedKeyType keyType) throws GeneralSecurityException {
        load();
        final PublicKey publicKey = cryptoService.getOwnedKeysManager().getPublicKey(keyType);
        synchronized(lock) {
            X509Certificate cert = map.get(publicKey);
            if(cert == null) {
                cert = generate(keyType);
                save(cert);
                map.put(publicKey, cert);
            }
            return cert;
        }
    }

    private void save(X509Certificate cert) throws GeneralSecurityException {
        Int128 fingerprint = cryptoService.getFingerprint(cert.getPublicKey());
        File file = new File(this.certsDir, talkeeg.bf.Arrays.toHexString(fingerprint.getData()) + ".cert");
        try {
            file.createNewFile();
            Files.write(cert.getEncoded(), file);
        } catch(IOException e) {
            throw new GeneralSecurityException("Can not save generated certificate to " + file, e);
        }
    }

    private X509Certificate generate(OwnedKeyType keyType) throws GeneralSecurityException {
        Calendar expiry = Calendar.getInstance();
        expiry.add(Calendar.DAY_OF_YEAR, -10);
        Date startDate = expiry.getTime();
        expiry.add(Calendar.YEAR, 3);
        Date expiryDate = expiry.getTime();
        //TODO we need allow regenerating of certificated and increment serialNumber
        BigInteger serialNumber = new BigInteger("10");

        X509V1CertificateGenerator certGen = new X509V1CertificateGenerator();
        X500Principal dnName = new X500Principal("CN=" + keyType.name() + " CA Certificate");
        certGen.setSerialNumber(serialNumber);
        certGen.setIssuerDN(dnName);
        certGen.setNotBefore(startDate);
        certGen.setNotAfter(expiryDate);
        certGen.setSubjectDN(dnName);                       // note: same as issuer
        OwnedKeysManager ownedKeysManager = this.cryptoService.getOwnedKeysManager();
        certGen.setPublicKey(ownedKeysManager.getPublicKey(keyType));
        certGen.setSignatureAlgorithm(this.cryptoService.getSignatureAlgorithm());
        X509Certificate cert = certGen.generate(ownedKeysManager.getPrivateKey(keyType), "BC");
        return cert;
    }

    private void load() throws GeneralSecurityException {
        synchronized(lock) {
            if(!notLoaded) {
                return;
            }
            notLoaded = false;
        }
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        File[] files = certsDir.listFiles();
        for(File file: files) {
            if(!file.isFile()) {
                continue;
            }
            loadCertFile(cf, file);
        }
    }

    private void loadCertFile(CertificateFactory cf, File file) throws GeneralSecurityException {
        Collection<? extends Certificate> loadedCerts;
        try(InputStream is = new FileInputStream(file)) {
            loadedCerts = cf.generateCertificates(is);
        } catch(IOException e) {
            throw new CertStoreException("Can not load certificates from file: " + file.getName(), e);
        }
        for(Certificate loadedCert: loadedCerts) {
            if(!(loadedCert instanceof X509Certificate)) {
                logger.warning("Unsupported certificate: " + loadedCert);
                continue;
            }
            synchronized(lock) {
                map.put(loadedCert.getPublicKey(), (X509Certificate)loadedCert);
            }
        }
    }
}
