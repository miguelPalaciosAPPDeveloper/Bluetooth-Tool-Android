package com.ad.miguelpalacios.bluetoothtool.Activitys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ad.miguelpalacios.bluetoothtool.Adapters.AdapterChatTerminal;
import com.ad.miguelpalacios.bluetoothtool.Admins.AdminBluetoothClasico;
import com.ad.miguelpalacios.bluetoothtool.Admins.AdminBluetoothLE;
import com.ad.miguelpalacios.bluetoothtool.Admins.AdminConfiguraciones.LlavesTerminal;
import com.ad.miguelpalacios.bluetoothtool.Admins.AdminGeneralBluetooth;
import com.ad.miguelpalacios.bluetoothtool.R;
import com.ad.miguelpalacios.bluetoothtool.Services.ServiceBluetoothLE;

public class ActivityTerminal extends AppCompatActivity {
    private static final String TAG = ActivityTerminal.class.getSimpleName();
    private static final String BLANCO = "Blanco";
    private static final String AZUL = "Azul";
    private static final String ROJO  = "Rojo";
    private static final String VERDE = "Verde";
    private EditText mEditTextMensaje;
    private ListView mListViewContenido;

    private AdminBluetoothClasico mAdminBluetoothClasico;
    private AdminBluetoothLE mAdminBluetoothLE;
    private AdapterChatTerminal mAdapterChatTerminal;
    private ServiceBluetoothLE mServiceBluetoothLE;

    private String mNombre;
    private String mTipo;
    private String mDireccion;
    private String mUsuario = "Usuario";
    private boolean mConnected;

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
                //clearUI();
                mAdapterChatTerminal.clear();
            } else if (AdminGeneralBluetooth.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                mAdminBluetoothLE.displayGattServices(mServiceBluetoothLE.getSupportedGattServices());
            } else if (AdminGeneralBluetooth.ACTION_DATA_AVAILABLE.equals(action)) {
                recibirDato(intent.getStringExtra(AdminGeneralBluetooth.EXTRA_DATA));
            }
        }
    };

    /*private void clearUI() {
        //mDataField.setText(R.string.no_data);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);

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


        mEditTextMensaje = (EditText)findViewById(R.id.editTextMensaje);
        mListViewContenido = (ListView)findViewById(R.id.listViewContenido);



    }

    private void configuracionesTerminal(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mUsuario = sharedPreferences.getString(LlavesTerminal.EDT_NOMBRE_USUARIO, "Usuario");

        String color = sharedPreferences.getString(LlavesTerminal.LIST_COLOR_TEXTO, "Blanco");
        switch (color){
            case BLANCO:
                mAdapterChatTerminal.setColorText(Color.WHITE);
                break;
            case AZUL:
                mAdapterChatTerminal.setColorText(Color.BLUE);
                break;
            case ROJO:
                mAdapterChatTerminal.setColorText(Color.RED);
                break;
            case VERDE:
                mAdapterChatTerminal.setColorText(Color.GREEN);
                break;
        }
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

        mAdapterChatTerminal = new AdapterChatTerminal(this);
        configuracionesTerminal();
        mListViewContenido.setAdapter(mAdapterChatTerminal);
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
        mAdapterChatTerminal.clear();
        if(mTipo.equals(getResources().getString(R.string.bluetooth_clasico))){
            mAdminBluetoothClasico.stopServiceBluetooth();
        } else if(mTipo.equals(getResources().getString(R.string.bluetooth_low_energy))){
            mAdminBluetoothLE.terminarServicio();
            mServiceBluetoothLE = null;
        }
    }

    public void terminalEnviar(View view){
        String mensajeTerminal = mUsuario + ": " + mEditTextMensaje.getText().toString();
        String mensaje = mEditTextMensaje.getText().toString();
        Log.e(TAG, mensajeTerminal);
        mAdapterChatTerminal.addConversation(mensajeTerminal);
        mAdapterChatTerminal.notifyDataSetChanged();
        if(mConnected) {
            if (mTipo.equals(getResources().getString(R.string.bluetooth_clasico))){mAdminBluetoothClasico.sendMessage(mensaje);}
            else if(mTipo.equals(getResources().getString(R.string.bluetooth_low_energy))){mAdminBluetoothLE.sendMessage(mensaje);}
        }

        mEditTextMensaje.setText("");
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
        mAdapterChatTerminal.clear();
    }

    private void recibirDato(String readMessage) {

        if (readMessage != null) {
            Log.e(TAG, "entrada: " + readMessage);
            mAdapterChatTerminal.addConversation(mNombre + ": " + readMessage);
            mAdapterChatTerminal.notifyDataSetChanged();
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
                    mAdapterChatTerminal.addConversation(mNombre + ": " + readMessage);
                    mAdapterChatTerminal.notifyDataSetChanged();
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
