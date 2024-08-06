package fpoly.pro1121.asm2client;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import fpoly.pro1121.asm2client.Fragment.CategoryManagerFragment;
import fpoly.pro1121.asm2client.Fragment.ProductManagerFragment;
import fpoly.pro1121.asm2client.databinding.ActivityAdminBinding;

public class AdminActivity extends AppCompatActivity {
    private ActivityAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadFragment(new ProductManagerFragment());
        binding.bottomNavigationView.setOnItemReselectedListener(item -> {
            if (item.getItemId() == R.id.nav_prod) {
                loadFragment(new ProductManagerFragment());
            }
            if (item.getItemId() == R.id.nav_category) {
                loadFragment(new CategoryManagerFragment());
            }
        });
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(binding.flFragment.getId(), fragment)
                .commit();
    }

    ;
}