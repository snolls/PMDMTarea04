package dam.pmdm.spyrothedragon;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

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
import dam.pmdm.spyrothedragon.databinding.GuideStep3Binding;
import dam.pmdm.spyrothedragon.databinding.GuideStep4Binding;
import dam.pmdm.spyrothedragon.databinding.GuideStep5Binding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private GuideBinding guideBinding;
    private GuideStep2Binding guideStep2Binding;
    private GuideStep3Binding guideStep3Binding;
    private GuideStep4Binding guideStep4Binding;
    private GuideStep5Binding guideStep5Binding;
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
        guideBinding = GuideBinding.bind(findViewById(R.id.guide));
        guideStep2Binding = GuideStep2Binding.bind(findViewById(R.id.guide2));
        guideStep3Binding = GuideStep3Binding.bind(findViewById(R.id.guide3));
        guideStep4Binding = GuideStep4Binding.bind(findViewById(R.id.guide4));
        guideStep5Binding = GuideStep5Binding.bind(findViewById(R.id.guide5));

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

        // Bloquear eventos táctiles en la guía
        guideStep2Binding.getRoot().setOnTouchListener((v, event) -> true);

        // Resaltar la pestaña "Personajes"
        binding.navView.getMenu().findItem(R.id.nav_characters).setChecked(true);

        // Esperar hasta que `BottomNavigationView` esté listo
        binding.navView.post(() -> {
            // Obtener la vista de la opción "Personajes" dentro del BottomNavigationView
            View personajeTab = binding.navView.getChildAt(0).findViewById(R.id.nav_characters);

            if (personajeTab != null) { // Evitar NullPointerException
                float iconX = personajeTab.getX(); // Posición X
                float iconY = binding.navView.getY(); // Posición Y del BottomNavigationView

                // Obtener tamaño del icono en el menú
                int iconWidth = personajeTab.getWidth();
                int iconHeight = personajeTab.getHeight();

                // Calcular el centro del icono en X y Y
                float centerX = iconX + (iconWidth / 2f);
                float centerY = iconY + (iconHeight / 2f);

                // Posicionar el anillo resaltador dinámicamente
                guideStep2Binding.highlightRing.post(() -> {
                    guideStep2Binding.highlightRing.setX(centerX - (guideStep2Binding.highlightRing.getWidth() / 2f));
                    guideStep2Binding.highlightRing.setY(centerY - (guideStep2Binding.highlightRing.getHeight() / 2f));

                    // Animación del anillo resaltador
                    guideStep2Binding.highlightRing.setAlpha(0f);
                    guideStep2Binding.highlightRing.animate()
                            .alpha(1f)
                            .scaleX(1.2f)
                            .scaleY(1.2f)
                            .setDuration(600)
                            .setInterpolator(new OvershootInterpolator()) // Rebote al final
                            .start();
                });
            } else {
                Log.e("Guide", "No se encontró la opción 'Personajes' en BottomNavigationView.");
            }
        });

        // Animación del texto sobre el anillo
        guideStep2Binding.tvGuideCharacters.setAlpha(0f);
        guideStep2Binding.tvGuideCharacters.setTranslationY(50f);
        guideStep2Binding.tvGuideCharacters.setVisibility(View.VISIBLE);
        guideStep2Binding.tvGuideCharacters.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(700)
                .setStartDelay(300)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // Animación de la flecha para avanzar al siguiente paso
        guideStep2Binding.btnNextStep.setAlpha(0f);
        guideStep2Binding.btnNextStep.setTranslationX(50f);
        guideStep2Binding.btnNextStep.setVisibility(View.VISIBLE);
        guideStep2Binding.btnNextStep.animate()
                .alpha(1f)
                .translationX(0f)
                .setDuration(700)
                .setStartDelay(600)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Configurar la flecha para avanzar al paso 3
        guideStep2Binding.btnNextStep.setOnClickListener(v -> showGuideStep3());

        // Configurar el botón de omitir la guía
        guideStep2Binding.btnSkipGuide2.setOnClickListener(v -> closeGuide());
    }



    private void showGuideStep3() {
        // Ocultar la segunda pantalla de la guía
        guideStep2Binding.getRoot().setVisibility(View.GONE);

        // Mostrar la tercera pantalla de la guía
        guideStep3Binding.getRoot().setVisibility(View.VISIBLE);

        // Bloquear eventos táctiles en la guía
        guideStep3Binding.getRoot().setOnTouchListener((v, event) -> true);

        // Cambiar automáticamente a la pestaña "Mundos"
        binding.navView.setSelectedItemId(R.id.nav_worlds);

        // Pequeño retraso para asegurar que la pestaña ha cambiado completamente
        binding.navView.postDelayed(() -> {
            // Obtener la vista de la opción "Mundos" dentro del BottomNavigationView
            View mundosTab = binding.navView.findViewById(R.id.nav_worlds);

            if (mundosTab != null) { // Evitar NullPointerException
                float iconX = mundosTab.getX(); // Posición X
                float iconY = binding.navView.getY(); // Posición Y del BottomNavigationView

                // Obtener tamaño del icono en el menú
                int iconWidth = mundosTab.getWidth();
                int iconHeight = mundosTab.getHeight();

                // Calcular el centro del icono en X y Y
                float centerX = iconX + (iconWidth / 2f);
                float centerY = iconY + (iconHeight / 2f);

                // Posicionar el anillo resaltador dinámicamente
                guideStep3Binding.highlightRingWorlds.post(() -> {
                    guideStep3Binding.highlightRingWorlds.setAlpha(0f);
                    guideStep3Binding.highlightRingWorlds.setScaleX(0.8f);
                    guideStep3Binding.highlightRingWorlds.setScaleY(0.8f);

                    guideStep3Binding.highlightRingWorlds.setX(centerX - (guideStep3Binding.highlightRingWorlds.getWidth() / 2f));
                    guideStep3Binding.highlightRingWorlds.setY(centerY - (guideStep3Binding.highlightRingWorlds.getHeight() / 2f));

                    // Animación del anillo resaltador con efecto más visible
                    guideStep3Binding.highlightRingWorlds.animate()
                            .alpha(1f)
                            .scaleX(1.5f)
                            .scaleY(1.5f)
                            .setDuration(700)
                            .setInterpolator(new OvershootInterpolator()) // Rebote al final
                            .start();
                });
            } else {
                Log.e("Guide", "No se encontró la opción 'Mundos' en BottomNavigationView.");
            }
        }, 300); // Agregar un retraso de 300ms para sincronizar con el cambio de pestaña

        // Animación del texto sobre el anillo
        guideStep3Binding.tvGuideWorlds.setAlpha(0f);
        guideStep3Binding.tvGuideWorlds.setTranslationY(50f);
        guideStep3Binding.tvGuideWorlds.setVisibility(View.VISIBLE);
        guideStep3Binding.tvGuideWorlds.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(700)
                .setStartDelay(300)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // Animación de la flecha para avanzar al siguiente paso
        guideStep3Binding.btnNextStep3.setAlpha(0f);
        guideStep3Binding.btnNextStep3.setTranslationX(50f);
        guideStep3Binding.btnNextStep3.setVisibility(View.VISIBLE);
        guideStep3Binding.btnNextStep3.animate()
                .alpha(1f)
                .translationX(0f)
                .setDuration(700)
                .setStartDelay(600)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Configurar la flecha para avanzar al paso 4
        guideStep3Binding.btnNextStep3.setOnClickListener(v -> showGuideStep4());

        // Configurar el botón de omitir la guía
        guideStep3Binding.btnSkipGuide3.setOnClickListener(v -> closeGuide());
    }


    private void showGuideStep4() {
        // Ocultar la tercera pantalla de la guía
        guideStep3Binding.getRoot().setVisibility(View.GONE);

        // Mostrar la cuarta pantalla de la guía
        guideStep4Binding.getRoot().setVisibility(View.VISIBLE);

        // Bloquear eventos táctiles en la guía
        guideStep4Binding.getRoot().setOnTouchListener((v, event) -> true);

        // Cambiar automáticamente a la pestaña "Coleccionables"
        binding.navView.setSelectedItemId(R.id.nav_collectibles);

        // Esperar hasta que `BottomNavigationView` esté listo
        binding.navView.postDelayed(() -> {
            // Obtener la vista de la opción "Coleccionables" dentro del BottomNavigationView
            View collectiblesTab = binding.navView.findViewById(R.id.nav_collectibles);

            if (collectiblesTab != null) { // Evitar NullPointerException
                float iconX = collectiblesTab.getX(); // Posición X
                float iconY = binding.navView.getY(); // Posición Y del BottomNavigationView

                // Obtener tamaño del icono en el menú
                int iconWidth = collectiblesTab.getWidth();
                int iconHeight = collectiblesTab.getHeight();

                // Calcular el centro del icono en X y Y
                float centerX = iconX + (iconWidth / 2f);
                float centerY = iconY + (iconHeight / 2f);

                // Posicionar el anillo resaltador dinámicamente
                guideStep4Binding.highlightRingCollectibles.post(() -> {
                    guideStep4Binding.highlightRingCollectibles.setAlpha(0f);
                    guideStep4Binding.highlightRingCollectibles.setScaleX(0.8f);
                    guideStep4Binding.highlightRingCollectibles.setScaleY(0.8f);

                    guideStep4Binding.highlightRingCollectibles.setX(centerX - (guideStep4Binding.highlightRingCollectibles.getWidth() / 2f));
                    guideStep4Binding.highlightRingCollectibles.setY(centerY - (guideStep4Binding.highlightRingCollectibles.getHeight() / 2f));

                    // Animación del anillo resaltador con efecto más visible
                    guideStep4Binding.highlightRingCollectibles.animate()
                            .alpha(1f)
                            .scaleX(1.5f)
                            .scaleY(1.5f)
                            .setDuration(700)
                            .setInterpolator(new OvershootInterpolator()) // Rebote al final
                            .start();
                });
            } else {
                Log.e("Guide", "No se encontró la opción 'Coleccionables' en BottomNavigationView.");
            }
        }, 300); // Agregar un retraso de 300ms para sincronizar con el cambio de pestaña

        // Animación del texto sobre el anillo
        guideStep4Binding.tvGuideCollectibles.setAlpha(0f);
        guideStep4Binding.tvGuideCollectibles.setTranslationY(50f);
        guideStep4Binding.tvGuideCollectibles.setVisibility(View.VISIBLE);
        guideStep4Binding.tvGuideCollectibles.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(700)
                .setStartDelay(300)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // Animación de la flecha para finalizar la guía
        guideStep4Binding.btnNextStep4.setAlpha(0f);
        guideStep4Binding.btnNextStep4.setTranslationX(50f);
        guideStep4Binding.btnNextStep4.setVisibility(View.VISIBLE);
        guideStep4Binding.btnNextStep4.animate()
                .alpha(1f)
                .translationX(0f)
                .setDuration(700)
                .setStartDelay(600)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Configurar la flecha para ir al paso 5
        guideStep4Binding.btnNextStep4.setOnClickListener(v -> showGuideStep5());

        // Configurar el botón de omitir la guía
        guideStep4Binding.btnSkipGuide4.setOnClickListener(v -> closeGuide());
    }

    private void showGuideStep5() {
        // Ocultar la cuarta pantalla de la guía
        guideStep4Binding.getRoot().setVisibility(View.GONE);

        // Mostrar la quinta pantalla de la guía
        GuideStep5Binding guideStep5Binding = GuideStep5Binding.bind(findViewById(R.id.guide5));
        guideStep5Binding.getRoot().setVisibility(View.VISIBLE);

        // Bloquear eventos táctiles en la guía
        guideStep5Binding.getRoot().setOnTouchListener((v, event) -> true);

        // Animación de la flecha indicando el icono de información
        guideStep5Binding.arrowPointingInfo.setAlpha(0f);
        guideStep5Binding.arrowPointingInfo.animate()
                .alpha(1f)
                .translationX(10f)
                .setDuration(700)
                .setInterpolator(new OvershootInterpolator()) // Rebote al final
                .start();

        // Animación del bocadillo con texto explicativo
        guideStep5Binding.tvGuideInfo.setAlpha(0f);
        guideStep5Binding.tvGuideInfo.setTranslationY(50f);
        guideStep5Binding.tvGuideInfo.setVisibility(View.VISIBLE);
        guideStep5Binding.tvGuideInfo.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(700)
                .setStartDelay(300)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // Animación de la flecha para finalizar la guía
        guideStep5Binding.btnNextStep5.setAlpha(0f);
        guideStep5Binding.btnNextStep5.setTranslationX(50f);
        guideStep5Binding.btnNextStep5.setVisibility(View.VISIBLE);
        guideStep5Binding.btnNextStep5.animate()
                .alpha(1f)
                .translationX(0f)
                .setDuration(700)
                .setStartDelay(600)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Configurar la flecha para finalizar la guía
        guideStep5Binding.btnNextStep5.setOnClickListener(v -> closeGuide());

        // Configurar el botón de omitir la guía
        guideStep5Binding.btnSkipGuide5.setOnClickListener(v -> closeGuide());
    }





    private void closeGuide() {
        // Ocultar cualquier pantalla de la guía si está visible
        if (guideStep2Binding.getRoot().getVisibility() == View.VISIBLE) {
            guideStep2Binding.getRoot().setVisibility(View.GONE);
        }
        if (guideStep3Binding.getRoot().getVisibility() == View.VISIBLE) {
            guideStep3Binding.getRoot().setVisibility(View.GONE);
        }
        if (guideStep4Binding.getRoot().getVisibility() == View.VISIBLE) {
            guideStep4Binding.getRoot().setVisibility(View.GONE);
        }
        if (guideStep5Binding.getRoot().getVisibility() == View.VISIBLE) {
            guideStep5Binding.getRoot().setVisibility(View.GONE);
        }
        // Ocultar la pantalla inicial si no hay otras visibles
        if (guideBinding.getRoot().getVisibility() == View.VISIBLE) {
            guideBinding.getRoot().setVisibility(View.GONE);
        }

        /*// Marcar la guía como completada para que no se vuelva a mostrar
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
