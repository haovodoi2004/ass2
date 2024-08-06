package fpoly.pro1121.asm2client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import fpoly.pro1121.asm2client.Model.Response;
import fpoly.pro1121.asm2client.Model.User;
import fpoly.pro1121.asm2client.Service.HttpRequest;
import fpoly.pro1121.asm2client.databinding.ActivityLoginBinding;
import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private HttpRequest httpRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        httpRequest = new HttpRequest();
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.btnLogin.setOnClickListener(v -> {
            User user = new User();
            user.setEmail(binding.edLoginEmail.getEditText().getText().toString());
            user.setPassword(binding.edLoginPassword.getEditText().getText().toString());
            httpRequest.callAPI().login(user).enqueue(loginCallBack);
        });
        binding.tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });
    }

    Callback<Response<User>> loginCallBack = new Callback<Response<User>>() {

        @Override
        public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
            if (response.isSuccessful()) {
                String ID = response.body().getData().get_id();
                SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("id", ID);
                Log.d("TAG:LoginActivity", "onResponse: =" + response.body().getData().get_id());
                editor.apply();

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            } else {
                Toast.makeText(LoginActivity.this, "Login failed " + response, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<Response<User>> call, Throwable throwable) {

        }
    };
}