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

import talkeeg.bf.Bf;
import talkeeg.bf.BinaryData;
import talkeeg.bf.Int128;
import talkeeg.common.core.*;
import talkeeg.common.model.ClientIdentityCard;
import talkeeg.common.model.MessageCipherType;
import talkeeg.common.model.SingleMessage;
import talkeeg.common.model.UserIdentityCard;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.ByteBuffer;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by wayerr on 19.12.14.
 */
@Singleton
final class SingleMessageVerifier implements MessageVerifier<SingleMessage> {
    private static final Logger LOG = Logger.getLogger(SingleMessageVerifier.class.getName());


    private final CryptoService cryptoService;
    private final AcquaintedClientsService acquaintedClients;
    private final AcquaintedUsersService acquaintedUsers;
    private final Bf bf;
    private final KeyLoader keyLoader;

    @Inject
    public SingleMessageVerifier(Bf bf,
                                 KeyLoader keyLoader,
                                 CryptoService cryptoService,
                                 AcquaintedClientsService acquaintedClients,
                                 AcquaintedUsersService acquaintedUsers) {
        this.bf = bf;
        this.keyLoader = keyLoader;
        this.cryptoService = cryptoService;
        this.acquaintedClients = acquaintedClients;
        this.acquaintedUsers = acquaintedUsers;
    }

    @Override
    public VerifyResult<SingleMessage> verify(IpcEntryHandlerContext context, SingleMessage message) {
        VerifyResult.Builder<SingleMessage> builder = VerifyResult.builder();
        try {
            checkSign(context, message, builder);
        } catch(Exception e) {
            final String str = message + " validation failed, see log. It came from " + context.getSrcClientAddress();
            LOG.log(Level.SEVERE, str, e);
            builder.addError(str);
        }
        return builder.build();
    }

    private void checkSign(IpcEntryHandlerContext context, SingleMessage message, VerifyResult.Builder<SingleMessage> builder) throws Exception{
        BinaryData clientSign = message.getClientSign();
        BinaryData userSign = message.getUserSign();
        if(clientSign == null && userSign == null) {
            builder.addError(message + " is unsigned, reject. It came from " + context.getSrcClientAddress());
            return;
        }
        final Int128 sourceClientId = message.getSrc();
        if(sourceClientId == null) {
            LOG.log(Level.SEVERE, message + " has null src, reject. It came from " + context.getSrcClientAddress());
            return;
        }
        PublicKey clientPublicKey = null;
        if(userSign != null && message.getCipherType() == MessageCipherType.NONE) {
            //user-signed messages can appear only in acquaint process,
            // so we need use AcquaintService for validation for extract user and client
            final AcquaintService.AcquaintData acquaintData = extractAcquaintData(message);
            final UserIdentityCard uic = acquaintData.getUserIdentityCard();
            final ClientIdentityCard cic = acquaintData.getClientIdentityCard();
            final PublicKey userPublicKey = this.keyLoader.loadPublic(uic.getKey().getData());
            clientPublicKey = this.keyLoader.loadPublic(cic.getKey().getData());
            final Signature verifyService = this.cryptoService.getVerifyService(userPublicKey);
            verifySign(message, userSign, builder, verifyService, "user");
        }
        if(clientSign != null) {
            if(clientPublicKey == null) {
                final AcquaintedClient client = this.acquaintedClients.getClient(sourceClientId);
                if(client == null) {
                    builder.addError("Client " + sourceClientId + " is not acquainted");
                    return;
                }
                clientPublicKey = client.getKey();
            }
            final Signature verifyService = this.cryptoService.getVerifyService(clientPublicKey);
            verifySign(message, clientSign, builder, verifyService, "client");
        }
    }

    private AcquaintService.AcquaintData extractAcquaintData(SingleMessage message) throws Exception {
        final List<?> objects = (List<?>)this.bf.read(ByteBuffer.wrap(message.getData().getData()));
        final IpcEntry entry = ((IpcEntry)objects.get(0));
        return AcquaintService.toAcquaintData(entry);
    }

    private void verifySign(SingleMessage message, BinaryData sign, VerifyResult.Builder<SingleMessage> builder, Signature verifyService, String signType) throws SignatureException {
        verifyService.update(message.getData().getData());
        boolean verify = verifyService.verify(sign.getData());
        if(!verify) {
            builder.addError(signType + " sign verification failed");
        }
    }

}