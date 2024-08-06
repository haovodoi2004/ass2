package fpoly.pro1121.asm2client.Fragment;

import static fpoly.pro1121.asm2client.Service.ApiService.BASE_URL;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fpoly.pro1121.asm2client.Model.Order;
import fpoly.pro1121.asm2client.Model.Product;
import fpoly.pro1121.asm2client.Model.Response;
import fpoly.pro1121.asm2client.Service.HttpRequest;
import fpoly.pro1121.asm2client.databinding.FragmentProductDetailBinding;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductDetailFragment extends Fragment {
    private HttpRequest httpRequest;
    private FragmentProductDetailBinding binding;
    private String productID;
    private Product product;
    private String userID;

    public ProductDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        productID = getArguments().getString("id");
        httpRequest = new HttpRequest();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("id", "");
        httpRequest.callAPI().getProductByID(productID).enqueue(new Callback<Response<Product>>() {
            @Override
            public void onResponse(Call<Response<Product>> call, retrofit2.Response<Response<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    product = response.body().getData();
                    binding.tvProductName.setText("Name :" + product.getName());
                    binding.tvProductPrice.setText("Price :" + product.getPrice() + " VND");
                    binding.tvProductDescription.setText("Description :" + product.getDescription());
                    String imageUrl = BASE_URL + product.getImageUrl();
                    Glide.with(getContext()).load(imageUrl).into(binding.imgProduct);
                }
            }

            @Override
            public void onFailure(Call<Response<Product>> call, Throwable throwable) {

            }
        });
        binding.btnAddToCart.setOnClickListener(v -> {
            Order order = new Order();
            order.setUserID(userID);
            order.setTotalAmount(10);
            Order.CartItem item = new Order.CartItem();
            item.setProductID(productID);
            item.setQuantity(1);
            List<Order.CartItem> listItem = Collections.singletonList(item);
            order.setItems(new ArrayList<>(listItem));
            placeOrder(order);
        });
    }

    private void placeOrder(Order order) {
        httpRequest.callAPI().createOrder(order).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, retrofit2.Response<Order> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Add to cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable throwable) {
                Toast.makeText(getContext(), "Add to cart err", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static ProductDetailFragment newInstance(String id) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putString("id", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProductDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}