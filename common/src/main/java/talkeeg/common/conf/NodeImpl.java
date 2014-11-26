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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * node implementation <p/>
 * Created by wayerr on 26.11.14.
 */
final class NodeImpl implements Node {
    private final String prefix;
    private final ConfigImpl config;
    private final ConcurrentMap<String, NodeImpl> childs = new ConcurrentHashMap<>();
    private final NodeImpl parent;

    NodeImpl(NodeImpl parent, String name) {
        this.parent = parent;
        this.prefix = parent.getEnclosedName(name);
        this.config = parent.config;
    }

    NodeImpl(ConfigImpl config) {
        this.parent = null;
        this.prefix = null;
        this.config = config;
    }

    @Override
    public Node getNode(String name) {
        final int pointIndex = name.indexOf('.');
        if(pointIndex >= 0) {
            final String childName = name.substring(0, pointIndex);
            final Node child = getNodeInner(childName);
            return child.getNode(name.substring(pointIndex + 1));
        }
        return getNodeInner(name);
    }

    private Node getNodeInner(String name) {
        NodeImpl child = childs.get(name);
        if(child == null) {
            // if we do not have a required node, then we create it
            child = new NodeImpl(this, name);
            final NodeImpl oldChild = childs.putIfAbsent(name, child);
            if(oldChild != null) {
                child = oldChild;
            }
        }
        return child;
    }

    @Override
    public <T> T getValue(String name, T defaultValue) {
        final String enclosedName = getEnclosedName(name);
        return config.getValue(enclosedName, defaultValue);
    }

    private String getEnclosedName(String name) {
        if(this.prefix == null) {
            return name;
        }
        return this.prefix + '.' + name;
    }
}
