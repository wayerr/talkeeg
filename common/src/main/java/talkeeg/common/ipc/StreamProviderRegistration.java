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

import talkeeg.bf.BinaryData;
import talkeeg.bf.Int128;
import talkeeg.common.model.*;

import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.util.List;

/**
 * registration of stream provider
 * Created by wayerr on 29.12.14.
 */
public final class StreamProviderRegistration extends StreamBasicRegistration {
    private final StreamProvider provider;
    private final StreamOffer offer;
    private static final int CHUNK_SIZE = 1024;

    StreamProviderRegistration(StreamSupport streamSupport, StreamProvider provider, StreamConfig config) {
        super(streamSupport, null, config);
        this.provider = provider;
        this.offer = StreamOffer.builder()
          .length(provider.getLength())
          .streamId(getStreamId())
          .build();
    }

    public StreamProvider getProvider() {
        return provider;
    }

    @Override
    protected void processDecrypted(StreamMessage message, BinaryData decrypted) throws Exception {
        StreamMessageType type = message.getType();
        switch(type) {
            case REQUEST:
                if(decrypted == null) {
                    throw new RuntimeException("message with type " + type + " must contains non null data");
                }
                processRequest((StreamRequest)deserialize(decrypted));
                break;
            case RESPONSE:
                sendData();
                break;
            case END:
                this.provider.abort(this);
                break;
            default:
                throw new RuntimeException("Consumer not support " + type + " stream message");
        }
    }

    private void sendData() throws Exception {
        //in future we must send data in another stream
        final BinaryData data = this.provider.provide(this, CHUNK_SIZE);
        send(StreamMessageType.DATA, data);
    }

    private void processRequest(StreamRequest request) throws Exception {
        this.provider.open(this);
        final BinaryData seed = getOrCreateSeed();
        final CipherOptions options = findSupportedOptions(request.getCiphers());

        final byte[] ivBytes = SecureRandom.getSeed(options.getCipher().getBlockSize());
        final IvParameterSpec iv = new IvParameterSpec(ivBytes);

        initStreamParameters(options, iv, seed, request.getSeed());

        final StreamHead.Builder builder = StreamHead.builder();
        builder.setLength(this.provider.getLength());
        builder.setSeed(seed);
        builder.setIv(new BinaryData(ivBytes));
        builder.setOptions(options);
        send(StreamMessageType.HEAD, serialize(builder.build()));
    }

    private CipherOptions findSupportedOptions(List<CipherOptions> ciphers) {
        final List<CipherOptions> supportedCiphers = getSupportedCiphers();
        for(int i = 0; i < supportedCiphers.size(); ++i) {
            CipherOptions options = supportedCiphers.get(i);
            if(ciphers.contains(options)) {
                return options;
            }
        }
        throw new RuntimeException("Can not find supported ciphers: " + supportedCiphers + " in ciphers from request: " + ciphers);
    }

    public StreamOffer getOffer() {
        return this.offer;
    }

    /**
     * unregister stream provider
     */
    @Override
    public void close() {
        super.close();
        //TODO send END if current state before END
    }
}
