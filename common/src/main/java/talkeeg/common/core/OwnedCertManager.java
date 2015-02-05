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
import talkeeg.common.util.Fingerprints;

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
 * certificate manager and generator <p/>
 * TODO we need allow regenerating of certificate and increment serialNumber <p/>
 * Created by wayerr on 02.02.15.
 */
public final class OwnedCertManager {

    private static final BigInteger START_SERIAL = new BigInteger("1");
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
     * return certificate for specified key type
     * @param keyType
     * @return
     */
    public X509Certificate getCertificate(OwnedKeyType keyType) throws GeneralSecurityException {
        load();
        final PublicKey publicKey = cryptoService.getOwnedKeysManager().getPublicKey(keyType);
        synchronized(lock) {
            X509Certificate cert = map.get(publicKey);
            if(cert == null) {
                cert = generate(keyType, START_SERIAL);
                save(cert);
                map.put(publicKey, cert);
            }
            return cert;
        }
    }

    private void save(X509Certificate cert) throws GeneralSecurityException {
        File file = getCertFile(cert);
        try {
            file.createNewFile();
            Files.write(cert.getEncoded(), file);
        } catch(IOException e) {
            throw new GeneralSecurityException("Can not save generated certificate to " + file, e);
        }
    }

    private File getCertFile(X509Certificate cert) {
        final PublicKey publicKey = cert.getPublicKey();
        final String fp = getFingerprint(publicKey);
        //file name of certificate is fingerprint of public key
        //  we assume that for each public key may be only one certificate
        return new File(this.certsDir,  fp + "_" + cert.getSerialNumber().toString() + ".cert");
    }

    private String getFingerprint(PublicKey publicKey) {
        Int128 fingerprint = cryptoService.getFingerprint(publicKey);
        return Fingerprints.toFriendly(fingerprint);
    }

    private X509Certificate generate(OwnedKeyType keyType, BigInteger oldSerial) throws GeneralSecurityException {
        final OwnedKeysManager ownedKeysManager = this.cryptoService.getOwnedKeysManager();
        final PublicKey publicKey = ownedKeysManager.getPublicKey(keyType);

        Calendar expiry = Calendar.getInstance();
        expiry.add(Calendar.DAY_OF_YEAR, -10);
        Date startDate = expiry.getTime();
        expiry.add(Calendar.YEAR, 1);
        Date expiryDate = expiry.getTime();
        BigInteger serialNumber = oldSerial.add(new BigInteger("1"));

        X509V1CertificateGenerator certGen = new X509V1CertificateGenerator();
        //TODO improve certificate principal data
        certGen.setSerialNumber(serialNumber);
        certGen.setIssuerDN(new X500Principal("CN=" + keyType.name() + "(" + getFingerprint(publicKey) + ") CA Certificate"));
        certGen.setNotBefore(startDate);
        certGen.setNotAfter(expiryDate);
        certGen.setSubjectDN(new X500Principal("CN=talkeeg certificate"));
        certGen.setPublicKey(publicKey);
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
        Certificate cert;
        try(InputStream is = new FileInputStream(file)) {
            // we support only one certificate per file
            cert = cf.generateCertificate(is);
        } catch(IOException e) {
            throw new CertStoreException("Can not load certificates from file: " + file.getName(), e);
        }
        if(!(cert instanceof X509Certificate)) {
            logger.warning("Unsupported certificate: " + cert);
            return;
        }
        X509Certificate loadedCert = (X509Certificate)cert;
        synchronized(lock) {
            PublicKey publicKey = cert.getPublicKey();
            X509Certificate oldCert = map.get(publicKey);
            if(oldCert != null) {
                // of old cert is newer than loadedCert then ignore
                if(oldCert.getSerialNumber().compareTo(loadedCert.getSerialNumber()) > 0) {
                    // remove loaded cert
                    file.delete();
                    return;
                }
                // else remove old certificate
                removeCert(oldCert);
            }
            map.put(publicKey, loadedCert);
        }
    }

    private void removeCert(X509Certificate cert) {
        final File file = getCertFile(cert);
        file.delete();
    }
}
