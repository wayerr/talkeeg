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

package talkeeg.common.util;

import org.junit.Test;
import talkeeg.common.core.Env;
import talkeeg.common.core.SampleMessages;
import talkeeg.common.model.UserIdentityCard;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FileDataTest {

    @PostConstruct
    public static void postConstruct() {
        Env.getInstance();
    }

    @PreDestroy
    public static void preDestroy() {
        Env.getInstance().close();
    }

    @Test
    public void testFileData() throws Exception {
        final Env env = Env.getInstance();
        final String appName = env.getConfig().getApplicationName();
        final File tempFile = env.getCacheDirsService().createTempFile("file-data");
        FileData fileData = new FileData(env.getBf(), tempFile);
        List<UserIdentityCard> orig = Arrays.asList(SampleMessages.USER_2_UIC, SampleMessages.USER_1_UIC);
        fileData.write(orig);
        final List<Object> readed = new ArrayList<>();
        fileData.read(new Callback<Object>() {
            @Override
            public void call(Object value) {
                readed.add(value);
            }
        });
        assertEquals(orig, readed);
        // if test failed then file was not deleted, remain it for analyzing error
        tempFile.delete();
    }
}