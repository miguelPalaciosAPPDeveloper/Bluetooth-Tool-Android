package com.ad.miguelpalacios.bluetoothtool.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ad.miguelpalacios.bluetoothtool.R;

/**
 * Created by miguelpalacios on 14/05/16.
 */
public class DialogAyuda implements View.OnClickListener{
    private static final String TAG = DialogAyuda.class.getSimpleName();
    private Context mContext;
    private Dialog mDialogAyuda;
    private TextView mTextViewDescripcion;
    private ImageView mImageViewVista;
    private String[] mDescripcion;
    private Drawable[] mImagen;
    private int mPagina = 0;

    public DialogAyuda(Context context){mContext = context;}

    public Dialog CrearDialog(String titulo, String[] descripcion, Drawable[] imagen){
        mDialogAyuda = new Dialog(mContext, R.style.Theme_Dialog_Translucent);
        mDialogAyuda.show();
        mDialogAyuda.setCancelable(false);
        mDialogAyuda.setContentView(R.layout.dialog_ayuda);

        mDescripcion = descripcion;
        mImagen = imagen;

        TextView textViewTitulo = (TextView)mDialogAyuda.findViewById(R.id.textViewTitulo);
        textViewTitulo.setText(titulo);

        mTextViewDescripcion = (TextView)mDialogAyuda.findViewById(R.id.textViewDescripcion);
        mTextViewDescripcion.setText(mDescripcion[mPagina]);

        mImageViewVista = (ImageView)mDialogAyuda.findViewById(R.id.imageViewVista);
        mImageViewVista.setImageDrawable(mImagen[mPagina]);

        Button buttonCerrar = (Button)mDialogAyuda.findViewById(R.id.buttonCerrar);
        buttonCerrar.setOnClickListener(this);

        ImageButton imageButtonAdelante = (ImageButton)mDialogAyuda.findViewById(R.id.imageButtonAdelante);
        imageButtonAdelante.setOnClickListener(this);

        ImageButton imageButtonAtras = (ImageButton)mDialogAyuda.findViewById(R.id.imageButtonAtras);
        imageButtonAtras.setOnClickListener(this);

        return mDialogAyuda;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.imageButtonAdelante:
                mPagina++;
                if(mPagina < mDescripcion.length){
                    mTextViewDescripcion.setText(mDescripcion[mPagina]);
                    mImageViewVista.setImageDrawable(mImagen[mPagina]);
                } else{
                    mPagina = 0;
                    mTextViewDescripcion.setText(mDescripcion[mPagina]);
                    mImageViewVista.setImageDrawable(mImagen[mPagina]);
                }
                break;
            case R.id.imageButtonAtras:
                mPagina--;
                if(mPagina >= 0){
                    mTextViewDescripcion.setText(mDescripcion[mPagina]);
                    mImageViewVista.setImageDrawable(mImagen[mPagina]);
                } else{
                    mPagina = mDescripcion.length -1;
                    mTextViewDescripcion.setText(mDescripcion[mPagina]);
                    mImageViewVista.setImageDrawable(mImagen[mPagina]);
                }
                break;
            case R.id.buttonCerrar:
                mDialogAyuda.dismiss();
                break;
        }
    }
}
