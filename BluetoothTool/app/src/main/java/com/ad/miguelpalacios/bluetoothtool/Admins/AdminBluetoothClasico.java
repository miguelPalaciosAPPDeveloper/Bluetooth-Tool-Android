package com.ad.miguelpalacios.bluetoothtool.Admins;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import com.ad.miguelpalacios.bluetoothtool.Services.ServiceBluetoothClasico;

/**
 * Created by miguelpalacios on 14/01/16.
 */
public class AdminBluetoothClasico {
    private static final String TAG = AdminBluetoothClasico.class.getSimpleName();

    private ServiceBluetoothClasico mServiceBluetoothClasico;

    public AdminBluetoothClasico(){

    }

    public void realizarConexion(Context context,android.os.Handler handler, String direccion){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter.isEnabled()){
            if (mServiceBluetoothClasico == null) {//y el Servicio_BT es nulo, invocamos el Servicio_BT
                mServiceBluetoothClasico = new ServiceBluetoothClasico(context, handler);

                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(direccion);
                mServiceBluetoothClasico.connect(device);
            }
        }
    }

    public void stopServiceBluetooth(){
        if(mServiceBluetoothClasico != null){mServiceBluetoothClasico.stop();}
    }

    public  void sendMessage(String message) {
        if (mServiceBluetoothClasico.getState() == ServiceBluetoothClasico.STATE_CONNECTED) {
            if (message.length() > 0) {
                byte[] send = message.getBytes();
                Log.e(TAG, "Mensaje enviado:" + message);
                mServiceBluetoothClasico.write(send);
            }
        }
    }
}
