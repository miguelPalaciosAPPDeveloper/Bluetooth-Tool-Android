package com.ad.miguelpalacios.bluetoothtool.Activitys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ad.miguelpalacios.bluetoothtool.Adapters.AdapterChatTerminal;
import com.ad.miguelpalacios.bluetoothtool.Admins.AdminBluetoothClasico;
import com.ad.miguelpalacios.bluetoothtool.Admins.AdminBluetoothLE;
import com.ad.miguelpalacios.bluetoothtool.Admins.AdminConfiguraciones.LlavesGeneral;
import com.ad.miguelpalacios.bluetoothtool.Admins.AdminGeneralBluetooth;
import com.ad.miguelpalacios.bluetoothtool.R;
import com.ad.miguelpalacios.bluetoothtool.Services.ServiceBluetoothLE;

public class ActivityGeneral extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = ActivityGeneral.class.getSimpleName();
    private String mNombre;
    private String mTipo;
    private String mDireccion;
    private Button mButton1, mButton2, mButton3, mButton4, mButton5, mButton6, mButton7, mButton8, mButton9;
    private TextView mTextViewEntrada, mTextViewValor;
    private SeekBar mSeekBar;

    private AdminBluetoothClasico mAdminBluetoothClasico;
    private AdminBluetoothLE mAdminBluetoothLE;
    private ServiceBluetoothLE mServiceBluetoothLE;

    private boolean mConnected;
    private SharedPreferences mSharedPreferences;

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (AdminGeneralBluetooth.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                mServiceBluetoothLE = mAdminBluetoothLE.getServiceBluetoothLE();
                mAdminBluetoothLE.actualizarEstadoConexion("Conectado");
                invalidateOptionsMenu();
            } else if (AdminGeneralBluetooth.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                mAdminBluetoothLE.actualizarEstadoConexion("Desconectado");

                invalidateOptionsMenu();
            } else if (AdminGeneralBluetooth.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                mAdminBluetoothLE.displayGattServices(mServiceBluetoothLE.getSupportedGattServices());
            } else if (AdminGeneralBluetooth.ACTION_DATA_AVAILABLE.equals(action)) {
                recibirDato(intent.getStringExtra(AdminGeneralBluetooth.EXTRA_DATA));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT < 19) {
            FrameLayout mFrameLayoutToolBar = (FrameLayout) findViewById(R.id.FrameLayoutToolbar);
            mFrameLayoutToolBar.setVisibility(View.GONE);
        }

        final Intent intent = getIntent();
        mTipo = intent.getStringExtra(AdminGeneralBluetooth.TIPO_DISPOSITIVO);
        mNombre = intent.getStringExtra(AdminGeneralBluetooth.NOMBRE_DISPOSITIVO);
        mDireccion = intent.getStringExtra(AdminGeneralBluetooth.DIRECCION_DISPOSITIVO);

        if (mTipo.equals(getResources().getString(R.string.bluetooth_clasico))){
            mAdminBluetoothClasico = new AdminBluetoothClasico();
            mAdminBluetoothClasico.realizarConexion(this, mHandler, mDireccion);
        }else if(mTipo.equals(getResources().getString(R.string.bluetooth_low_energy))){
            mAdminBluetoothLE = new AdminBluetoothLE(this, mDireccion);
            mAdminBluetoothLE.realizarConexion();
        }

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mTextViewValor = (TextView)findViewById(R.id.textViewValor);
        mTextViewEntrada = (TextView)findViewById(R.id.textViewEntrada);
        mTextViewValor.setText("0");

        mSeekBar = (SeekBar)findViewById(R.id.seekBar);
        leerSeekBar(mSeekBar);

        mButton1 = (Button)findViewById(R.id.button1);
        mButton2 = (Button)findViewById(R.id.button2);
        mButton3 = (Button)findViewById(R.id.button3);
        mButton4 = (Button)findViewById(R.id.button4);
        mButton5 = (Button)findViewById(R.id.button5);
        mButton6 = (Button)findViewById(R.id.button6);
        mButton7 = (Button)findViewById(R.id.button7);
        mButton8 = (Button)findViewById(R.id.button8);
        mButton9 = (Button)findViewById(R.id.button9);

        configurarGeneral();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mTipo.equals(getResources().getString(R.string.bluetooth_low_energy))) {
            registerReceiver(mGattUpdateReceiver, mAdminBluetoothLE.makeGattUpdateIntentFilter());
            if (mServiceBluetoothLE != null) {
                final boolean result = mServiceBluetoothLE.connect(mDireccion);
                Log.d(TAG, "Connect request result=" + result);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mTipo.equals(getResources().getString(R.string.bluetooth_low_energy))) {
            unregisterReceiver(mGattUpdateReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mTipo.equals(getResources().getString(R.string.bluetooth_clasico))){
            mAdminBluetoothClasico.stopServiceBluetooth();
        } else if(mTipo.equals(getResources().getString(R.string.bluetooth_low_energy))){
            mAdminBluetoothLE.terminarServicio();
            mServiceBluetoothLE = null;
        }
    }

    private void configurarGeneral(){
        Button[] botones = {mButton1, mButton2, mButton3, mButton4, mButton5, mButton6, mButton7, mButton8, mButton9};

        for(Button boton : botones){
            boton.setOnClickListener(this);
        }
        int maxValor =  Integer.valueOf(mSharedPreferences.getString(LlavesGeneral.EDT_SEEKBAR_MAX, "100"));
        mSeekBar.setMax(maxValor);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mTipo.equals(getResources().getString(R.string.bluetooth_clasico))){
            mAdminBluetoothClasico.stopServiceBluetooth();
        }
    }

    private void recibirDato(String readMessage) {

        if (readMessage != null) {
            Log.e(TAG, "entrada: " + readMessage);
            enviarTexto(mTextViewEntrada, readMessage);
        }
    }

    @Override
    public void onClick(View view) {
        String mensaje;
        switch (view.getId()){
            case R.id.button1:
                mensaje = mSharedPreferences.getString(LlavesGeneral.EDT_BOTON_1, "1");
                generalEnviar(mensaje);
                break;
            case R.id.button2:
                mensaje = mSharedPreferences.getString(LlavesGeneral.EDT_BOTON_2, "2");
                generalEnviar(mensaje);
                break;
            case R.id.button3:
                mensaje = mSharedPreferences.getString(LlavesGeneral.EDT_BOTON_3, "3");
                generalEnviar(mensaje);
                break;
            case R.id.button4:
                mensaje = mSharedPreferences.getString(LlavesGeneral.EDT_BOTON_4, "4");
                generalEnviar(mensaje);
                break;
            case R.id.button5:
                mensaje = mSharedPreferences.getString(LlavesGeneral.EDT_BOTON_5, "5");
                generalEnviar(mensaje);
                break;
            case R.id.button6:
                mensaje = mSharedPreferences.getString(LlavesGeneral.EDT_BOTON_6, "6");
                generalEnviar(mensaje);
                break;
            case R.id.button7:
                mensaje = mSharedPreferences.getString(LlavesGeneral.EDT_BOTON_7, "7");
                generalEnviar(mensaje);
                break;
            case R.id.button8:
                mensaje = mSharedPreferences.getString(LlavesGeneral.EDT_BOTON_8, "8");
                generalEnviar(mensaje);
                break;
            case R.id.button9:
                mensaje = mSharedPreferences.getString(LlavesGeneral.EDT_BOTON_9, "9");
                generalEnviar(mensaje);
                break;
        }
    }

    private void leerSeekBar(SeekBar seekBar){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                String valor = String.valueOf(seekBar.getProgress());
                enviarTexto(mTextViewValor, valor);

                String mensaje;
                String datoExtra = mSharedPreferences.getString(LlavesGeneral.EDT_DATO_ADICIONAL, "");
                if(!datoExtra.isEmpty()){
                    boolean datoInicio = mSharedPreferences.getBoolean(LlavesGeneral.CHECKBOX_DATO_INICIO, false);
                    if(datoInicio)
                        mensaje = datoExtra + valor;
                    else
                        mensaje = valor + datoExtra;
                }else
                    mensaje = valor;

                generalEnviar(mensaje);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void enviarTexto(final TextView textView,final String texto){
        new Thread(new Runnable() {
            @Override
            public void run() {
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(texto);
                    }
                });
            }
        }).start();
    }

    private void generalEnviar(String mensaje){
        enviarTexto(mTextViewEntrada, "");
        if(mConnected) {
            if (mTipo.equals(getResources().getString(R.string.bluetooth_clasico)))
                mAdminBluetoothClasico.sendMessage(mensaje);
            else if (mTipo.equals(getResources().getString(R.string.bluetooth_low_energy)))
                mAdminBluetoothLE.sendMessage(mensaje);
        }else{
            Toast.makeText(this, getResources().getString(R.string.toast_sin_conexion), Toast.LENGTH_SHORT).show();
        }
    }

    final android.os.Handler mHandler = new android.os.Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case AdminGeneralBluetooth.mMensajeEscrito:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    Log.e(TAG, "Message_write  =w= " + writeMessage);
                    break;
                case AdminGeneralBluetooth.mMensajeLeido:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.e(TAG, "Message_read   =w= "+ readMessage);
                    enviarTexto(mTextViewEntrada, readMessage);
                    break;
                case AdminGeneralBluetooth.mMensajeNombreDispositivo:
                    Toast.makeText(getApplicationContext(), "Conectado con " + mNombre, Toast.LENGTH_SHORT).show();
                    mConnected = true;
                    break;
                case AdminGeneralBluetooth.mMensajeTOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(AdminGeneralBluetooth.TOAST), Toast.LENGTH_SHORT).show();
                    break;
                case AdminGeneralBluetooth.mMensajeDesconectado:
                    mConnected = false;
                    Log.e(TAG,"Desconectados");
                    break;
            }
        }
    };
}
