package com.ad.miguelpalacios.bluetoothtool.Admins;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.ad.miguelpalacios.bluetoothtool.Attributes.SampleGattAttributes;
import com.ad.miguelpalacios.bluetoothtool.Services.ServiceBluetoothLE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by miguelpalacios on 02/05/16.
 */
public class AdminBluetoothLE {
    private static final String TAG = AdminBluetoothLE.class.getSimpleName();
    private ServiceBluetoothLE mServiceBluetoothLE;
    private String mDireccion;
    private Activity mActivity;

    private BluetoothGattCharacteristic characteristicTX;
    private BluetoothGattCharacteristic characteristicRX;

    public final static UUID HM_RX_TX = UUID.fromString(SampleGattAttributes.HM_RX_TX);

    public AdminBluetoothLE(Activity activity, String direccion){
        mActivity = activity;
        mDireccion = direccion;
    }

    public ServiceBluetoothLE getServiceBluetoothLE(){
        Log.e(TAG, "getService");
        return mServiceBluetoothLE;
    }

    public void realizarConexion(){
        Intent gattServiceIntent = new Intent(mActivity, ServiceBluetoothLE.class);
        mActivity.bindService(gattServiceIntent, mServiceConnection, mActivity.BIND_AUTO_CREATE);
    }

    public void actualizarEstadoConexion(final String texto) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //mConnectionState.setText(resourceId);
                //getSupportActionBar().setTitle(resourceId);
                //sendMessage(modeloDeviceControl.getEnvioConexion());
                Log.e(TAG, texto);
            }
        });
    }

    public void sendMessage(String mensaje) {
        Log.d(TAG, "Sending result=" + mensaje);
        final byte[] tx = mensaje.getBytes();
        characteristicTX.setValue(tx);
        mServiceBluetoothLE.writeCharacteristic(characteristicTX);
        mServiceBluetoothLE.setCharacteristicNotification(characteristicRX,true);
    }

    public void terminarServicio(){
        mActivity.unbindService(mServiceConnection);
        mServiceBluetoothLE = null;
    }

    public void displayGattServices(List<BluetoothGattService> gattServices) {
        final String LIST_NAME = "NAME";
        final String LIST_UUID = "UUID";

        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = "getResources().getString(R.string.unknown_service)";
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();


        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));

            // If the service exists for HM 10 Serial, say so.
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            // get characteristic when UUID matches RX/TX UUID
            characteristicTX = gattService.getCharacteristic(AdminGeneralBluetooth.UUID_HM_RX_TX);
            characteristicRX = gattService.getCharacteristic(AdminGeneralBluetooth.UUID_HM_RX_TX);
        }

    }

    public IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AdminGeneralBluetooth.ACTION_GATT_CONNECTED);
        intentFilter.addAction(AdminGeneralBluetooth.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(AdminGeneralBluetooth.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(AdminGeneralBluetooth.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mServiceBluetoothLE = ((ServiceBluetoothLE.LocalBinder) service).getService();
            if (!mServiceBluetoothLE.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                mActivity.finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            //mServiceBluetoothLE.connect(mDireccion);
            Log.e(TAG, "successful");
            mServiceBluetoothLE.connect(mDireccion);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mServiceBluetoothLE = null;
        }
    };
}
