package dam.pmdm.spyrothedragon;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import dam.pmdm.spyrothedragon.databinding.ActivityMainBinding;
import dam.pmdm.spyrothedragon.databinding.GuideBinding;
import dam.pmdm.spyrothedragon.databinding.GuideStep2Binding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private GuideBinding guideBinding;
    private GuideStep2Binding guideStep2Binding;
    private NavController navController;
    private boolean needToStartGuide;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configuración del almacenamiento de preferencias para saber si la guía ya fue vista
        preferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        needToStartGuide = !preferences.getBoolean("GuideCompleted", false);

        // Inflar el layout principal
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inflar los bindings de las guías desde los includes
        guideBinding = GuideBinding.bind(findViewById(R.id.includelayout));
        guideStep2Binding = GuideStep2Binding.bind(findViewById(R.id.guide2));

        // Configurar NavController
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        if (navHostFragment != null) {
            navController = NavHostFragment.findNavController(navHostFragment);
            NavigationUI.setupWithNavController(binding.navView, navController);
            NavigationUI.setupActionBarWithNavController(this, navController);
        }

        // Manejo de navegación en el menú inferior
        binding.navView.setOnItemSelectedListener(this::selectedBottomMenu);

        // Manejo de visibilidad de la flecha atrás en el ActionBar
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_characters ||
                    destination.getId() == R.id.navigation_worlds ||
                    destination.getId() == R.id.navigation_collectibles) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });

        // Iniciar la guía si es necesario
        initializeGuide();
    }

    private void initializeGuide() {
        if (needToStartGuide) {
            // Mostrar la primera pantalla de la guía
            guideBinding.getRoot().setVisibility(View.VISIBLE);

            // Bloquear eventos táctiles en la guía
            guideBinding.getRoot().setOnTouchListener((v, event) -> true);

            // Botón para avanzar a la segunda pantalla de la guía
            guideBinding.btnStartGuide.setOnClickListener(v -> showGuideStep2());


        }
    }

    private void showGuideStep2() {
        // Ocultar la primera pantalla de la guía
        guideBinding.getRoot().setVisibility(View.GONE);

        // Mostrar la segunda pantalla de la guía
        guideStep2Binding.getRoot().setVisibility(View.VISIBLE);

        // Bloquear eventos táctiles en la segunda pantalla
        guideStep2Binding.getRoot().setOnTouchListener((v, event) -> true);

        // Animación de entrada del bocadillo informativo
        guideStep2Binding.bocadillo.setAlpha(0f);
        guideStep2Binding.bocadillo.animate().alpha(1f).setDuration(500).start();

        // Botón para cerrar la guía en la segunda pantalla
        guideStep2Binding.btnFinishGuide.setOnClickListener(v -> closeGuide());


    }

    private void closeGuide() {
        // Ocultar la segunda pantalla de la guía si está visible
        if (guideStep2Binding.getRoot().getVisibility() == View.VISIBLE) {
            guideStep2Binding.getRoot().setVisibility(View.GONE);
        } else {
            // Ocultar la primera pantalla de la guía si la segunda no está visible
            guideBinding.getRoot().setVisibility(View.GONE);
        }

        /*// Marcar la guía como completada para que no se muestre nuevamente
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("GuideCompleted", true);
        editor.apply();*/
    }




    private boolean selectedBottomMenu(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_characters) {
            navController.navigate(R.id.navigation_characters);
        } else if (menuItem.getItemId() == R.id.nav_worlds) {
            navController.navigate(R.id.navigation_worlds);
        } else {
            navController.navigate(R.id.navigation_collectibles);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_info) {
            showInfoDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showInfoDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_about)
                .setMessage(R.string.text_about)
                .setPositiveButton(R.string.accept, null)
                .show();
    }
}
