package com.example.mystore.ui.allCommand;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.mystore.R;
import com.example.mystore.database.Command;

import java.util.ArrayList;

public class CommandAdapter extends ArrayAdapter<Command> {

    public CommandAdapter(Context context, int resource, ArrayList<Command> commands) {
        super(context, resource, commands);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Command command = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.command_layout, parent, false);
        }

        TextView tvUserName = convertView.findViewById(R.id.tv_user_name_command);
        TextView tvCommand = convertView.findViewById(R.id.tv_command);
        TextView tvScore = convertView.findViewById(R.id.tv_score);

        tvUserName.setText(command.getUser());
        tvCommand.setText(command.getComment());
        tvScore.setText(String.format("評分: %.1f", command.getRating()));

        return convertView;
    }
}
