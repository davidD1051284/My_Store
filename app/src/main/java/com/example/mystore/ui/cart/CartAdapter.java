package com.example.mystore.ui.cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mystore.R;

import java.util.List;

public class CartAdapter extends BaseAdapter {
    private Context context;
    private List<CartItem> cartItems;
    private UpdateLumpSumListener updateLumpSumListener;

    public CartAdapter(Context context, List<CartItem> cartItems, UpdateLumpSumListener updateLumpSumListener) {
        this.context = context;
        this.cartItems = cartItems;
        this.updateLumpSumListener = updateLumpSumListener;
    }

    @Override
    public int getCount() {
        return cartItems.size();
    }

    @Override
    public Object getItem(int position) {
        return cartItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_shopcart_product, parent, false);
            holder = new ViewHolder();
            holder.ivProductImage = convertView.findViewById(R.id.iv_shopcart_image);
            holder.tvProductName = convertView.findViewById(R.id.tv_shopcart_product_name);
            holder.tvProductPrice = convertView.findViewById(R.id.tv_shopcart_product_price);
            holder.btnDelete = convertView.findViewById(R.id.btn_shopcart_delete);
            holder.btnDecrease = convertView.findViewById(R.id.btn_shopcart_decrease);
            holder.btnIncrease = convertView.findViewById(R.id.btn_shopcart_add);
            holder.tvProductNum = convertView.findViewById(R.id.tv_shopcart_product_amount);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CartItem cartItem = cartItems.get(position);

        holder.tvProductName.setText(cartItem.getProductName());
        holder.tvProductPrice.setText("NT$" + cartItem.getProductPrice());
        holder.tvProductNum.setText(String.valueOf(cartItem.getProductAmount()));

        Glide.with(context).load(cartItem.getProductImage()).into(holder.ivProductImage);

        // 購買數量增加按鈕
        holder.btnIncrease.setOnClickListener(v -> {
            if (cartItem.getProductAmount() >= 9) {
                return;
            }
            cartItem.setProductAmount(cartItem.getProductAmount() + 1);
            notifyDataSetChanged();
            updateLumpSumListener.updateLumpSum();
        });

        // 購買數量減少按鈕
        holder.btnDecrease.setOnClickListener(v -> {
            if (cartItem.getProductAmount() > 1) {
                cartItem.setProductAmount(cartItem.getProductAmount() - 1);
                notifyDataSetChanged();
                updateLumpSumListener.updateLumpSum();
            }
        });

        // 移出購物車按鈕
        holder.btnDelete.setOnClickListener(v -> {
            cartItems.remove(position);
            notifyDataSetChanged();
            updateLumpSumListener.updateLumpSum();
        });

        return convertView;
    }

    private static class ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName;
        TextView tvProductPrice;
        TextView tvProductNum;
        Button btnDelete;
        Button btnDecrease;
        Button btnIncrease;
    }

    public interface UpdateLumpSumListener {
        void updateLumpSum();
    }
}