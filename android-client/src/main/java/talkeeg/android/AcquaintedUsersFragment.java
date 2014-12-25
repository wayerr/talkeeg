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

package talkeeg.android;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.common.base.Function;
import talkeeg.android.util.CheckableLayout;
import talkeeg.android.util.ConfirmAction;
import talkeeg.common.core.*;
import talkeeg.common.util.Stringifiers;
import talkeeg.mb.Listener;
import talkeeg.mb.MessageBusRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * list of acquainted users
 * Created by wayerr on 08.12.14.
 */
public final class AcquaintedUsersFragment extends Fragment {

    private AcquaintedUserListAdapter adapter;
    private Listener<Object> listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.adapter = new AcquaintedUserListAdapter(getActivity());
        final MessageBusRegistry registry = App.get(MessageBusRegistry.class);
        this.listener = new Listener<Object>() {
            @Override
            public void listen(Object event) throws Exception {
                adapter.reload();
            }
        };
        registry.getOrCreateBus(AcquaintedClientsService.MB_KEY).register(listener);
        registry.getOrCreateBus(AcquaintedUsersService.MB_KEY).register(listener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.acquainted_users_fragment, container, false);
        final ListView listView = (ListView)inflate.findViewById(R.id.acquaintedUsersList);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            final CurrentDestinationService currentDestination = App.get(CurrentDestinationService.class);
            private int oldCheckedPosition = -1;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // here item always checked
                if(oldCheckedPosition == position) {
                    listView.setItemChecked(position, false);
                    oldCheckedPosition = -1;
                } else {
                    oldCheckedPosition = position;
                }
                AcquaintedClient selectedClient = (AcquaintedClient)listView.getAdapter().getItem(position);
                updateDestination(selectedClient);
            }

            protected void updateDestination(AcquaintedClient selectedClient) {
                currentDestination.setClientId(selectedClient == null? null : selectedClient.getId());
                currentDestination.setUserId(selectedClient == null? null : selectedClient.getUserId());
            }
        });
        return inflate;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        final MessageBusRegistry registry = App.get(MessageBusRegistry.class);
        registry.getOrCreateBus(AcquaintedClientsService.MB_KEY).unregister(listener);
        registry.getOrCreateBus(AcquaintedUsersService.MB_KEY).unregister(listener);
    }

    private static final class AcquaintedUserListAdapter extends BaseAdapter {

        private final AcquaintedUsersService usersService;
        private final AcquaintedClientsService clientsService;
        private final Function<AcquaintedClient, String> clientStringifier;
        private final Function<AcquaintedUser, String> userStringifier;
        private final List<AcquaintedClient> clientList = new ArrayList<>();
        private final Context context;

        private AcquaintedUserListAdapter(Context context) {
            this.context = context;
            this.usersService = App.get(AcquaintedUsersService.class);
            this.clientsService = App.get(AcquaintedClientsService.class);
            final Stringifiers stringifiers = App.get(Stringifiers.class);
            this.clientStringifier = stringifiers.getToStringFunction(AcquaintedClient.class);
            this.userStringifier = stringifiers.getToStringFunction(AcquaintedUser.class);
            reload();
        }

        private void reload() {
            this.clientList.clear();
            this.clientList.addAll(this.clientsService.getClients());
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return this.clientList.size();
        }

        @Override
        public Object getItem(int i) {
            return this.clientList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView;
            if(convertView == null) {
                itemView = new CheckableLayout(context, R.layout.acquainted_user_view, R.id.acquaintedUserCheckBox);
            } else {
                itemView = convertView;
            }
            TextView nickView = (TextView)itemView.findViewById(R.id.acquaintedUserNick);
            TextView fingerprintView  = (TextView)itemView.findViewById(R.id.acquaintedUserFingerprint);
            View delButton = itemView.findViewById(R.id.acquaintedUserDel);


            final AcquaintedUser user;
            final AcquaintedClient client;
            if(this.clientList != null && this.clientList.size() > position) {
                client = this.clientList.get(position);
                user = this.usersService.getUser(client.getUserId());
            } else {
                client = null;
                user = null;
            }
            delButton.setOnClickListener(new ConfirmAction()
              .messageResource(R.string.dialog_confirm_delete_content)
              .onYes(new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                      if(user != null) {
                          usersService.remove(user.getId());
                      }
                      if(client != null) {
                          clientsService.remove(client.getId());
                      }
                  }
              }));
            nickView.setText(this.userStringifier.apply(user));
            fingerprintView.setText(this.clientStringifier.apply(client));
            return itemView;
        }
    }
}