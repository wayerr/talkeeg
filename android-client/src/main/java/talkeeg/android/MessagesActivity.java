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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import talkeeg.bf.BinaryData;
import talkeeg.bf.Int128;
import talkeeg.common.core.DataService;
import talkeeg.common.model.Constants;
import talkeeg.common.model.Data;
import talkeeg.common.util.Callback;
import talkeeg.common.util.Closeable;
import talkeeg.common.util.Closeables;

import java.util.ArrayList;
import java.util.List;

/**
 * activity of messages list
 * Created by wayerr on 22.12.14.
 */
public final class MessagesActivity extends Activity {

    private DataService dataService;
    private Int128 clientId;
    private MessagesListAdapter listAdapter;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_activity);


        this.dataService = App.get(DataService.class);
        this.listAdapter = new MessagesListAdapter(this, R.layout.messages_history_view, dataService);


        ListView messagesList = (ListView)findViewById(R.id.messagesListView);
        messagesList.setAdapter(this.listAdapter);
    }

    @Override
    protected void onDestroy() {
        Closeables.close(this.listAdapter);
        super.onDestroy();
    }

    public void sendMessageAction(View view) {
        if(this.clientId == null) {
            return;
        }
        BinaryData data = new BinaryData("тест уникода".getBytes());
        this.dataService.push(this.clientId, Data.buidler().action(Constants.DATA_ACTION_CHAT).data(data).build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private static final class MessagesListAdapter extends BaseAdapter implements Closeable {

        private final DataService service;
        private final LayoutInflater inflater;
        private final int resource;
        private final List<Data> history = new ArrayList<>();
        private final Callback<Data> chatCallback = new Callback<Data>() {
            @Override
            public void call(Data value) {
                history.add(value);
            }
        };
        private final Closeable callbackEnd;

        private MessagesListAdapter(Context context, int resource, DataService service) {
            this.service = service;
            this.resource = resource;
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            this.callbackEnd = this.service.addHandler(Constants.DATA_ACTION_CHAT, chatCallback);
        }

        @Override
        public int getCount() {
            return this.history.size();
        }

        @Override
        public Object getItem(int i) {
            return this.history.get(i);
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
            TextView sourceView = (TextView)itemView.findViewById(R.id.messagesListItemSource);
            TextView dataView  = (TextView)itemView.findViewById(R.id.messagesListItemData);

            Data data = null;
            if(this.history.size() > position) {
                data = this.history.get(position);
            }
            if(data == null) {
                sourceView.setText(null);
                dataView.setText(null);
            } else {
                sourceView.setText(data.getAction());
                final BinaryData binaryData = data.getData();
                dataView.setText(new String(binaryData.getData()));
            }

            return itemView;
        }

        @Override
        public void close() {
            Closeables.close(this.callbackEnd);
        }
    }
}