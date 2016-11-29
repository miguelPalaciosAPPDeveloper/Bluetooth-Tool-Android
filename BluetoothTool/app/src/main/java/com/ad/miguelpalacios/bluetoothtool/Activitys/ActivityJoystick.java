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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ad.miguelpalacios.bluetoothtool.Admins.AdminBluetoothClasico;
import com.ad.miguelpalacios.bluetoothtool.Admins.AdminBluetoothLE;
import com.ad.miguelpalacios.bluetoothtool.Admins.AdminConfiguraciones.LlavesJoystick;
import com.ad.miguelpalacios.bluetoothtool.Admins.AdminGeneralBluetooth;
import com.ad.miguelpalacios.bluetoothtool.Admins.AdminJoystick;
import com.ad.miguelpalacios.bluetoothtool.R;
import com.ad.miguelpalacios.bluetoothtool.Services.ServiceBluetoothLE;

public class ActivityJoystick extends AppCompatActivity implements View.OnClickListener{
    private static String TAG = ActivityJoystick.class.getSimpleName();
    private String mNombre;
    private String mTipo;
    private String mDireccion;
    private boolean mMultiplesEnvios = false;
    private boolean mConnected;
    private Button mButtonY, mButtonX, mButtonB, mButtonA;
    private TextView mTextViewEntrada;

    private SharedPreferences mSharedPreferences;
    private AdminJoystick mAdminJoystick;
    private AdminBluetoothClasico mAdminBluetoothClasico;
    private AdminBluetoothLE mAdminBluetoothLE;
    private ServiceBluetoothLE mServiceBluetoothLE;

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
        setContentView(R.layout.activity_joystick);
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

        mTextViewEntrada = (TextView)findViewById(R.id.textViewEntrada);
        RelativeLayout mRelativeLayoutJoystick = (RelativeLayout)findViewById(R.id.relativeLayoutJoystick);

        mButtonY = (Button)findViewById(R.id.buttonY);
        mButtonX = (Button)findViewById(R.id.buttonX);
        mButtonB = (Button)findViewById(R.id.buttonB);
        mButtonA = (Button)findViewById(R.id.buttonA);

        mAdminJoystick = new AdminJoystick(this, mRelativeLayoutJoystick, android.R.drawable.toast_frame);

        configurarJoystick();

        mRelativeLayoutJoystick.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mAdminJoystick.drawStick(motionEvent);

                /*if ((motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE) && mConnected) {
                    int direction = mAdminJoystick.get4Direction();
                    String mensaje = "";
                    if(direction == AdminJoystick.STICK_UP){
                        Log.e(TAG, "Palanca arriba");
                        mensaje = mSharedPreferences.getString(LlavesJoystick.EDT_POSICION_UP, "");
                    } else if(direction == AdminJoystick.STICK_RIGHT){
                        Log.e(TAG, "Palanca derecha");
                        mensaje = mSharedPreferences.getString(LlavesJoystick.EDT_POSICION_RIGHT, "");
                    } else if(direction == AdminJoystick.STICK_LEFT){
                        Log.e(TAG, "Palanca izquierda");
                        mensaje = mSharedPreferences.getString(LlavesJoystick.EDT_POSICION_LEFT, "");
                    } else if(direction == AdminJoystick.STICK_DOWN){
                        Log.e(TAG, "Palanca abajo");
                        mensaje = mSharedPreferences.getString(LlavesJoystick.EDT_POSICION_DOWN, "");
                    }

                    hiloJoystickEnviar(mensaje);
                }*/
                if(mMultiplesEnvios){
                    multiplesEnvios(motionEvent);
                } else {
                    unicoEnvio(motionEvent);
                }
                return true;
            }
        });
    }

    private void unicoEnvio(MotionEvent motionEvent){
        if ((motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE) && mConnected) {
            int direction = mAdminJoystick.get4Direction();
            String mensaje = "";
            if (direction == AdminJoystick.STICK_UP) {
                Log.e(TAG, "un envio: Palanca arriba");
                mensaje = mSharedPreferences.getString(LlavesJoystick.EDT_POSICION_UP, "U");
            } else if (direction == AdminJoystick.STICK_RIGHT) {
                Log.e(TAG, "un envio: Palanca derecha");
                mensaje = mSharedPreferences.getString(LlavesJoystick.EDT_POSICION_RIGHT, "R");
            } else if (direction == AdminJoystick.STICK_LEFT) {
                Log.e(TAG, "un envio: Palanca izquierda");
                mensaje = mSharedPreferences.getString(LlavesJoystick.EDT_POSICION_LEFT, "L");
            } else if (direction == AdminJoystick.STICK_DOWN) {
                Log.e(TAG, "un envio: Palanca abajo");
                mensaje = mSharedPreferences.getString(LlavesJoystick.EDT_POSICION_DOWN, "D");
            }

            hiloJoystickEnviar(mensaje);
        }
    }

    private void multiplesEnvios(final MotionEvent motionEvent){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while((motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE) && mConnected){
                    int direction = mAdminJoystick.get4Direction();
                    String mensaje = "";
                    if(direction == AdminJoystick.STICK_UP){
                        Log.e(TAG, "multiple envio: Palanca arriba");
                        mensaje = mSharedPreferences.getString(LlavesJoystick.EDT_POSICION_UP, "U");
                    } else if(direction == AdminJoystick.STICK_RIGHT){
                        Log.e(TAG, "multiple envio: Palanca derecha");
                        mensaje = mSharedPreferences.getString(LlavesJoystick.EDT_POSICION_RIGHT, "R");
                    } else if(direction == AdminJoystick.STICK_LEFT){
                        Log.e(TAG, "multiple envio: Palanca izquierda");
                        mensaje = mSharedPreferences.getString(LlavesJoystick.EDT_POSICION_LEFT, "L");
                        joystickEnviar(mensaje);
                    } else if(direction == AdminJoystick.STICK_DOWN){
                        Log.e(TAG, "multiple envio: Palanca abajo");
                        mensaje = mSharedPreferences.getString(LlavesJoystick.EDT_POSICION_DOWN, "D");
                    }

                    joystickEnviar(mensaje);
                    retardo();
                }
            }
        }).start();
    }

    private void retardo(){
        try {
            Thread.sleep(25);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
        }else if(mTipo.equals(getResources().getString(R.string.bluetooth_low_energy))){
            mAdminBluetoothLE.terminarServicio();
            mServiceBluetoothLE = null;
        }
    }

    private void configurarJoystick(){
        Button[] botones = {mButtonY, mButtonX, mButtonB, mButtonA};
        for(Button boton : botones){
            boton.setOnClickListener(this);
        }

        mAdminJoystick.setStickSize(130, 130);
        mAdminJoystick.setLayoutAlpha(150);
        mAdminJoystick.setStickAlpha(100);
        mAdminJoystick.setOffset(90);
        mAdminJoystick.setMinimumDistance(50);

        mMultiplesEnvios = mSharedPreferences.getBoolean(LlavesJoystick.CHECKBOX_MULTIPLES_ENVIOS, false);
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
        switch (view.getId()) {
            case R.id.buttonY:
                mensaje = mSharedPreferences.getString(LlavesJoystick.EDT_BOTON_Y, "Y");
                //joystickEnviar(mensaje);
                hiloJoystickEnviar(mensaje);
                break;
            case R.id.buttonX:
                mensaje = mSharedPreferences.getString(LlavesJoystick.EDT_BOTON_X, "X");
                //joystickEnviar(mensaje);
                hiloJoystickEnviar(mensaje);
                break;
            case R.id.buttonB:
                mensaje = mSharedPreferences.getString(LlavesJoystick.EDT_BOTON_B, "B");
                //joystickEnviar(mensaje);
                hiloJoystickEnviar(mensaje);
                break;
            case R.id.buttonA:
                mensaje = mSharedPreferences.getString(LlavesJoystick.EDT_BOTON_A, "A");
                //joystickEnviar(mensaje);
                hiloJoystickEnviar(mensaje);
                break;
        }
    }

    private void hiloJoystickEnviar(final String mensaje){
        new Thread(new Runnable() {
            @Override
            public void run() {
                joystickEnviar(mensaje);
            }
        }).start();
    }

    private void joystickEnviar(String mensaje){
        enviarTexto(mTextViewEntrada, "");
        if(mConnected) {
            if (mTipo.equals(getResources().getString(R.string.bluetooth_clasico)))
                mAdminBluetoothClasico.sendMessage(mensaje);
            else if (mTipo.equals(getResources().getString(R.string.bluetooth_low_energy)))
                mAdminBluetoothLE.sendMessage(mensaje);
        }
    }

    private void enviarTexto(final TextView textView, final String texto){
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
