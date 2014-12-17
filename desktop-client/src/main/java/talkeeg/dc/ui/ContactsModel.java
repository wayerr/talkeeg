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

import com.google.common.base.Function;
import com.google.common.base.Functions;
import talkeeg.bf.Arrays;
import talkeeg.bf.Int128;
import talkeeg.common.core.*;
import talkeeg.common.model.ClientAddress;
import talkeeg.common.model.ClientAddresses;
import talkeeg.common.model.ClientIdentityCard;
import talkeeg.common.model.UserIdentityCard;
import talkeeg.common.util.StringUtils;
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
        void load(Node<T> parent, List<Node<?>> childs);
    }

    private final NodeLoader<AcquaintedUser> loaderClients = new NodeLoader<AcquaintedUser>() {
        @Override
        public void load(Node<AcquaintedUser> parent, List<Node<?>> childs) {
            final AcquaintedUser user = parent.getValue();
            List<AcquaintedClient> clients = acquaintedClients.getUserClients(user.getId());
            for(AcquaintedClient client : clients) {
                childs.add(new Node<>(client, ContactsModel.this::clientStringifier));
            }
        }
    };

    private final List<TreeModelListener> listeners = new ArrayList<>();
    private final AcquaintedUsersService acquaintedUsers;
    private final AcquaintedClientsService acquaintedClients;
    private final ClientsAddressesService clientsAddresses;
    private final MessageBusRegistry registry;
    private final ListNode root = new ListNode<Object>(null, Functions.toStringFunction(), new NodeLoader<Object>() {
        @Override
        public void load(Node<Object> parent, List<Node<?>> value) {
            final List<AcquaintedUser> users = acquaintedUsers.getAcquaintedUsers();
            for(final AcquaintedUser user: users) {
                value.add(new ListNode<>(user, ContactsModel.this::userStringifier, loaderClients));
            }
        }
    });

    @Inject
    public ContactsModel(MessageBusRegistry registry,
                         AcquaintedUsersService acquaintedUsers,
                         AcquaintedClientsService acquaintedClients,
                         ClientsAddressesService clientsAddresses) {
        this.registry = registry;
        this.acquaintedUsers = acquaintedUsers;
        this.acquaintedClients = acquaintedClients;
        this.clientsAddresses = clientsAddresses;

        this.registry.getOrCreateBus(AcquaintedUsersService.MB_KEY).register(event -> reloadTree());
        this.registry.getOrCreateBus(AcquaintedClientsService.MB_KEY).register(event -> reloadTree());

        reloadTree();
    }

    private String userStringifier(AcquaintedUser user) {
        UserIdentityCard identityCard = user.getIdentityCard();
        Object string = identityCard.getAttrs().get(UserIdentityCard.ATTR_NICK);
        if(string == null) {
            string = Arrays.toHexString(user.getId().getData());
        }
        return string.toString();
    }

    private String clientStringifier(AcquaintedClient client) {
        final Int128 id = client.getId();
        String result = null;
        final List<ClientAddress> addresses = this.clientsAddresses.getAddresses(id);
        if(addresses != null) {
            result = StringUtils.print(addresses);
        }
        if(result == null) {
            result = Arrays.toHexString(id.getData());
        }
        return result;
    }

    protected void reloadTree() {
        root.reload();
        fireStructureChanged(new TreeModelEvent(this, new Object[]{this.root}));
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

    public class Node<T> {
        private final T value;
        private final Function<T, String> stringifier;

        private Node(T value, Function<T, String> stringifier) {
            this.value = value;
            this.stringifier = stringifier;
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
            if(this.value == null) {
                return "null";
            }
            if(this.stringifier != null) {
                return this.stringifier.apply(this.value);
            }
            return String.valueOf(value);
        }
    }

    private final class ListNode<T> extends Node<T> {
        private final List<Node<?>> list = new ArrayList<>();
        private final NodeLoader loader;

        private ListNode(T value, Function<T, String> stringifier, NodeLoader loader) {
            super(value, stringifier);
            this.loader = loader;
        }

        @Override
        @SuppressWarnings("unchecked")
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
