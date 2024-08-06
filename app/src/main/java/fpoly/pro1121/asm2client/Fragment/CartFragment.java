package fpoly.pro1121.asm2client.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import fpoly.pro1121.asm2client.Adapter.OrderAdapter;
import fpoly.pro1121.asm2client.Model.OrderResponse;
import fpoly.pro1121.asm2client.Model.Product;
import fpoly.pro1121.asm2client.Service.HttpRequest;
import fpoly.pro1121.asm2client.databinding.FragmentCartBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment {
    private FragmentCartBinding binding;
    private String userID;
    private HttpRequest httpRequest;
    private ArrayList<Product> products = new ArrayList<>();
    private OrderAdapter adapter;
    ;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("id", "");
        httpRequest = new HttpRequest();
        getOrder(userID);
        adapter = new OrderAdapter(products, getContext());
        binding.rvCart.setAdapter(adapter);
        binding.rvCart.setLayoutManager(new LinearLayoutManager(getContext()));


        Log.d("TAG", "onViewCreated: " + products.size());
        Log.d("TAG", "onViewCreated: " + userID);

    }

    private void getOrder(String userID) {
        httpRequest.callAPI().getOrders(userID).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> orders = response.body().getData();
                    for (String order : orders) {
                        Log.d("TAG", "onResponse: " + order);
                        addProduct(order);
                    }
                    adapter.notifyDataSetChanged();
                }else {
                    Log.d("TAG", "onResponse: " + response.message());
                }
            }
            @Override
            public void onFailure(Call<OrderResponse> call, Throwable throwable) {
                Log.d("TAG", "onResponse: " + throwable.getMessage());
            }
        });
    }

    private void addProduct(String id) {
        httpRequest.callAPI().getProductByID(id).enqueue(new Callback<fpoly.pro1121.asm2client.Model.Response<Product>>() {
            @Override
            public void onResponse(Call<fpoly.pro1121.asm2client.Model.Response<Product>> call, Response<fpoly.pro1121.asm2client.Model.Response<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("TAG", "onResponse: " + response.body().getData());
                    products.add(response.body().getData());
                    Log.d("TAG", "onResponse: " + products.size());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<fpoly.pro1121.asm2client.Model.Response<Product>> call, Throwable throwable) {

            }
        });
    }


    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
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
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}