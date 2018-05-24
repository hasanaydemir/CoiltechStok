package cf.coiltech.com.stok.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cf.coiltech.com.stok.R;
import cf.coiltech.com.stok.data.Data;

import java.util.List;

/**
 * Created by Hasan Aydemir 10.04.2018
 * */
public class Adapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Data> items;

    public Adapter(Activity activity, List<Data> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int location) {
        return items.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        TextView id = (TextView) convertView.findViewById(R.id.id);
        TextView urunAdi = (TextView) convertView.findViewById(R.id.urunAdi);
        TextView urunMarka = (TextView) convertView.findViewById(R.id.urunMarka);
        TextView urunAdet = (TextView) convertView.findViewById(R.id.urunAdet);

        Data data = items.get(position);

        id.setText(data.getId());
        urunAdi.setText(data.getUrunAdi());
        urunMarka.setText(data.getUrunMarka());
        urunAdet.setText(data.getUrunAdet() + " Adet");

        return convertView;
    }

}