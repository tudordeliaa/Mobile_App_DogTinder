package com.example.licenta_tinder_caini.Cards;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.licenta_tinder_caini.R;

import java.util.List;

public class arrayAdapter extends ArrayAdapter<cards>{
    Context context;

    public arrayAdapter(Context context, int resourceId, List<cards> items){
        super(context,resourceId,items);
    }

    //populate de cards-each card
    public View getView(int position, View convertView, ViewGroup parent){
        cards card_item = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        //do the find view by IDs for each item -> item.xml
        //for the textView
        TextView name = (TextView) convertView.findViewById(R.id.name);
        //for the imageview
        ImageView image = (ImageView) convertView.findViewById(R.id.image);

        name.setText(card_item.getName());
        switch(card_item.getProfileImageUrl()) {
            case "default":
                Glide.with(convertView.getContext()).load(R.drawable.cocker).into(image);
                break;

            default:
                Glide.clear(image);
                Glide.with(convertView.getContext()).load(card_item.getProfileImageUrl()).into(image);
                break;
        }

        return convertView;
    }
}
