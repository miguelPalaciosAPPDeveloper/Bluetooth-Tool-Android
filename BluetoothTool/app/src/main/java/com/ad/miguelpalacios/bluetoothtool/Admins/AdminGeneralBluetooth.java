package com.ad.miguelpalacios.bluetoothtool.Admins;

import com.ad.miguelpalacios.bluetoothtool.Attributes.SampleGattAttributes;

import java.util.UUID;

/**
 * Created by miguelpalacios on 01/02/16.
 */
public class AdminGeneralBluetooth {
    public static final String  DIRECCION_DISPOSITIVO = "DIRECCION";
    public static final String NOMBRE_DISPOSITIVO = "NOMBRE";
    public static final String TIPO_DISPOSITIVO = "TIPO";

    public static final int mMensajeEstadoCambiado = 1;
    public static final int mMensajeLeido = 2;
    public static final int mMensajeEscrito = 3;
    public static final int mMensajeNombreDispositivo = 4;
    public static final int mMensajeTOAST = 5;
    public static final int mMensajeDesconectado = 6;
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    public final static UUID UUID_HM_RX_TX = UUID.fromString(SampleGattAttributes.HM_RX_TX);
}
