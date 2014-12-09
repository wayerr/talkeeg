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

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import talkeeg.common.core.AcquaintedUser;
import talkeeg.common.core.AcquaintedUsersService;
import talkeeg.common.model.UserIdentityCard;

import java.util.List;

/**
 * list of acquainted users
 * Created by wayerr on 08.12.14.
 */
public final class AcquaintedUsersFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.acquainted_users_fragment, container, false);
        ListView listView = (ListView)inflate.findViewById(R.id.acquaintedUsersList);
        AcquaintedUsersService service = ((App)getActivity().getApplication()).get(AcquaintedUsersService.class);
        listView.setAdapter(new AcquaintedUserListAdapter(getActivity(), R.layout.acquainted_user_view, service));
        return inflate;
    }

    private static final class AcquaintedUserListAdapter extends BaseAdapter {

        private final AcquaintedUsersService service;
        private final LayoutInflater inflater;
        private final int resource;
        private List<AcquaintedUser> userList;

        private AcquaintedUserListAdapter(Context context, int resource, AcquaintedUsersService service) {

            this.service = service;
            this.resource = resource;
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.userList = this.service.getAcquaintedUsers();
        }

        @Override
        public int getCount() {
            return this.userList.size();
        }

        @Override
        public Object getItem(int i) {
            return this.userList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView;
            if(convertView == null) {
                itemView = this.inflater.inflate(this.resource, parent, false);
            } else {
                itemView = convertView;
            }
            TextView nickView = (TextView)itemView.findViewById(R.id.acquaintedUserNick);
            TextView fingerprintView  = (TextView)itemView.findViewById(R.id.acquaintedUserFingerprint);

            AcquaintedUser user = null;
            if(this.userList != null && this.userList.size() > position) {
                user = this.userList.get(position);
            }
            if(user == null) {
                nickView.setText(null);
                fingerprintView.setText(null);
            } else {
                final UserIdentityCard identityCard = user.getIdentityCard();
                nickView.setText(String.valueOf(identityCard.getAttrs().get(UserIdentityCard.ATTR_NICK)));
                fingerprintView.setText(String.valueOf(identityCard.getKey()));
            }

            return itemView;
        }
    }
}