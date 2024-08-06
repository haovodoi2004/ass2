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

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private ArrayList<Product> list;
    private Context context;
    private OnClickProductListener onClickProductListener;

    public ProductAdapter(ArrayList<Product> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.rec_product, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = list.get(position);
        holder.tv_product_name.setText("Name :" + product.getDescription());
        holder.tv_product_price.setText("Price :" + product.getPrice() + " VND");
        String imageUrl = BASE_URL + product.getImageUrl();
        Glide.with(context).load(imageUrl).into(holder.iv_product);
        holder.itemView.setOnClickListener(v -> {
            if (onClickProductListener != null) {
                onClickProductListener.onClickProduct(position, product.get_id());
            }
        });
        //Glide.with(context).load(product.getImageUrl()).into(holder.iv_product);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void setOnClickProductListener(OnClickProductListener onClickProductListener) {
        this.onClickProductListener = onClickProductListener;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_product;
        TextView tv_product_name, tv_product_price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_product = itemView.findViewById(R.id.iv_product);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            tv_product_price = itemView.findViewById(R.id.tv_product_price);
            itemView.setOnClickListener(v -> {
                if (onClickProductListener != null) {
                    onClickProductListener.onClickProduct(getAdapterPosition(), list.get(getAdapterPosition()).get_id());
                }
            });
        }
    }
}
