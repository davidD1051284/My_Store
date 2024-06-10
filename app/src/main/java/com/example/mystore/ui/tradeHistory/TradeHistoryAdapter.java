package com.example.mystore.ui.tradeHistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mystore.R;
import com.example.mystore.database.TradeHistory;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TradeHistoryAdapter extends ArrayAdapter<TradeHistory> {

    private Context context;
    private List<TradeHistory> tradeHistoryList;

    public TradeHistoryAdapter(Context context, List<TradeHistory> tradeHistoryList) {
        super(context, 0, tradeHistoryList);
        this.context = context;
        this.tradeHistoryList = tradeHistoryList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.trade_history_layout, parent, false);
        }

        TradeHistory tradeHistory = tradeHistoryList.get(position);

        TextView productNameTextView = listItemView.findViewById(R.id.tv_product_name_th);
        productNameTextView.setText(tradeHistory.getProductName());

        TextView quantityTextView = listItemView.findViewById(R.id.tv_quantity_th);
        quantityTextView.setText(String.format(Locale.getDefault(), "數量: %d", tradeHistory.getQuantity()));

        TextView totalPriceTextView = listItemView.findViewById(R.id.tv_total_price_th);
        totalPriceTextView.setText(String.format(Locale.getDefault(), "NT$%d", tradeHistory.getTotalPrice()));

        TextView dateTextView = listItemView.findViewById(R.id.tv_date_th);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        String formattedDate = sdf.format(tradeHistory.getTradeDate());
        dateTextView.setText(formattedDate);

        TextView sellerTextView = listItemView.findViewById(R.id.tv_seller_th);
        sellerTextView.setText(String.format("賣家: %s", tradeHistory.getSeller()));

        return listItemView;
    }
}
