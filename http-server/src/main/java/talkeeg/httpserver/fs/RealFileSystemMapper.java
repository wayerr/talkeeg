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
import com.google.common.primitives.Booleans;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * a projection of real file system to {@link talkeeg.httpserver.fs.VirtualFileSystem }
 * Created by wayerr on 30.01.15.
 */
public final class RealFileSystemMapper implements VirtualFileSystem<RealFileSystemMapper.RealFile> {

    private static final Comparator<File> COMPARATOR = new Comparator<File>() {
        @Override
        public int compare(File lhs, File rhs) {
            int res = -Booleans.compare(lhs.isDirectory(), rhs.isDirectory());
            if(res == 0) {
                res = lhs.getName().compareTo(rhs.getName());
            }
            return res;
        }
    };

    public class RealFile implements VirtualFile {
        private final File file;

        private RealFile(File file) {
            Preconditions.checkState(file.isAbsolute(), "File must be an absolute");
            this.file = file;
        }

        @Override
        public String getName() {
            return file.getName();
        }

        @Override
        public long getSize() {
            if(file.isDirectory()) {
                return -1l;
            }
            return file.length();
        }

        @Override
        public String getMimeType() throws Exception {
            if(file.isDirectory()) {
                return null;
            }
            return Files.probeContentType(this.file.toPath());
        }

        @Override
        public boolean isDirectory() {
            return file.isDirectory();
        }

        @Override
        public void getChilds(List<VirtualFile> childs) throws Exception {
            list(this, childs);
        }

        @Override
        public VirtualFile getParent() throws Exception {
            final File parentFile = file.getParentFile();
            final File canonicalParentFile = parentFile.getCanonicalFile();
            if(isExternal(canonicalParentFile)) {
                return null;
            }
            return new RealFile(canonicalParentFile);
        }

        @Override
        public InputStream openInputStream() throws Exception {
            return new FileInputStream(file);
        }
    }

    protected void checkThatInRoot(File canonicalFile) throws IOException {
        //we apologise than `canonicalFile` does not contains any '.' characters
        if(isExternal(canonicalFile)) {
            throw new IOException("Try to list external path.");
        }
    }

    private boolean isExternal(File canonicalFile) {
        return !canonicalFile.getPath().startsWith(this.root.getPath());
    }

    private static final Pattern PATTERN;
    static {
        // linux allow some this chars in file path, but making pattern for each OS is a few difficult
        String regexp = "\\.\\.[\\\\/]|[\\|;:\\\\]";
        PATTERN = Pattern.compile(regexp);
    }
    private final File root;

    public RealFileSystemMapper(File root) {
        try {
            this.root = root.getCanonicalFile();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RealFile fromPath(String name) throws Exception {
        if(PATTERN.matcher(name).find()) {
            throw new RuntimeException("Invalid filename: " + name);
        }
        final File canonicalFile = new File(this.root, name).getCanonicalFile();
        checkThatInRoot(canonicalFile);
        return new RealFile(canonicalFile);
    }

    @Override
    public String toPath(RealFile childFile) throws Exception {
        String path = childFile.file.getCanonicalPath();
        String rootPath = this.root.getPath();
        if(!path.startsWith(rootPath)) {
            throw new IOException("Try to access external path.");
        }
        return path.substring(rootPath.length());
    }

    private void list(RealFile element, List<VirtualFile> childs) {
        final File dir;
        if(element == null) {
           dir = this.root;
        } else {
            if(!element.isDirectory()) {
                return;
            }
            dir = element.file;
        }
        File[] files = dir.listFiles();
        Arrays.sort(files, COMPARATOR);
        for(File file: files) {
            if(deny(file)) {
                continue;
            }
            childs.add(new RealFile(file));
        }
    }

    private boolean deny(File file) {
        //there we can add custom filtering
        return file.isHidden();
    }
}
