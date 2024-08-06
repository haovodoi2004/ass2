package fpoly.pro1121.asm2client.Adapter;

import static fpoly.pro1121.asm2client.Service.ApiService.BASE_URL;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import fpoly.pro1121.asm2client.Model.Category;
import fpoly.pro1121.asm2client.Model.Product;
import fpoly.pro1121.asm2client.R;
import fpoly.pro1121.asm2client.Service.HttpRequest;

public class ProductManagerAdapter extends RecyclerView.Adapter<ProductManagerAdapter.ViewHolder> {
    private ArrayList<Product> list;
    private Context context;
    private HttpRequest httpRequest;
    private ArrayList<Category> listCategory = new ArrayList<>();
    private OnClickUpdateListener onClickUpdateListener;

    public ProductManagerAdapter(ArrayList<Product> list, ArrayList<Category> listCategory, Context context, OnClickUpdateListener onClickUpdateListener) {
        this.list = list;
        this.context = context;
        this.listCategory = listCategory;
        this.onClickUpdateListener = onClickUpdateListener;
        httpRequest = new HttpRequest();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.rec_product_manager, null);
        return new ProductManagerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = list.get(position);
        holder.tv_product_name.setText("Name :" + product.getName());
        for (Category category : listCategory) {
            if (category.get_id().equals(product.getCategory())) {
                holder.tv_category.setText("Category :" + category.getName());
            }
        }
        holder.tv_description.setText("Description :" + product.getDescription());
        holder.tv_price.setText("Price :" + product.getPrice() + " VND");
        holder.tv_stock.setText("Stock :" + product.getStock());
        String imageUrl = BASE_URL + product.getImageUrl();
        Glide.with(context).load(imageUrl).into(holder.image_view);
        holder.iv_update.setOnClickListener(v -> {
            onClickUpdateListener.onClickUpdate(product);
        });
        holder.iv_delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete Product");
            builder.setMessage("Are you sure you want to delete this product?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                deleteProd(product.get_id());
            });
            builder.setNegativeButton("No", (dialog, which) -> {
                dialog.dismiss();
            });
            builder.show();
        });
    }

    private void updateProduct(String id) {

    }

    private void deleteProd(String id) {
        httpRequest.callAPI().deleteProduct(id).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    list.removeIf(product -> product.get_id().equals(id));
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image_view, iv_update, iv_delete;
        TextView tv_product_name, tv_category, tv_description, tv_price, tv_stock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_view = itemView.findViewById(R.id.image_view);
            iv_update = itemView.findViewById(R.id.iv_update);
            iv_delete = itemView.findViewById(R.id.iv_delete);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            tv_category = itemView.findViewById(R.id.tv_category);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_stock = itemView.findViewById(R.id.tv_stock);

        }
    }
}
