package com.ad.miguelpalacios.bluetoothtool.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ad.miguelpalacios.bluetoothtool.R;

import java.util.ArrayList;

/**
 * Created by miguelpalacios on 09/02/16.
 */
public class AdapterChatTerminal extends BaseAdapter{
    private Context mContext;
    private ArrayList<String> mChat;
    private int mColorText;

    public AdapterChatTerminal(Context context){
        mContext = context;
        mChat = new ArrayList<String>();
    }

    public void addConversation(String mensaje){
        mChat.add(mensaje);
    }

    public void clear(){mChat.clear();}

    public void setColorText(int color){ mColorText = color;}

    @Override
    public int getCount() {return mChat.size();}

    @Override
    public Object getItem(int position) {return mChat.get(position);}

    @Override
    public long getItemId(int position) {return position;}

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view == null){
            LayoutInflater mInflator = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            view = mInflator.inflate(R.layout.nombre_dispositivo, null);
            viewHolder = new ViewHolder();
            viewHolder.mTextViewMensaje = (TextView)view.findViewById(R.id.textViewMensaje);
            viewHolder.mTextViewMensaje.setTextColor(mColorText);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.mTextViewMensaje.setText(mChat.get(i));
        return view;
    }

    static class ViewHolder {
        TextView mTextViewMensaje;
    }
}
