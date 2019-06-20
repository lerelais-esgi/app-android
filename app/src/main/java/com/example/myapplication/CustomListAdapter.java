package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class CustomListAdapter extends ArrayAdapter<Product> {
    ArrayList<Product> products;
    Context context;
    int resource;

    public CustomListAdapter(Context context, int resource, ArrayList<Product> products) {
        super(context, resource, products);
        this.products = products;
        this.context = context;
        this.resource = resource;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext().
                    getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item, null, true);

        }
        Product product = getItem(position);
        TextView product_name = (TextView) convertView.findViewById(R.id.product_name);
        product_name.setText(product.getName());
        TextView expiration_date = (TextView) convertView.findViewById(R.id.expiration_date);
        expiration_date.setText(product.getExpiration_date());


        return convertView;
    }
}

