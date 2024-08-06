package fpoly.pro1121.asm2client;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import fpoly.pro1121.asm2client.Fragment.CartFragment;
import fpoly.pro1121.asm2client.Fragment.HomeFragment;
import fpoly.pro1121.asm2client.Fragment.UserFragment;
import fpoly.pro1121.asm2client.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadFragment(new HomeFragment());
        binding.bottomNavigationView.setOnItemReselectedListener(item -> {
            if (item.getItemId() == R.id.nav_product) {
                loadFragment(new HomeFragment());
            }
            if (item.getItemId() == R.id.nav_cart) {
                loadFragment(new CartFragment());
            }
            if (item.getItemId() == R.id.nav_user) {
                loadFragment(new UserFragment());
            }
        });
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(binding.flFragment.getId(), fragment)
                .commit();
    };
}