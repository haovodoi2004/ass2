package fpoly.pro1121.asm2client.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;
import java.util.ArrayList;

import fpoly.pro1121.asm2client.Adapter.OnClickProductListener;
import fpoly.pro1121.asm2client.Adapter.ProductAdapter;
import fpoly.pro1121.asm2client.Model.Category;
import fpoly.pro1121.asm2client.Model.Product;
import fpoly.pro1121.asm2client.Model.Response;
import fpoly.pro1121.asm2client.R;
import fpoly.pro1121.asm2client.Service.HttpRequest;
import fpoly.pro1121.asm2client.Spinner.CategorySpinner;
import fpoly.pro1121.asm2client.databinding.FragmentHomeBinding;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private ProductAdapter adapter;
    private ArrayList<Product> list = new ArrayList<>();
    private ArrayList<Category> listCategory = new ArrayList<>();
    private CategorySpinner spinnerAdapter;
    private HttpRequest httpRequest;
    private Context context;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = view.getContext();

        // Initialize the adapter with an empty list
        adapter = new ProductAdapter(list, context);

        // Ensure binding is not null before setting the adapter
        if (binding != null) {
            binding.rcvProduct.setAdapter(adapter);
        }
        // Call the API after setting the adapter
        callApi();
        binding.edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = binding.edSearch.getText().toString().trim();
                httpRequest.callAPI()
                        .getProductByName(query)
                        .enqueue(new Callback<Response<ArrayList<Product>>>() {
                            @Override
                            public void onResponse(Call<Response<ArrayList<Product>>> call, retrofit2.Response<Response<ArrayList<Product>>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Response<ArrayList<Product>> responseBody = response.body();
                                    if (responseBody.getData() != null) {
                                        list.clear();
                                        list.addAll(responseBody.getData());
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        Log.d("TAG:HomeFragment", "Data is null");
                                    }
                                } else {
                                    try {
                                        Log.d("TAG:HomeFragment", "Error response: " + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Response<ArrayList<Product>>> call, Throwable throwable) {
                                Log.d("DDDDD", "onFailure: " + throwable.getMessage());
                            }
                        });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        loadSpinner();
        spinnerAdapter = new CategorySpinner(context, listCategory);
        binding.spnCategory.setAdapter(spinnerAdapter);
        binding.spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category selected = (Category) parent.getItemAtPosition(position);
                if (selected.get_id().equals("0")) {
                    callApi();
                } else {
                    httpRequest.callAPI().getProductByCategory(selected.get_id()).enqueue(new Callback<Response<ArrayList<Product>>>() {
                        @Override
                        public void onResponse(Call<Response<ArrayList<Product>>> call, retrofit2.Response<Response<ArrayList<Product>>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Response<ArrayList<Product>> responseBody = response.body();
                                if (responseBody.getData() != null) {
                                    list.clear();
                                    list.addAll(responseBody.getData());
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Log.d("TAG:HomeFragment", "Data is null");
                                }
                            } else {
                                try {
                                    Log.d("TAG:HomeFragment", "Error response: " + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Response<ArrayList<Product>>> call, Throwable throwable) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        adapter.setOnClickProductListener(new OnClickProductListener() {
            @Override
            public void onClickProduct(int pos, String id) {
                ProductDetailFragment fragment = ProductDetailFragment.newInstance(id);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.flFragment, fragment);
                transaction.addToBackStack(null); // Thêm vào backstack để người dùng có thể quay lại
                transaction.commit();
            }
        });

    }

    private void loadSpinner() {
        httpRequest.callAPI().getAllCategory().enqueue(new Callback<Response<ArrayList<Category>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Category>>> call, retrofit2.Response<Response<ArrayList<Category>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Response<ArrayList<Category>> responseBody = response.body();
                    if (responseBody.getData() != null) {
                        listCategory.clear();
                        listCategory.add(new Category("0", "All", "all"));
                        listCategory.addAll(responseBody.getData());
                        spinnerAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("TAG:HomeFragment", "Data is null");
                    }
                } else {
                    try {
                        Log.d("TAG:HomeFragment", "Error response: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<Category>>> call, Throwable throwable) {

            }
        });
    }

    private void callApi() {
        httpRequest = new HttpRequest();
        httpRequest.callAPI().getAllProduct().enqueue(new Callback<Response<ArrayList<Product>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Product>>> call, retrofit2.Response<Response<ArrayList<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Response<ArrayList<Product>> responseBody = response.body();
                    if (responseBody.getData() != null) {
                        list.clear();
                        list.addAll(responseBody.getData());
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d("TAG:HomeFragment", "Data is null");
                    }
                } else {
                    try {
                        Log.d("TAG:HomeFragment", "Error response: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<Product>>> call, Throwable throwable) {
                Log.e("TAG:HomeFragment", "API call failed", throwable);
            }
        });
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
