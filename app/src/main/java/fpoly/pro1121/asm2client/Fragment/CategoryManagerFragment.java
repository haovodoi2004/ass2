package fpoly.pro1121.asm2client.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.io.IOException;
import java.util.ArrayList;

import fpoly.pro1121.asm2client.Adapter.CategoryAdapter;
import fpoly.pro1121.asm2client.Model.Category;
import fpoly.pro1121.asm2client.Model.Response;
import fpoly.pro1121.asm2client.R;
import fpoly.pro1121.asm2client.Service.HttpRequest;
import fpoly.pro1121.asm2client.databinding.FragmentCategoryManagerBinding;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryManagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryManagerFragment extends Fragment {
    private FragmentCategoryManagerBinding binding;
    private ArrayList<Category> list = new ArrayList<>();
    private HttpRequest httpRequest;
    private CategoryAdapter adapter;

    public CategoryManagerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        httpRequest = new HttpRequest();
        getAllCategory();
        adapter = new CategoryAdapter(list , getContext());
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.fab.setOnClickListener(v -> {
            addCategory();
        });

    }

    private void addCategory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.category_manager_dialog, null);
        builder.setView(view);
        EditText etCategoryNameDialog = view.findViewById(R.id.etCategoryNameDialog);
        EditText etCategoryDescriptionDialog = view.findViewById(R.id.etCategoryDescriptionDialog);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = etCategoryNameDialog.getText().toString();
            String description = etCategoryDescriptionDialog.getText().toString();
            httpRequest.callAPI().AddCategory(new Category(name, description)).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                    if (response.isSuccessful()) {
                        getAllCategory();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable throwable) {

                }
            });
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    private void getAllCategory() {
        httpRequest.callAPI().getAllCategory().enqueue(new Callback<Response<ArrayList<Category>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Category>>> call, retrofit2.Response<Response<ArrayList<Category>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Response<ArrayList<Category>> responseBody = response.body();
                    if (responseBody.getData() != null) {
                        list.clear();
                        list.addAll(responseBody.getData());
                        adapter.notifyDataSetChanged();
                        for (Category category : list) {
                            Log.d("TAG:CateMANAGER", category.toString());
                        }
                    } else {
                        Log.d("TAG:CateMANAGER", "Data is null");
                    }
                } else {
                    try {
                        Log.d("TAG:CateMANAGER", "Error response: " + response.errorBody().string());
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


    public static CategoryManagerFragment newInstance(String param1, String param2) {
        CategoryManagerFragment fragment = new CategoryManagerFragment();

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
        binding = FragmentCategoryManagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}