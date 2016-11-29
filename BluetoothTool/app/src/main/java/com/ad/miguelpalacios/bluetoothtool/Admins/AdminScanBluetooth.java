package com.ad.miguelpalacios.bluetoothtool.Admins;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.ad.miguelpalacios.bluetoothtool.R;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by miguelpalacios on 19/12/15.
 */
public class AdminScanBluetooth {
    private static final String TAG = AdminScanBluetooth.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private Activity mActivity;
    private ArrayAdapter<String> mListDevicesArrayAdapter;
    private ArrayList<BluetoothDevice> mDatosBluetooth;

    public ArrayAdapter<String> getListDevicesArrayAdapter() {return mListDevicesArrayAdapter;}
    public void clearArrayAdapter(){
        mListDevicesArrayAdapter.clear();
    }

    public String getDeviceName(int position){
        return mDatosBluetooth.get(position).getName();
    }
    public String getDeviceAddress(int position){
        return mDatosBluetooth.get(position).getAddress();
    }
    public String getDeviceType(int position){
        String tipo = "";
        if(mDatosBluetooth.get(position).getType() == 1){
            tipo = (String) mActivity.getResources().getText(R.string.bluetooth_clasico);
        }else if(mDatosBluetooth.get(position).getType() == 2){
            tipo = (String) mActivity.getResources().getText(R.string.bluetooth_low_energy);
        }

        return tipo;
    }


    public AdminScanBluetooth(Activity activity){
        mActivity = activity;
        mListDevicesArrayAdapter = new ArrayAdapter<String>(mActivity, R.layout.nombre_dispositivo);
        mDatosBluetooth = new ArrayList<BluetoothDevice>();
    }

    public void verificacionActivacionBluetooth(){
        final BluetoothManager bluetoothManager = (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(mActivity, R.string.toast_bluetooth_null, Toast.LENGTH_SHORT).show();
            mActivity.finish();
            return;
        }

        if(!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    public void configuracionBusquedaBluetooth(){
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mActivity.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mActivity.registerReceiver(mReceiver, filter);
    }

    public void scanBluetoothDevices(){
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                mListDevicesArrayAdapter.add(device.getName());
                mDatosBluetooth.add(device);
            }
        }
        if (mBluetoothAdapter.isDiscovering()){mBluetoothAdapter.cancelDiscovery();}
        mBluetoothAdapter.startDiscovery();
    }

    public void cancelScanBlutoothDevices(){
        if(mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
        }
    }

    public void salida()
    {
        mActivity.unregisterReceiver(mReceiver);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String mAction = intent.getAction();

            Log.e(TAG, "Buscando...");

            if(BluetoothDevice.ACTION_FOUND.equals(mAction))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if (device.getName() != null && device.getName().length() > 0) {
                        Log.e(TAG, "device: " + device.getName());
                        mListDevicesArrayAdapter.add(device.getName());
                        mDatosBluetooth.add(device);
                    }
                }
            }

            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(mAction))
            {
                if(mListDevicesArrayAdapter.getCount() == 0)
                {
                    //String mNoDevices = mActivity.getResources().getText(R.string.admin_scan_bluetooth_no_dispositivos).toString();
                    //Toast.makeText(mActivity, mNoDevices, Toast.LENGTH_SHORT).show();
                }

                Log.e(TAG, "Fin busqueda");
            }
        }
    };
}
