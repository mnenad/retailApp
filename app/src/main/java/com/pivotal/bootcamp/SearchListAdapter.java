package com.pivotal.bootcamp;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by administrator on 2015-05-07.
 */
public class SearchListAdapter extends ArrayAdapter<BestBuyItem> {
    private final Context context;
    private final ArrayList<BestBuyItem> list;
    int layout_res = R.layout.bestbuy_list_item;

    public SearchListAdapter(Context context, ArrayList<BestBuyItem> list) {
        super(context, R.layout.bestbuy_list_item, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItemView = inflater.inflate(layout_res, parent, false);

        TextView itemName = (TextView) listItemView.findViewById(R.id.itemName);
        TextView itemPrice = (TextView) listItemView.findViewById(R.id.itemPrice);
        ImageView itemImage = (ImageView) listItemView.findViewById(R.id.imageView);

        itemName.setText(list.get(position).getName());
        itemPrice.setText(list.get(position).getPrice().toString());
        new ImageDownloader(itemImage).execute(list.get(position).getThumbUrl());

        return listItemView;
    }
}
