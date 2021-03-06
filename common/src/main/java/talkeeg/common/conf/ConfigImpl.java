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

package talkeeg.common.conf;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.io.File;

/**
 * {@link Config} impelmentation
 * Created by wayerr on 26.11.14.
 */
public final class ConfigImpl implements Config {

    public static class Builder {
        private String applicationName;
        private Function<String, File> configDirFunction = DefaultConfigDirFunction.INSTANCE;
        private Function<String, File> cacheDirFunction;
        private ConfigBackend backend;

        /**
         * application specific name in [a-z.-_] letters <p/>
         * usualy used as prefix for files, tmp dir names and etc.
         * @return
         */
        public String getApplicationName() {
            return applicationName;
        }

        /**
         * application specific name in [a-z.-_] letters <p/>
         * usualy used as prefix for files, tmp dir names and etc.
         * @param applicationName
         * @return
         */
        public Builder applicationName(String applicationName) {
            setApplicationName(applicationName);
            return this;
        }

        /**
         * application specific name in [a-z.-_] letters <p/>
         * usualy used as prefix for files, tmp dir names and etc.
         * @param applicationName
         */
        public void setApplicationName(String applicationName) {
            this.applicationName = applicationName;
        }

        public ConfigBackend getBackend() {
            return backend;
        }

        public Builder backend(ConfigBackend backend) {
            setBackend(backend);
            return this;
        }

        public void setBackend(ConfigBackend backend) {
            this.backend = backend;
        }

        /**
         * function which provide configuration dir based on app name
         * @see talkeeg.common.conf.DefaultConfigDirFunction
         * @see Config#getConfigDir()
         * @return
         */
        public Function<String, File> getConfigDirFunction() {
            return configDirFunction;
        }

        /**
         * * function which provide configuration dir based on app name
         * @see talkeeg.common.conf.DefaultConfigDirFunction
         * @see Config#getConfigDir()
         * @param configDirFunction
         * @return
         */
        public Builder configDirFunction(Function<String, File> configDirFunction) {
            setConfigDirFunction(configDirFunction);
            return this;
        }

        /**
         * function which provide configuration dir based on app name
         * @see talkeeg.common.conf.DefaultConfigDirFunction
         * @see Config#getConfigDir()
         * @param configDirFunction
         */
        public void setConfigDirFunction(Function<String, File> configDirFunction) {
            this.configDirFunction = configDirFunction;
        }

        public Function<String, File> getCacheDirFunction() {
            return cacheDirFunction;
        }

        public void setCacheDirFunction(Function<String, File> cacheDirFunction) {
            this.cacheDirFunction = cacheDirFunction;
        }

        public ConfigImpl build() {
            return new ConfigImpl(this);
        }
    }

    private final String applicationName;
    private final ConfigBackend backend;
    private final NodeImpl root;
    private final Function<String, File> configDirFunction;
    private final Function<String, File> cacheDirFunction;
    private volatile File _configDir;

    private ConfigImpl(Builder b) {
        this.applicationName = b.applicationName;
        this.configDirFunction = b.configDirFunction;
        this.cacheDirFunction = b.cacheDirFunction;
        this.backend = b.backend;
        Preconditions.checkNotNull(this.backend, "backend is null");
        this.root = new NodeImpl(this);

        this.backend.load(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * application specific name in [a-z.-_] letters <p/>
     * usualy used as prefix for files, tmp dir names and etc.
     * @return
     */
    @Override
    public String getApplicationName() {
        return applicationName;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getValue(String name, T defaultValue) {
        return this.backend.getValue(this, name, defaultValue);
    }

    @Override
    public <T> void setValue(String name, T value) {
        this.backend.setValue(this, name, value);
    }

    @Override
    public Node getRoot() {
        return root;
    }

    @Override
    public File getConfigDir() {
        if(_configDir == null) {
            _configDir = this.configDirFunction.apply(applicationName);
        }
        return _configDir;
    }

    @Override
    public void save() {
        this.backend.save(this);
    }
}
