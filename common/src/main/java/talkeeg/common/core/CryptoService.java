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

import talkeeg.bf.BinaryData;
import talkeeg.common.conf.Config;
import talkeeg.bf.Int128;
import javax.crypto.Cipher;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.security.*;
import java.util.logging.Logger;

/**
 * service which provide some cryptographic functions for ciphering, signing and etc.
 * also this service manage user and client private keys
 *
 * Created by wayerr on 27.11.14.
 */
@Singleton
public final class CryptoService {
    private static final Logger LOG = Logger.getLogger(CryptoService.class.getName());
    private final OwnedKeysManager ownedKeysManager;
    private final Config config;
    private static final String ALG_SIGN = CryptoConstants.ALG_HASH + "with" + CryptoConstants.ALG_ASYMMETRIC;

    @Inject
    CryptoService(Config config, KeyLoader keyLoader) {
        this.config = config;
        this.ownedKeysManager = new OwnedKeysManager(config, keyLoader, new KeyPairGen());
    }

    void init() {
        this.ownedKeysManager.loadKeys();
    }

    public Signature getSignService(OwnedKeyType keyType) {
        try {
            Signature signature = Signature.getInstance(ALG_SIGN);
            signature.initSign(ownedKeysManager.getPrivateKey(keyType));
            return signature;
        } catch(InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public Signature getVerifyService(OwnedKeyType keyType) {
        final PublicKey publicKey = this.ownedKeysManager.getPublicKey(keyType);
        return getVerifyService(publicKey);
    }

    public Signature getVerifyService(PublicKey publickKey) {
        try {
            Signature signature = Signature.getInstance(ALG_SIGN);
            signature.initVerify(publickKey);
            return signature;
        } catch(InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * configure cipher service for asymmetric ciphering
     * @return
     */
    public Cipher getCipherAsymmetricService(PublicKey key) {
        try {
            Cipher cipher = Cipher.getInstance(CryptoConstants.CIPHER_ASYMMETRIC);
            cipher.init(Cipher.PUBLIC_KEY, key);
            return cipher;
        } catch(GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * configure cipher service for asymmetric deciphering
     * @return
     */
    public Cipher getDecipherAsymmetricService(OwnedKeyType keyType) {
        try {
            final PrivateKey privateKey = this.getOwnedKeysManager().getPrivateKey(keyType);
            final Cipher cipher = Cipher.getInstance(CryptoConstants.CIPHER_ASYMMETRIC);
            cipher.init(Cipher.PRIVATE_KEY, privateKey);
            return cipher;
        } catch(GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    Int128 getFingerprint(Key key) {
        return getFingerprint(key.getEncoded());
    }

    Int128 getFingerprint(BinaryData key) {
        return getFingerprint(key.getData());
    }

    private Int128 getFingerprint(final byte[] data) {
        try {
            final MessageDigest md = MessageDigest.getInstance(CryptoConstants.ALG_MESSAGE_DIGEST);
            byte[] digest = md.digest(data);
            if(digest.length > Int128.LENGTH) {//truncate to 128 bit
                digest = java.util.Arrays.copyOf(digest, Int128.LENGTH);
            }
            return new Int128(digest);
        } catch(NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public OwnedKeysManager getOwnedKeysManager() {
        return ownedKeysManager;
    }
}
