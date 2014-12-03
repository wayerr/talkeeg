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

package talkeeg.common.barcode;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.GlobalHistogramBinarizer;
import org.junit.Assert;
import org.junit.Test;
import talkeeg.bf.BinaryData;

import java.awt.image.BufferedImage;

public class BarcodeServiceTest {

    @Test
    public void testEncode() throws Exception {
        BarcodeService service = new BarcodeService();
        BinaryData src = BinaryData.fromString("80200130000060D2EEEBC41AFE35E0824CF506BB98A54960BE0C19B5E78D44D49DDC55FBE2E0FE887020000010900030010380200B7020A230819F300D06092A864886F70D010101050003818D0030818902818100E850277FA39F7D63F713ED2E2E6EC23043A9DF29DE44ECEAA64C726ADE1F8BB69ECA13B5D6CA2BFDF4A0A98235B762E9B7EFA0B9864465D7589B65BE52898724865948FEBD674A4B5C58D3AAAD0374B736A88A3D0E422927A8646FF7A551A4520C2048E55A9E106CA3F9F78E264A26178C24824ED69800A6565E0A949DDBD6FD0203010001900020117020046E69636B702007636C69656E74329000201160D2EEEBC41AFE35E0824CF506BB98A54980200C9000202A80200D2001702021666337313A303A303A303A3133313A316163653A6136313A34616130256574683011");
        BitMatrix bitMatrix = service.encode(src);
        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
        BinaryBitmap bitmap = new BinaryBitmap(new GlobalHistogramBinarizer(new BufferedImageLuminanceSource(image)));
        BinaryData decoded = service.decode(bitmap);
        //System.out.println(Arrays.toHexString(decoded.getData()));
        Assert.assertArrayEquals(src.getData(), decoded.getData());
    }
}