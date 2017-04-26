package edu.csulb.android.friendfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FriendAdapter extends ArrayAdapter {

    private final List friendsList;

    public FriendAdapter(Context context, int resource, List friendsList) {
        super(context, resource, friendsList);
        this.friendsList = friendsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.row, null);

        // setting friend name
        TextView friends_name = (TextView) row.findViewById(R.id.friends_name);
        friends_name.setText(friendsList.get(position).toString());

        return row;
    }
}
