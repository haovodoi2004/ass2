package fpoly.pro1121.asm2client;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import fpoly.pro1121.asm2client.Model.User;
import fpoly.pro1121.asm2client.Service.HttpRequest;
import fpoly.pro1121.asm2client.databinding.ActivitySignupBinding;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private HttpRequest httpRequest;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        httpRequest = new HttpRequest();
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Show err text
        binding.edRegisterUsername.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.edRegisterUsername.setError(null);
                binding.edRegisterUsername.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.edRegisterEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.edRegisterEmail.setError(null);
                binding.edRegisterEmail.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.edRegisterPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.edRegisterPassword.setError(null);
                binding.edRegisterPassword.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.edRegisterAddress.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.edRegisterAddress.setError(null);
                binding.edRegisterAddress.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.btnRegister.setOnClickListener(v -> {
            if (valid()) {
                String username = binding.edRegisterUsername.getEditText().getText().toString();
                String password = binding.edRegisterPassword.getEditText().getText().toString();
                String email = binding.edRegisterEmail.getEditText().getText().toString();
                String address = binding.edRegisterAddress.getEditText().getText().toString();

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
                httpRequest.callAPI().register(_username, _email, _password, _avatar, _address).enqueue(registerCallBack);
            }
        });
        binding.imgRegisterAvatar.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            pickImage.launch(intent);
        });
        binding.tvBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        });

    }

    private boolean valid() {
        if (binding.edRegisterUsername.getEditText().getText().toString().isEmpty()) {
            binding.edRegisterUsername.setError("Username is required");
            return false;
        }
        if (binding.edRegisterPassword.getEditText().getText().toString().isEmpty()) {
            binding.edRegisterPassword.setError("Password is required");
            return false;
        }
        if (binding.edRegisterEmail.getEditText().getText().toString().isEmpty()) {
            binding.edRegisterEmail.setError("Email is required");
            return false;
        }
        if (binding.edRegisterAddress.getEditText().getText().toString().isEmpty()) {
            binding.edRegisterAddress.setError("Address is required");
            return false;
        }
        if (file == null) {
            Toast.makeText(this, "Select you icon", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    file = createFileFromUri(result.getData().getData());
                    Glide.with(this)
                            .load(file).centerCrop()
                            .circleCrop()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(binding.imgRegisterAvatar);
                }
            });

    private File createFileFromUri(Uri uri) {
        File _file = new File(getFilesDir(), "avatar" + ".png");
        try {
            InputStream in = getContentResolver().openInputStream(uri);
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

    Callback<User> registerCallBack = new Callback<User>() {
        @Override
        public void onResponse(Call<User> call, Response<User> response) {
            if (response.isSuccessful()) {
                Toast.makeText(SignupActivity.this, "Register success", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            } else {
                Toast.makeText(SignupActivity.this, "Register failed", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<User> call, Throwable throwable) {

        }
    };
}