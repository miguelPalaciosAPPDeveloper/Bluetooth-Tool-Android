package com.ad.miguelpalacios.bluetoothtool.Services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.ad.miguelpalacios.bluetoothtool.Admins.AdminGeneralBluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ServiceBluetoothClasico {
    private static final String TAG = ServiceBluetoothClasico.class.getSimpleName();
    private static final boolean D = true;

    private static final String NAME = "BluetoothDEB";

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter BluetoothAdt = null;
    private final Handler mHandler;
    private AcceptThread HebraDeAceptacion;
    private ConnectThread HiloDeConexion;
    private ConnectedThread HiloConetado;
    private int EstadoActual;

    public ServiceBluetoothClasico(Context context, Handler handler){
        BluetoothAdt = BluetoothAdapter.getDefaultAdapter();
        EstadoActual = STATE_NONE;
        mHandler = handler;
    }

    private synchronized void setState(int estado) {
        EstadoActual = estado;

        mHandler.obtainMessage(AdminGeneralBluetooth.mMensajeEstadoCambiado, estado, -1).sendToTarget();
    }

    public synchronized int getState() {
        return EstadoActual;
    }

    public synchronized void start() {
        if (D) Log.e(TAG, "start");

        if (HiloDeConexion != null) {HiloDeConexion.cancel(); HiloDeConexion = null;}

        if (HiloConetado != null) {HiloConetado.cancel(); HiloConetado = null;}

        if (HebraDeAceptacion == null) {
            HebraDeAceptacion = new AcceptThread();
            HebraDeAceptacion.start();
        }
        setState(STATE_LISTEN);
    }

    public synchronized void connect(BluetoothDevice device) {
        if (D) Log.e(TAG, "Conectado con: " + device);

        if (EstadoActual == STATE_CONNECTING) {
            if (HiloDeConexion != null) {HiloDeConexion.cancel(); HiloDeConexion = null;}  }

        if (HiloConetado != null) {HiloConetado.cancel(); HiloConetado = null;}

        HiloDeConexion = new ConnectThread(device);
        HiloDeConexion.start();
        setState(STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (D) Log.e(TAG, "connected");

        if (HiloDeConexion != null) {HiloDeConexion.cancel(); HiloDeConexion = null;}

        if (HiloConetado != null) {HiloConetado.cancel(); HiloConetado = null;}

        if (HebraDeAceptacion != null) {HebraDeAceptacion.cancel(); HebraDeAceptacion = null;}

        HiloConetado = new ConnectedThread(socket);
        HiloConetado.start();

        Message msg = mHandler.obtainMessage(AdminGeneralBluetooth.mMensajeNombreDispositivo);
        Bundle bundle = new Bundle();
        bundle.putString(AdminGeneralBluetooth.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    public synchronized void stop() {
        if (D) Log.e(TAG, "stop");
        if (HiloDeConexion != null) {HiloDeConexion.cancel(); HiloDeConexion = null;}
        if (HiloConetado != null) {HiloConetado.cancel(); HiloConetado = null;}
        if (HebraDeAceptacion != null) {HebraDeAceptacion.cancel(); HebraDeAceptacion = null;}
        setState(STATE_NONE);
    }

    public void write(byte[] out) {
        ConnectedThread r;

        synchronized (this)    {
            if (EstadoActual != STATE_CONNECTED) return;
            r = HiloConetado;  }
        r.write(out);
    }

    private void connectionFailed() {
        setState(STATE_LISTEN);

        Message msg = mHandler.obtainMessage(AdminGeneralBluetooth.mMensajeTOAST);
        Bundle bundle = new Bundle();
        bundle.putString(AdminGeneralBluetooth.TOAST, "Error de conexión");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }


    private void connectionLost() {
        setState(STATE_LISTEN);

        Message msg = mHandler.obtainMessage(AdminGeneralBluetooth.mMensajeTOAST);
        Bundle bundle = new Bundle();
        bundle.putString(AdminGeneralBluetooth.TOAST, "Se perdio conexión");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        msg = mHandler.obtainMessage(AdminGeneralBluetooth.mMensajeDesconectado);
        mHandler.sendMessage(msg);


    }


    private class AcceptThread extends Thread {

        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            try {
                tmp = BluetoothAdt.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "listen() fallo", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            if (D) Log.e(TAG, "Comenzar HiloDeAceptacion " + this);
            setName("HiloAceptado");
            BluetoothSocket socket = null;

            while (EstadoActual != STATE_CONNECTED) {
                try {

                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "accept() failed", e);
                    break;
                }

                if (socket != null) {
                    synchronized (ServiceBluetoothClasico.this) {
                        switch (EstadoActual) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:

                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:

                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "No se pudo cerrar el socket no deseado", e);
                                }
                                break;
                        }
                    }
                }
            }
            if (D) Log.e(TAG, "Fin de HIlodeAceptacion");
        }
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        public void cancel() {
            if (D) Log.e(TAG, "Cancela " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() del servidor FAllo", e);
            }
        }
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    }


    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() Fallo", e);
            }
            mmSocket = tmp;
        }
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        public void run() {
            Log.e(TAG, "Comenzando HebraConectada");
            setName("HiloConectado");
            BluetoothAdt.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException e) {
                connectionFailed();
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "Imposible cerrar el socket durante la falla de conexion", e2);
                }
                ServiceBluetoothClasico.this.start();
                return;
            }
            synchronized (ServiceBluetoothClasico.this) {
                HiloDeConexion = null;
            }
            connected(mmSocket, mmDevice);
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket BTSocket;
        private final InputStream INPUT_Stream;
        private final OutputStream OUTPUT_Stream;


        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "Creacion de HiloConectado");
            BTSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Sockets temporales No creados", e);
            }
            INPUT_Stream = tmpIn;
            OUTPUT_Stream = tmpOut;
        }

        public void write(byte[] buffer) {
            try {
                OUTPUT_Stream.write(buffer);
                mHandler.obtainMessage(AdminGeneralBluetooth.mMensajeEscrito, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);}
        }
        public void cancel() {
            try {
                BTSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() del socket conectado Fallo", e);
            }
        }
        public void leerMensaje(String mensaje){
            byte[] beforeBuffer = mensaje.getBytes();
            int beforeBytes = mensaje.length();
            Log.e(TAG, "mensaje completo: " + mensaje);
            mHandler.obtainMessage(AdminGeneralBluetooth.mMensajeLeido, beforeBytes, 0, beforeBuffer).sendToTarget();
        }
        public void run() {
            Log.e(TAG, "Comenzar Hebraconectada");
            byte[] buffer = new byte[1024];
            int bytes = 0;
            String mensajeSalida = "";

            while (true) {
                try {
                    if(INPUT_Stream.available() > 0) {
                        Log.e(TAG, "bytes: " + bytes);
                        byte[] readBufX = (byte[]) buffer;
                        String readMessageX;
                        if(bytes == 0){
                            bytes = INPUT_Stream.read(buffer);
                            readMessageX = new String(readBufX, 0, bytes);
                            mensajeSalida = readMessageX;
                            Log.e(TAG, "message: " + readMessageX);
                            Thread.sleep(10);
                        }else{
                            bytes = INPUT_Stream.read(buffer);
                            readMessageX = new String(readBufX, 0, bytes);
                            mensajeSalida += readMessageX;
                            Log.e(TAG, "message out: " + mensajeSalida);
                            leerMensaje(mensajeSalida);
                        }

                        Log.e(TAG, "valor entrada: " + bytes);
                    }else {
                        bytes = 0;
                        mensajeSalida = "";
                    }

                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);connectionLost();break;
                } catch (InterruptedException e) {e.printStackTrace();}
            }

        }
    }

}
