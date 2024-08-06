package fpoly.pro1121.asm2client.Adapter;

import static fpoly.pro1121.asm2client.Service.ApiService.BASE_URL;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import fpoly.pro1121.asm2client.Model.Product;
import fpoly.pro1121.asm2client.R;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private final ArrayList<Product> list;
    private final Context context;

    public OrderAdapter(ArrayList<Product> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.rec_order, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = list.get(position);
        holder.tv_order_name.setText("Name :" + product.getDescription());
        holder.tv_order_price.setText("Price :" + product.getPrice() + " VND");
        String imageUrl = BASE_URL + product.getImageUrl();
        Glide.with(context).load(imageUrl).into(holder.img_order);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_order;
        TextView tv_order_price, tv_order_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_order = itemView.findViewById(R.id.img_order);
            tv_order_price = itemView.findViewById(R.id.tv_order_price);
            tv_order_name = itemView.findViewById(R.id.tv_order_name);
        }
    }
}
