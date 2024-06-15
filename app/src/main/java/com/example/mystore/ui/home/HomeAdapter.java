package com.example.mystore.ui.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mystore.R;

import com.bumptech.glide.Glide;
import com.example.mystore.product_information;

import java.util.List;

public class HomeAdapter extends ArrayAdapter<HomeItem> {
    private Context mContext;
    private int mResource;

    TextView productName;
    TextView productPrice;
    ImageView productImage;
    Button productMore;


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
        String productId = getItem(position).getProductId();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        productName = convertView.findViewById(R.id.tv_home_product_name);
        productPrice = convertView.findViewById(R.id.tv_home_product_price);
        productImage = convertView.findViewById(R.id.iv_home_product_image);
        productMore = convertView.findViewById(R.id.btn_home_product_more);

        setProductName(name);
        productPrice.setText("NT$" + String.valueOf(price));
        Glide.with(mContext).load(imageUrl).into(productImage);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, product_information.class);
                intent.putExtra("PRODUCT_ID", productId);
                mContext.startActivity(intent);
            }
        };

        productMore.setOnClickListener(listener);


        return convertView;
    }

    private void setProductName(String name) {
        int maxLength = 30;
        if (name.length() > maxLength) {
            name = name.substring(0, maxLength) + "...";
        }
        productName.setText(name);
    }
}



