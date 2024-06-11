package com.example.mystore.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mystore.R;

import com.bumptech.glide.Glide;

import java.util.List;

public class HomeAdapter extends ArrayAdapter<HomeItem> {
    private Context mContext;
    private int mResource;


    public HomeAdapter(@NonNull android.content.Context context, int resource, @NonNull List<HomeItem> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position).getProductName();
        int price = getItem(position).getProductPrice();
        String imageUrl = getItem(position).getImageUrl();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView productName = convertView.findViewById(R.id.tv_home_product_name);
        TextView productPrice = convertView.findViewById(R.id.tv_home_product_price);
        ImageView productImage = convertView.findViewById(R.id.iv_home_product_image);

        productName.setText(name);
        productPrice.setText("NT$" + String.valueOf(price));
        Glide.with(mContext).load(imageUrl).into(productImage);

        return convertView;
    }
}
