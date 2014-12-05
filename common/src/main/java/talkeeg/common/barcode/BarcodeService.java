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

import com.google.common.collect.ImmutableMap;
import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import talkeeg.bf.BinaryData;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Created by wayerr on 03.12.14.
 */
public final class BarcodeService {
    private static final String DEFAULT_BYTE_MODE_ENCODING = "ISO-8859-1";

    public BitMatrix encode(BinaryData data) {
        final QRCodeWriter writer = new QRCodeWriter();
        final String string = new String(data.getData(), StandardCharsets.ISO_8859_1);
        try {
            BitMatrix matrix = writer.encode(string, BarcodeFormat.QR_CODE, 0, 0, ImmutableMap.of(EncodeHintType.CHARACTER_SET, DEFAULT_BYTE_MODE_ENCODING));
            return matrix;
        } catch(WriterException e) {
            throw new RuntimeException(e);
        }
    }

    public BinaryData decode(BinaryBitmap image) {
        QRCodeReader reader = new QRCodeMultiReader();
        try {
            Result result = reader.decode(image, ImmutableMap.of(DecodeHintType.CHARACTER_SET, DEFAULT_BYTE_MODE_ENCODING));
            Map<ResultMetadataType, Object> resultMetadata = result.getResultMetadata();
            List<?> segments = (List<?>)resultMetadata.get(ResultMetadataType.BYTE_SEGMENTS);
            if(segments == null || segments.isEmpty()) {
                throw new RuntimeException("no binary data");
            }
            return new BinaryData((byte[])segments.get(0));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
