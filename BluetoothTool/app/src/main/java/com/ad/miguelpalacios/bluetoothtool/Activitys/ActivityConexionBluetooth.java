package com.ad.miguelpalacios.bluetoothtool.Activitys;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.ad.miguelpalacios.bluetoothtool.Admins.AdminGeneralBluetooth;
import com.ad.miguelpalacios.bluetoothtool.R;

public class ActivityConexionBluetooth extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ActivityConexionBluetooth.class.getSimpleName();
    private static final String SALIR = "Salir";
    private static final String CONFIGURACION = "Configuración";

    private DrawerLayout mDrawerLayout;
    private String mDireccion;
    private String mNombre;
    private String mTipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_activity_conexion_bluetooth);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT < 19) {
            FrameLayout mFrameLayoutToolBar = (FrameLayout) findViewById(R.id.FrameLayoutToolbar);
            mFrameLayoutToolBar.setVisibility(View.GONE);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        final Intent intent = getIntent();
        mTipo = intent.getStringExtra(AdminGeneralBluetooth.TIPO_DISPOSITIVO);
        mDireccion = intent.getStringExtra(AdminGeneralBluetooth.DIRECCION_DISPOSITIVO);
        mNombre = intent.getStringExtra(AdminGeneralBluetooth.NOMBRE_DISPOSITIVO);

        CardView mCardViewTerminal = (CardView)findViewById(R.id.cardViewTerminal);
        CardView mCardViewGeneral = (CardView)findViewById(R.id.cardViewGeneral);
        CardView mCardViewControl = (CardView)findViewById(R.id.cardViewControl);

        mCardViewTerminal.setOnClickListener(this);
        mCardViewGeneral.setOnClickListener(this);
        mCardViewControl.setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setupDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        if(menuItem.getTitle().toString().equals(CONFIGURACION)) {
                            navigationView.getMenu().findItem(R.id.navigationConfiguracion).setCheckable(false);
                            selectItem(menuItem);
                        }
                        else if (menuItem.getTitle().toString().equals(SALIR)) {
                            finish();
                        } else {
                            selectItem(menuItem);
                        }
                        return true;
                    }
                }
        );
    }

    private void selectItem(MenuItem itemDrawer) {
        switch (itemDrawer.getItemId()) {
            case R.id.navigationBusqueda:
                onBackPressed();
                break;
            case R.id.navigationConfiguracion:
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
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            return true;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        int cardViewID = view.getId();
        switch (cardViewID){
            case R.id.cardViewTerminal:
                conectar(ActivityTerminal.class);
                break;
            case R.id.cardViewGeneral:
                conectar(ActivityGeneral.class);
                break;
            case R.id.cardViewControl:
                conectar(ActivityJoystick.class);
                break;
        }
    }

    private void conectar(Class clase){
        Intent intent = new Intent(this, clase);
        intent.putExtra(AdminGeneralBluetooth.TIPO_DISPOSITIVO, mTipo);
        intent.putExtra(AdminGeneralBluetooth.NOMBRE_DISPOSITIVO, mNombre);
        intent.putExtra(AdminGeneralBluetooth.DIRECCION_DISPOSITIVO, mDireccion);
        startActivity(intent);
    }
}
