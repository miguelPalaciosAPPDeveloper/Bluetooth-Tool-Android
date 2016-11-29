package com.ad.miguelpalacios.bluetoothtool.Activitys;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ad.miguelpalacios.bluetoothtool.Admins.AdminGeneralBluetooth;
import com.ad.miguelpalacios.bluetoothtool.Admins.AdminScanBluetooth;
import com.ad.miguelpalacios.bluetoothtool.R;

public class ActivityPrincipal extends AppCompatActivity {
    private static final String TAG = ActivityPrincipal.class.getSimpleName();
    private static final String SALIR = "Salir";
    private static final String CONFIGURACION = "Configuración";
    private static final int REQUEST_ENABLE_BT = 1;
    private DrawerLayout mDrawerLayout;
    private TextView mTextViewNombre, mTextViewTipo, mTextViewMac;
    private Button mButtonConectar;
    private AdminScanBluetooth mAdminScanBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_activity_principal);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);

        if(Build.VERSION.SDK_INT < 19)
        {
            FrameLayout mFrameLayoutToolBar = (FrameLayout)findViewById(R.id.FrameLayoutToolbar);
            mFrameLayoutToolBar.setVisibility(View.GONE);
        }

        mAdminScanBluetooth = new AdminScanBluetooth(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mTextViewNombre = (TextView)findViewById(R.id.textViewNombre);
        mTextViewTipo = (TextView)findViewById(R.id.textViewTipo);
        mTextViewMac = (TextView)findViewById(R.id.textViewMac);

        ListView listViewDispositivos = (ListView)findViewById(R.id.listViewDispositivos);
        listViewDispositivos.setAdapter(mAdminScanBluetooth.getListDevicesArrayAdapter());
        listViewDispositivos.setOnItemClickListener(mDeviceClickListener);

        mAdminScanBluetooth.verificacionActivacionBluetooth();
        mAdminScanBluetooth.configuracionBusquedaBluetooth();

        mButtonConectar = (Button)findViewById(R.id.buttonConectar);

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.getMenu().getItem(0).setChecked(true);
            setupDrawerContent(navigationView);
            selectItem(navigationView.getMenu().getItem(0));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(ActivityPrincipal.this, R.string.toast_bluetooth_encendido, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ActivityPrincipal.this, R.string.toast_bluetooth_apagado, Toast.LENGTH_SHORT).show();
                finish();
            }
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
                break;
            case R.id.navigationConfiguracion:
                // Iniciar actividad de configuración
                startActivity(new Intent(this, ActivitySettings.class));

                break;
            case R.id.navigationAyuda:
                //Iniciar actividad de ayuda
                startActivity(new Intent(this, ActivityAyuda.class));
                finish();
                break;
        }

        mDrawerLayout.closeDrawers();
    }

    public void buscarDispositivosClick(View view)
    {
        mTextViewMac.setText("");
        mTextViewNombre.setText("");
        mTextViewTipo.setText("");
        mButtonConectar.setEnabled(false);
        mAdminScanBluetooth.clearArrayAdapter();
        mAdminScanBluetooth.scanBluetoothDevices();
    }

    public void conectarClick(View view){
        if(mTextViewTipo.getText().toString().equals(getResources().getString(R.string.bluetooth_low_energy))){
            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                Toast.makeText(this, "Lo sentimos tu movil no cuenta con este tipo de Bluetooth", Toast.LENGTH_SHORT).show();
            } else {
                Conectar();
            }
        } else
            Conectar();
    }

    private void Conectar(){
        mAdminScanBluetooth.cancelScanBlutoothDevices();
        Intent intent = new Intent(this, ActivityConexionBluetooth.class);
        intent.putExtra(AdminGeneralBluetooth.DIRECCION_DISPOSITIVO, mTextViewMac.getText().toString());
        intent.putExtra(AdminGeneralBluetooth.NOMBRE_DISPOSITIVO, mTextViewNombre.getText().toString());
        intent.putExtra(AdminGeneralBluetooth.TIPO_DISPOSITIVO, mTextViewTipo.getText().toString());
        startActivity(intent);
        finish();
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            mButtonConectar.setEnabled(true);
            Log.e(TAG, "onItemClick");
            mAdminScanBluetooth.cancelScanBlutoothDevices();
            Log.e(TAG, "nombre: " + mAdminScanBluetooth.getDeviceName(i));
            mTextViewNombre.setText(mAdminScanBluetooth.getDeviceName(i));
            mTextViewMac.setText(mAdminScanBluetooth.getDeviceAddress(i));
            mTextViewTipo.setText(mAdminScanBluetooth.getDeviceType(i));
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        mAdminScanBluetooth.cancelScanBlutoothDevices();
        Log.e(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdminScanBluetooth.cancelScanBlutoothDevices();
        mAdminScanBluetooth.salida();
        Log.e(TAG, "onDestroy");
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
        if(id == android.R.id.home)
        {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }
}
