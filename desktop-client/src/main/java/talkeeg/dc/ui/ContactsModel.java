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

package talkeeg.dc.ui;

import talkeeg.common.core.AcquaintedClient;
import talkeeg.common.core.AcquaintedClientsService;
import talkeeg.common.core.AcquaintedUser;
import talkeeg.common.core.AcquaintedUsersService;
import talkeeg.mb.MessageBusRegistry;

import javax.inject.Inject;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

/**
 * model of contacts list <p/>
 *
 * TODO we need correctly fire tree model events
 * Created by wayerr on 11.12.14.
 */
final class ContactsModel implements TreeModel {

    private interface NodeLoader<T> {
        void load(Node<T> parent, List<Node<Object>> childs);
    }

    private final NodeLoader<AcquaintedUser> loaderClients = new NodeLoader<AcquaintedUser>() {
        @Override
        public void load(Node<AcquaintedUser> parent, List<Node<Object>> childs) {
            final AcquaintedUser user = parent.getValue();
            List<AcquaintedClient> clients = acquaintedClients.getUserClients(user.getId());
            for(AcquaintedClient client : clients) {
                childs.add(new Node<>(client));
            }
        }
    };

    private final List<TreeModelListener> listeners = new ArrayList<>();
    private final AcquaintedUsersService acquaintedUsers;
    private final AcquaintedClientsService acquaintedClients;
    private final MessageBusRegistry registry;
    private final ListNode root = new ListNode<Object>(null, new NodeLoader<Object>() {
        @Override
        public void load(Node<Object> parent, List<Node<Object>> value) {
            final List<AcquaintedUser> users = acquaintedUsers.getAcquaintedUsers();
            for(final AcquaintedUser user: users) {
                value.add(new ListNode<>(user, loaderClients));
            }
        }
    });

    @Inject
    public ContactsModel(MessageBusRegistry registry, AcquaintedUsersService acquaintedUsers, AcquaintedClientsService acquaintedClients) {
        this.registry = registry;
        this.acquaintedUsers = acquaintedUsers;
        this.acquaintedClients = acquaintedClients;
        this.registry.getOrCreateBus(AcquaintedUsersService.MB_KEY).register(event -> root.reload());
        this.registry.getOrCreateBus(AcquaintedClientsService.MB_KEY).register(event -> root.reload());
        root.reload();
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        return ((Node<?>)parent).getChild(index);
    }

    @Override
    public int getChildCount(Object parent) {
        return ((Node<?>)parent).getChildCount();
    }

    @Override
    public boolean isLeaf(Object node) {
        return ((Node<?>)node).isLeaf();
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {

    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return ((Node<?>)parent).getIndexOfChild(child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    protected void fireNodesChanged(TreeModelEvent e) {
        for(TreeModelListener listener: listeners) {
            listener.treeNodesChanged(e);
        }
    }

    protected void fireNodesInserted(TreeModelEvent e) {
        for(TreeModelListener listener: listeners) {
            listener.treeNodesInserted(e);
        }
    }

    protected void fireNodesRemoved(TreeModelEvent e) {
        for(TreeModelListener listener: listeners) {
            listener.treeNodesRemoved(e);
        }
    }

    protected void fireStructureChanged(TreeModelEvent e) {
        for(TreeModelListener listener: listeners) {
            listener.treeStructureChanged(e);
        }
    }

    private class Node<T> {
        private final T value;

        public Node(T value) {
            this.value = value;
        }

        void reload() {
            //nothing
        }

        public T getValue() {
            return value;
        }

        public Object getChild(int index) {
            return null;
        }

        public int getChildCount() {
            return 0;
        }

        public int getIndexOfChild(Object child) {
            return 0;
        }

        public boolean isLeaf() {
            return true;
        }

        public String toString() {
            return String.valueOf(value);
        }
    }

    private final class ListNode<T> extends Node<T> {
        private final List<Node<?>> list = new ArrayList<>();
        private final NodeLoader loader;

        private ListNode(T value, NodeLoader loader) {
            super(value);
            this.loader = loader;
        }

        @Override
        void reload() {
            this.list.clear();
            this.loader.load(this, this.list);
            for(Node<?> node: this.list) {
                node.reload();
            }
        }

        @Override
        public Object getChild(int index) {
            return list.get(index);
        }

        @Override
        public int getIndexOfChild(Object child) {
            return list.indexOf(child);
        }

        @Override
        public int getChildCount() {
            return list.size();
        }

        @Override
        public boolean isLeaf() {
            return false;
        }
    }
}
