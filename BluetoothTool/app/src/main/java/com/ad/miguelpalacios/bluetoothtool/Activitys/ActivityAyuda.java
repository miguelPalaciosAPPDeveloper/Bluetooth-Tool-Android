package com.ad.miguelpalacios.bluetoothtool.Activitys;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.ad.miguelpalacios.bluetoothtool.Dialogs.DialogAyuda;
import com.ad.miguelpalacios.bluetoothtool.R;

public class ActivityAyuda extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = ActivityAyuda.class.getSimpleName();
    private static final String SALIR = "Salir";
    private static final String CONFIGURACION = "Configuración";
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_activity_ayuda);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);

        if(Build.VERSION.SDK_INT < 19)
        {
            FrameLayout mFrameLayoutToolBar = (FrameLayout)findViewById(R.id.FrameLayoutToolbar);
            mFrameLayoutToolBar.setVisibility(View.GONE);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.getMenu().getItem(2).setChecked(true);
            setupDrawerContent(navigationView);
            selectItem(navigationView.getMenu().getItem(2));
        }

        ConfigurarAyudas();

    }

    private void ConfigurarAyudas(){
        CardView cardViewBuscarB = (CardView)findViewById(R.id.cardViewBuscarB);
        CardView cardViewTerminal = (CardView)findViewById(R.id.cardViewTerminal);
        CardView cardViewJoystick = (CardView)findViewById(R.id.cardViewJoystick);
        CardView cardViewConexionB = (CardView)findViewById(R.id.cardViewConexionB);
        CardView cardViewGeneral = (CardView)findViewById(R.id.cardViewGeneral);
        CardView cardViewConfiguraciones = (CardView)findViewById(R.id.cardViewConfiguraciones);

        CardView[] arrayCardView = {cardViewBuscarB, cardViewTerminal, cardViewJoystick, cardViewConexionB, cardViewGeneral, cardViewConfiguraciones};
        for(CardView cardView : arrayCardView){
            cardView.setOnClickListener(this);
        }
    }

    private void setupDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        Log.e(TAG, menuItem.getTitle().toString());
                        if (menuItem.getTitle().toString().equals(CONFIGURACION)) {
                            navigationView.getMenu().findItem(R.id.navigationConfiguracion).setCheckable(false);
                            selectItem(menuItem);
                        } else if (menuItem.getTitle().toString().equals(SALIR)) {
                            finish();
                        } else
                            selectItem(menuItem);
                        return true;
                    }
                }
        );
    }

    private void selectItem(MenuItem itemDrawer) {
        switch (itemDrawer.getItemId()) {
            case R.id.navigationBusqueda:
                //Iniciar actividad de inicio
                startActivity(new Intent(this, ActivityPrincipal.class));
                finish();
                break;
            case R.id.navigationConfiguracion:
                // Iniciar actividad de configuración
                startActivity(new Intent(this, ActivitySettings.class));

                break;
            case R.id.navigationAyuda:
                //Iniciar actividad de ayuda
                break;
        }

        mDrawerLayout.closeDrawers();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, ActivityPrincipal.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home)
        {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        String titulo;
        String[] descripcion;
        Drawable[] imagen;
        Drawable drawable_1;
        Drawable drawable_2;
        Drawable drawable_3;
        Drawable drawable_4;

        switch (view.getId()){
            case R.id.cardViewBuscarB:
                titulo = getResources().getString(R.string.ayuda_buscar_bluetooth);
                descripcion = getResources().getStringArray(R.array.ayuda_busqueda);
                drawable_1 = getResources().getDrawable(R.drawable.ayuda_busqueda_1);
                drawable_2 = getResources().getDrawable(R.drawable.ayuda_busqueda_2);
                drawable_3 = getResources().getDrawable(R.drawable.ayuda_busqueda_3);
                drawable_4 = getResources().getDrawable(R.drawable.ayuda_busqueda_4);
                imagen = new Drawable[]{drawable_1, drawable_2, drawable_3, drawable_4};
                new DialogAyuda(this).CrearDialog(titulo, descripcion, imagen);
                break;
            case R.id.cardViewTerminal:
                titulo = getResources().getString(R.string.ayuda_terminal);
                descripcion = getResources().getStringArray(R.array.ayuda_terminal);
                drawable_1 = getResources().getDrawable(R.drawable.ayuda_terminal_1);
                drawable_2 = getResources().getDrawable(R.drawable.ayuda_terminal_2);
                imagen = new Drawable[]{drawable_1, drawable_2};
                new DialogAyuda(this).CrearDialog(titulo, descripcion, imagen);
                break;
            case R.id.cardViewJoystick:
                titulo = getResources().getString(R.string.ayuda_Joystick);
                descripcion = getResources().getStringArray(R.array.ayuda_joystick);
                drawable_1 = getResources().getDrawable(R.drawable.ayuda_joystick_1);
                drawable_2 = getResources().getDrawable(R.drawable.ayuda_joystick_2);
                imagen = new Drawable[]{drawable_1, drawable_2};
                new DialogAyuda(this).CrearDialog(titulo, descripcion, imagen);
                break;
            case R.id.cardViewConexionB:
                titulo = getResources().getString(R.string.ayuda_conexion_bluetooth);
                descripcion = getResources().getStringArray(R.array.ayuda_conexion);
                drawable_1 = getResources().getDrawable(R.drawable.ayuda_conexion_1);
                drawable_2 = getResources().getDrawable(R.drawable.ayuda_conexion_2);
                imagen = new Drawable[]{drawable_1, drawable_2};
                new DialogAyuda(this).CrearDialog(titulo, descripcion, imagen);
                break;
            case R.id.cardViewGeneral:
                titulo = getResources().getString(R.string.ayuda_general);
                descripcion = getResources().getStringArray(R.array.ayuda_General);
                drawable_1 = getResources().getDrawable(R.drawable.ayuda_general_1);
                drawable_2 = getResources().getDrawable(R.drawable.ayuda_general_2);
                drawable_3 = getResources().getDrawable(R.drawable.ayuda_general_3);
                imagen = new Drawable[]{drawable_1, drawable_2, drawable_3};
                new DialogAyuda(this).CrearDialog(titulo, descripcion, imagen);
                break;
            case R.id.cardViewConfiguraciones:
                titulo = getResources().getString(R.string.ayuda_configuracion);
                descripcion = getResources().getStringArray(R.array.ayuda_configuraciones);
                drawable_1 = getResources().getDrawable(R.drawable.ayuda_configuraciones_1);
                drawable_2 = getResources().getDrawable(R.drawable.ayuda_configuraciones_2);
                drawable_3 = getResources().getDrawable(R.drawable.ayuda_configuraciones_3);
                imagen = new Drawable[]{drawable_1, drawable_2, drawable_3};
                new DialogAyuda(this).CrearDialog(titulo, descripcion, imagen);
                break;
        }

    }
}
