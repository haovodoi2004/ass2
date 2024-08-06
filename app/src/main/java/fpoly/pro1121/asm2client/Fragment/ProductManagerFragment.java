package fpoly.pro1121.asm2client.Fragment;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import fpoly.pro1121.asm2client.Adapter.OnClickUpdateListener;
import fpoly.pro1121.asm2client.Adapter.ProductManagerAdapter;
import fpoly.pro1121.asm2client.Model.Category;
import fpoly.pro1121.asm2client.Model.Product;
import fpoly.pro1121.asm2client.Model.Response;
import fpoly.pro1121.asm2client.R;
import fpoly.pro1121.asm2client.Service.HttpRequest;
import fpoly.pro1121.asm2client.Spinner.CategorySpinner;
import fpoly.pro1121.asm2client.databinding.FragmentProductManagerBinding;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductManagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductManagerFragment extends Fragment implements OnClickUpdateListener {

    private FragmentProductManagerBinding binding;
    private HttpRequest httpRequest;
    private ArrayList<Product> list = new ArrayList<>();
    private ProductManagerAdapter adapter;
    private ArrayList<Category> listCategory = new ArrayList<>();
    private File file;
    private File updateFile;
    private ImageView imgProduct;
    private CategorySpinner categorySpinner;
    private Context context;
    public ProductManagerFragment() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        httpRequest = new HttpRequest();
        callApi();
        context= getContext();
        categorySpinner = new CategorySpinner(getContext(), listCategory);
        callApi();
        getAllCategory();
        adapter = new ProductManagerAdapter(list, listCategory, getContext(), this);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.fab.setOnClickListener(v -> {
            showDialog();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        callApi();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Product");
        View view = LayoutInflater.from(getContext()).inflate(R.layout.product_manager_dialog, null);

        EditText edtProductName, edtProductPrice, edtProductDescription, edtProductStock;
        Spinner spnCategory;
        imgProduct = view.findViewById(R.id.img_product_dia);
        edtProductName = view.findViewById(R.id.ed_product_name_dia);
        edtProductPrice = view.findViewById(R.id.ed_price_ida);
        edtProductDescription = view.findViewById(R.id.ed_description_ida);
        edtProductStock = view.findViewById(R.id.ed_stock_dia);
        spnCategory = view.findViewById(R.id.sp_category_ida);
        spnCategory.setAdapter(categorySpinner);
        imgProduct.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            pickImage.launch(intent);
        });

        builder.setView(view);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String Name = edtProductName.getText().toString();
            String Price = edtProductPrice.getText().toString();
            String Description = edtProductDescription.getText().toString();
            String Stock = edtProductStock.getText().toString();
            String Category = listCategory.get(spnCategory.getSelectedItemPosition()).get_id();
            //addProduct();
            if (file == null) {
                Toast.makeText(getContext(), "Please choose image", Toast.LENGTH_SHORT).show();
            }
//            RequestBody _username = RequestBody.create(MediaType.parse("multipart/form-data"), username);
//            RequestBody _password = RequestBody.create(MediaType.parse("multipart/form-data"), password);
//            RequestBody _email = RequestBody.create(MediaType.parse("multipart/form-data"), email);
//            RequestBody _address = RequestBody.create(MediaType.parse("multipart/form-data"), address);
            RequestBody _name = RequestBody.create(MediaType.parse("multipart/form-data"), Name);
            RequestBody _price = RequestBody.create(MediaType.parse("multipart/form-data"), Price);
            RequestBody _description = RequestBody.create(MediaType.parse("multipart/form-data"), Description);
            RequestBody _stock = RequestBody.create(MediaType.parse("multipart/form-data"), Stock);
            RequestBody _category = RequestBody.create(MediaType.parse("multipart/form-data"), Category);
            MultipartBody.Part _image;
            if (file != null) {
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                _image = MultipartBody.Part.createFormData("imageUrl", file.getName(), requestFile);
            } else {
                _image = null;
            }
            httpRequest.callAPI().AddProd(
                    _name,
                    _category,
                    _description,
                    _price,
                    _image,
                    _stock
            ).enqueue(new Callback<Product>() {
                @Override
                public void onResponse(Call<Product> call, retrofit2.Response<Product> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Add product success", Toast.LENGTH_SHORT).show();
                        callApi();
                    } else {
                        Toast.makeText(getContext(), "Add product failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Product> call, Throwable throwable) {

                }
            });

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());

        // setting width to 90% of display
        layoutParams.width = (int) (displayMetrics.widthPixels * 0.9f);

        // setting height to 90% of display
        layoutParams.height = (int) (displayMetrics.heightPixels * 0.9f);
        alertDialog.getWindow().setAttributes(layoutParams);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                    file = createFileFromUri(result.getData().getData());
                    Glide.with(getContext())
                            .load(file).centerCrop()
                            .circleCrop()
                            .into(imgProduct);
                }
            });
    private final ActivityResultLauncher<Intent> pickImageUpdate = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    updateFile = createFileFromUri(result.getData().getData());
                    Glide.with(getContext())
                            .load(updateFile).centerCrop()
                            .circleCrop()
                            .into(imgProduct);
                }
            });

    private File createFileFromUri(Uri uri) {
        File _file = new File(getActivity().getFilesDir(), "avatar" + ".png");
        try {
            InputStream in = getActivity().getContentResolver().openInputStream(uri);
            FileOutputStream out = new FileOutputStream(_file);
            byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
            out.close();
            in.close();
            return _file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void getAllCategory() {
        httpRequest.callAPI().getAllCategory().enqueue(new Callback<Response<ArrayList<Category>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Category>>> call, retrofit2.Response<Response<ArrayList<Category>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Response<ArrayList<Category>> responseBody = response.body();
                    if (responseBody.getData() != null) {
                        listCategory.clear();
                        listCategory.addAll(responseBody.getData());
                        for (Category category : listCategory) {
                            Log.d("TAG:HomeFragment", category.toString());
                        }
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
        httpRequest.callAPI().getAllProduct().enqueue(new Callback<Response<ArrayList<Product>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Product>>> call, retrofit2.Response<Response<ArrayList<Product>>> response) {
                Response<ArrayList<Product>> responseBody = response.body();
                if (responseBody.getData() != null) {
                    list.clear();
                    list.addAll(responseBody.getData());
                    adapter.notifyDataSetChanged();
                    

                } else {
                    Log.d("TAG:HomeFragment", "Data is null");
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<Product>>> call, Throwable throwable) {

            }
        });
    }


    public static ProductManagerFragment newInstance(String param1, String param2) {
        ProductManagerFragment fragment = new ProductManagerFragment();
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
        binding = FragmentProductManagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onClickUpdate(Product product) {
        String id = product.get_id();
        // Assuming you want to update the product details via a dialog similar to the "Add Product" dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Product");
        View view = LayoutInflater.from(context).inflate(R.layout.product_manager_dialog, null);

        EditText edtProductName, edtProductPrice, edtProductDescription, edtProductStock;
        Spinner spnCategory;
        imgProduct = view.findViewById(R.id.img_product_dia);
        edtProductName = view.findViewById(R.id.ed_product_name_dia);
        edtProductPrice = view.findViewById(R.id.ed_price_ida);
        edtProductDescription = view.findViewById(R.id.ed_description_ida);
        edtProductStock = view.findViewById(R.id.ed_stock_dia);
        spnCategory = view.findViewById(R.id.sp_category_ida);

        // Set existing product data in the dialog fields
        edtProductName.setText(product.getName());
        edtProductPrice.setText(String.valueOf(product.getPrice()));
        edtProductDescription.setText(product.getDescription());
        edtProductStock.setText(String.valueOf(product.getStock()));

        // Set category spinner
        categorySpinner = new CategorySpinner(view.getContext(), listCategory);
        spnCategory.setAdapter(categorySpinner);
        // Assuming you set the position of the spinner to the current product's category

        imgProduct.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            pickImageUpdate.launch(intent);
        });

        builder.setView(view);
        builder.setPositiveButton("Update", (dialog, which) -> {
            String name = edtProductName.getText().toString();
            String price = edtProductPrice.getText().toString();
            String description = edtProductDescription.getText().toString();
            String stock = edtProductStock.getText().toString();
            String category = listCategory.get(spnCategory.getSelectedItemPosition()).get_id();

            RequestBody requestName = RequestBody.create(MediaType.parse("multipart/form-data"), name);
            RequestBody requestPrice = RequestBody.create(MediaType.parse("multipart/form-data"), price);
            RequestBody requestDescription = RequestBody.create(MediaType.parse("multipart/form-data"), description);
            RequestBody requestStock = RequestBody.create(MediaType.parse("multipart/form-data"), stock);
            RequestBody requestCategory = RequestBody.create(MediaType.parse("multipart/form-data"), category);

            MultipartBody.Part imagePart = null;
            if (updateFile != null) {
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), updateFile);
                imagePart = MultipartBody.Part.createFormData("imageUrl", updateFile.getName(), requestFile);
            }

            httpRequest.callAPI().updateProduct(
                    id,
                    requestName,
                    requestCategory,
                    requestDescription,
                    requestPrice,
                    imagePart,
                    requestStock
            ).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Product updated successfully", Toast.LENGTH_SHORT).show();
                        callApi();  // Refresh the product list
                    } else {
                        Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                }
            });
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
        layoutParams.width = (int) (displayMetrics.widthPixels * 0.9f);
        layoutParams.height = (int) (displayMetrics.heightPixels * 0.9f);
        alertDialog.getWindow().setAttributes(layoutParams);
    }
}