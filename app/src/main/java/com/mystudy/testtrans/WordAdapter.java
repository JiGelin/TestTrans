package com.mystudy.testtrans;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class WordAdapter extends ArrayAdapter {
    List<WordItem> list;
    int resourceId;

    public WordAdapter(Context context,int textViewResourceId,List<WordItem> list){
        super(context,textViewResourceId,list);
        this.list = list;
        this.resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        WordItem wordItem = list.get(position);
        TextView item_name = (TextView) view.findViewById(R.id.word_name);
        item_name.setText(""+wordItem.getWord());
        return view;
    }

}
