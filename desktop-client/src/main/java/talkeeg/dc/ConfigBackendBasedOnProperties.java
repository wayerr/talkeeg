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

package talkeeg.dc;

import talkeeg.common.conf.Config;
import talkeeg.common.conf.DefaultConfigBackend;
import talkeeg.common.util.Closeables;
import talkeeg.mb.MessageBusRegistry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * properties based config backend
 * Created by wayerr on 15.12.14.
 */
final class ConfigBackendBasedOnProperties extends DefaultConfigBackend {

    private final Properties properties;

    public ConfigBackendBasedOnProperties(MessageBusRegistry registry, Map<String, ?> defaults) {
        super(registry, defaults);
        this.properties = new Properties();
    }

    @Override
    protected Object get(String name) {
        return this.properties.get(name);
    }

    @Override
    protected <T> Object put(String name, T value) {
        return this.properties.put(name, value);
    }

    @Override
    protected boolean contains(String name) {
        return this.properties.containsKey(name);
    }

    @Override
    public void load(Config config) {
        final File propertiesFile = getPropertiesFile(config);
        if(!propertiesFile.exists()) {
            //no file - no properties
            return;
        }
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(propertiesFile);
            this.properties.load(stream);
        } catch(IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "at file: " + propertiesFile, e);
        } finally {
            Closeables.close(stream);
        }
    }

    protected File getPropertiesFile(Config config) {
        return new File(config.getConfigDir(), "app.properties");
    }

    @Override
    public void save(Config config) {
        final File propertiesFile = getPropertiesFile(config);
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(propertiesFile);
            this.properties.store(stream, "  " + config.getApplicationName() + " config");
        } catch(IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "at file: " + propertiesFile, e);
        } finally {
            Closeables.close(stream);
        }
    }
}
