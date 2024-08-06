package fpoly.pro1121.asm2client.Fragment;

import static fpoly.pro1121.asm2client.Service.ApiService.BASE_URL;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import fpoly.pro1121.asm2client.AdminActivity;
import fpoly.pro1121.asm2client.Model.Response;
import fpoly.pro1121.asm2client.Model.User;
import fpoly.pro1121.asm2client.Service.HttpRequest;
import fpoly.pro1121.asm2client.databinding.FragmentUserBinding;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {
    private static final String TAG = "UserFragment";
    private boolean isEdit = false;
    private FragmentUserBinding binding;
    private HttpRequest httpRequest;
    private File file;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("id", "");

        httpRequest = new HttpRequest();
        getUserData(userID);

        binding.edUserPassword.getEditText().setEnabled(false);
        binding.edUserUsername.getEditText().setEnabled(false);
        binding.edUserEmail.getEditText().setEnabled(false);
        binding.edUserAddress.getEditText().setEnabled(false);
        binding.btnUpdate.setOnClickListener(v -> {
            if (!isEdit) {
                binding.edUserPassword.getEditText().setEnabled(true);
                binding.edUserUsername.getEditText().setEnabled(true);
                binding.edUserEmail.getEditText().setEnabled(true);
                binding.edUserAddress.getEditText().setEnabled(true);
                binding.btnUpdate.setText("Save");
                isEdit = true;
            } else {
                updateData(userID);
                binding.edUserPassword.getEditText().setEnabled(false);
                binding.edUserUsername.getEditText().setEnabled(false);
                binding.edUserEmail.getEditText().setEnabled(false);
                binding.edUserAddress.getEditText().setEnabled(false);
                binding.btnUpdate.setText("Update Info");
                isEdit = false;
            }
        });
        binding.imgUser.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            pickImage.launch(intent);
        });
        binding.imgToAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AdminActivity.class);
            startActivity(intent);
        });
    }

    private void updateData(String userID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Update user info");
        builder.setMessage("Are you sure to update your info?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            callUpdateAPI(userID);
        });
        builder.setNegativeButton("No", (dialog, which) -> {

        });
        builder.show();
    }

    private void callUpdateAPI(String userID) {
        String username = binding.edUserUsername.getEditText().getText().toString();
        String password = binding.edUserPassword.getEditText().getText().toString();
        String email = binding.edUserEmail.getEditText().getText().toString();
        String address = binding.edUserAddress.getEditText().getText().toString();

        RequestBody _username = RequestBody.create(MediaType.parse("multipart/form-data"), username);
        RequestBody _password = RequestBody.create(MediaType.parse("multipart/form-data"), password);
        RequestBody _email = RequestBody.create(MediaType.parse("multipart/form-data"), email);
        RequestBody _address = RequestBody.create(MediaType.parse("multipart/form-data"), address);
        MultipartBody.Part _avatar;
        if (file != null) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            _avatar = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);
        } else {
            _avatar = null;
        }
        Log.d("NOWAY", "onCreate: " + username + password + email + _avatar + address);
        httpRequest.callAPI().updateUser(userID, _username, _email, _password, _avatar, _address).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, retrofit2.Response<User> response) {
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: " + response.body().toString());
                    Toast.makeText(getContext(), "Update success", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                Log.d(TAG, "onFailure: " + throwable.getMessage());
            }
        });
    }


    private void getUserData(String userID) {
        httpRequest.callAPI().getUserById(userID).enqueue(new Callback<Response<User>>() {

            @Override
            public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
                if (response.body().getStatus() == 200) {
                    User user = response.body().getData();
                    binding.edUserUsername.getEditText().setText(user.getName());
                    binding.edUserEmail.getEditText().setText(user.getEmail());
                    binding.edUserPassword.getEditText().setText(user.getPassword());
                    binding.edUserAddress.getEditText().setText(user.getAddress());
                    Glide.with(getContext()).load(BASE_URL + user.getAvatar()).into(binding.imgUser);
                }
            }

            @Override
            public void onFailure(Call<Response<User>> call, Throwable throwable) {

            }
        });
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    file = createFileFromUri(result.getData().getData());
                    Glide.with(getContext())
                            .load(file)
                            .centerCrop()
                            .into(binding.imgUser);
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

    public UserFragment() {
        // Required empty public constructor
    }

    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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
        binding = FragmentUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}