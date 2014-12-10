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
import talkeeg.bf.Int128;
import talkeeg.common.model.UserIdentityCard;

/**
 * tool for generating sample messages
 *
 * Created by wayerr on 10.12.14.
 */
public final class SampleMessages {
    public static final Int128 CLIENT_1_ID = Int128.fromString("BE0C19B5E78D44D49DDC55FBE2E0FE88");

    public static final Int128 CLIENT_2_ID = Int128.fromString("D2EEEBC41AFE35E0824CF506BB98A549");
    public static final UserIdentityCard USER_2_UIC = UserIdentityCard.builder()
            .key(BinaryData.fromString("30819F300D06092A864886F70D010101050003818D0030818902818100E8" +
                    "50277FA39F7D63F713ED2E2E6EC23043A9DF29DE44ECEAA64C726ADE1F8B" +
                    "B69ECA13B5D6CA2BFDF4A0A98235B762E9B7EFA0B9864465D7589B65BE52" +
                    "898724865948FEBD674A4B5C58D3AAAD0374B736A88A3D0E422927A8646F" +
                    "F7A551A4520C2048E55A9E106CA3F9F78E264A26178C24824ED69800A656" +
                    "5E0A949DDBD6FD0203010001"))
            .putAttr(UserIdentityCard.ATTR_NICK, "user2")
            .addClient(CLIENT_2_ID)
            .build();
    //TODO key is dublicate of USER_2, we must fix it
    public static final UserIdentityCard USER_1_UIC = UserIdentityCard.builder()
            .key(BinaryData.fromString("30819F300D06092A864886F70D010101050003818D0030818902818100E8" +
                    "50277FA39F7D63F713ED2E2E6EC23043A9DF29DE44ECEAA64C726ADE1F8B" +
                    "B69ECA13B5D6CA2BFDF4A0A98235B762E9B7EFA0B9864465D7589B65BE52" +
                    "898724865948FEBD674A4B5C58D3AAAD0374B736A88A3D0E422927A8646F" +
                    "F7A551A4520C2048E55A9E106CA3F9F78E264A26178C24824ED69800A656" +
                    "5E0A949DDBD6FD0203010001"))
            .putAttr(UserIdentityCard.ATTR_NICK, "user1")
            .addClient(CLIENT_1_ID)
            .build();

}
