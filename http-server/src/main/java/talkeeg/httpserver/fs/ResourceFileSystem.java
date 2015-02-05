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

package talkeeg.httpserver.fs;

import com.google.common.base.Preconditions;

import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * a VFS for resources <p/>
 *
 * Created by wayerr on 05.02.15.
 */
public final class ResourceFileSystem implements VirtualFileSystem<ResourceFileSystem.ResourceFile> {
    public final class ResourceFile implements VirtualFile {
        private final String path;
        private final URL resource;
        private URLConnection connection;

        private ResourceFile(String path, URL resource) throws Exception {
            this.path = path;
            Preconditions.checkNotNull(path, "path is null");
            this.resource = resource;
            Preconditions.checkNotNull(resource, "resource is null, at path: %s", path);
        }

        @Override
        public String getName() throws Exception {
            String path = getPath();
            if(!path.isEmpty()) {
                int slashPos = getLastSlashPos(path);
                path = path.substring(slashPos);
            }
            return path;
        }

        protected String getPath() {
            return path;
        }

        @Override
        public long getSize() throws Exception {
            if(isDirectory()) {
                return -1;
            }
            return getConnection().getContentLength();
        }

        @Override
        public String getMimeType() throws Exception {
            if(isDirectory()) {
                return null;
            }
            return getConnection().getContentType();
        }

        private URLConnection getConnection() throws Exception {
            if(this.connection == null) {
                this.connection = resource.openConnection();
            }
            return connection;
        }

        @Override
        public boolean isDirectory() throws Exception {
            URLConnection conn = getConnection();
            if(conn instanceof JarURLConnection) {
                JarURLConnection jc = (JarURLConnection)conn;
                return jc.getJarEntry().isDirectory();
            }
            //is behaviour based on analyzing of FileURLConnection sources
            return conn.getHeaderField("content-length") == null;
        }

        @Override
        public void getChilds(List<VirtualFile> childs) throws Exception {
            //list of child elements is not supported
            // but if us very need it, then we can manually enumerate jar or directory entries
        }

        @Override
        public VirtualFile getParent() throws Exception {
            String path = getPath();
            if(path.isEmpty()) {
                return null;
            }
            int slashPos = getLastSlashPos(path);
            path = path.substring(0, slashPos);
            return getResourceFile(path);
        }

        private int getLastSlashPos(String path) {
            return path.lastIndexOf('/', path.length() - 2);
        }

        @Override
        public InputStream openInputStream() throws Exception {
            if(isDirectory()) {
                return null;
            }
            return getConnection().getInputStream();
        }

        @Override
        public String toString() {
            return "ResourceFile{" + getPath() + '}';
        }
    }

    private static final Pattern PATTERN = Pattern.compile("\\.\\.[\\\\/]|[\\|;:\\\\]");

    private final String prefix;
    private final ClassLoader cl;

    public ResourceFileSystem(String prefix) {
        Preconditions.checkNotNull(prefix, "prefix is null");
        // if resource start with '/' then classLoader never provide resource, root resource is ''
        if(prefix.startsWith("/")) {
            prefix = prefix.substring(1);
        }
        this.prefix = prefix;
        this.cl = getClass().getClassLoader();
    }

    @Override
    public ResourceFile fromPath(String name) throws Exception {
        checkName(name);
        return getResourceFile(name);
    }

    private ResourceFile getResourceFile(String name) throws Exception {
        String path = toFullPath(name);
        URL resource = cl.getResource(path);
        if(resource == null) {
            return null;
        }
        return new ResourceFile(path, resource);
    }

    private String toFullPath(String name) {
        return concatPath(this.prefix, name);
    }

    private static String concatPath(String prefix, String name) {
        if(name.startsWith("/")) {
            name = name.substring(1);
        }
        if(!prefix.endsWith("/")) {
            return prefix + "/" + name;
        }
        return prefix + name;
    }

    private void checkName(String name) {
        if(PATTERN.matcher(name).find()) {
            throw new IllegalArgumentException("Path '" + name + "' is invalid.");
        }
    }

    @Override
    public String toPath(ResourceFile childFile) throws Exception {
        String file = childFile.getPath();
        if(!file.startsWith(this.prefix)) {
            throw new IllegalArgumentException("Unexpected file name, expect that it start with '" + this.prefix + "'.");
        }
        return file.substring(this.prefix.length());
    }
}
