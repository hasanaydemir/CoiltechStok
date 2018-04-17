package cf.coiltech.com.stok;
/**
 * Created by Hasan Aydemir 10.04.2018
 * */
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import cf.coiltech.com.stok.adapter.Adapter;
import cf.coiltech.com.stok.app.AppController;
import cf.coiltech.com.stok.data.Data;
import cf.coiltech.com.stok.util.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataLists extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    Toolbar toolbar;
    ListView list;
    SwipeRefreshLayout swipe;
    List<Data> itemList = new ArrayList<Data>();
    Adapter adapter;
    int success;
    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    View dialogView;
    EditText txt_id,  txt_urunAdet;
    TextView txt_urunAdi, txt_urunMarka;
    String id, urunAdi, urunAdet, urunMarka;
    Button barcodeGeriDon,veriAktar;
    private static final String TAG = DataLists.class.getSimpleName();

    private static String url_select 	 = Server.URL + "urunGeciciCek.php";
    private static String url_insert 	 = Server.URL + "urunEkle.php";
    private static String url_edit 	     = Server.URL + "urunDuzenle.php";
    private static String url_update 	 = Server.URL + "urunGuncelle.php";
    private static String url_delete 	 = Server.URL + "urunSil.php";
    private static String url_total_update 	 = Server.URL + "sayimAktar.php";

    public static final String TAG_ID       = "id";
    public static final String TAG_URUN_ADI = "model";
    public static final String TAG_MARKA = "marka";
    public static final String TAG_ADET = "urun_adet";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_lists);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // Swipe refresh listView value
        swipe   = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        list    = (ListView) findViewById(R.id.list);

        // JSON adapter
        adapter = new Adapter(DataLists.this, itemList);
        list.setAdapter(adapter);

        // Swipe to refresg action
        swipe.setOnRefreshListener(this);

        swipe.post(new Runnable() {
                       @Override
                       public void run() {
                           swipe.setRefreshing(true);
                           itemList.clear();
                           adapter.notifyDataSetChanged();
                           callVolley();
                       }
                   }
        );



        // create dialog menu when long pres to listview items
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view,
                                           final int position, long id) {
                // TODO Auto-generated method stub
                final String idx = itemList.get(position).getId();

                final CharSequence[] dialogitem = {"Düzenle", "Sil"};
                dialog = new AlertDialog.Builder(DataLists.this);
                dialog.setCancelable(true);
                dialog.setItems(dialogitem, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        switch (which) {
                            case 0:
                                edit(idx);
                                break;
                            case 1:
                                delete(idx);
                                break;
                        }
                    }
                }).show();
                return false;
            }
        });


        //listele button action
        Button barcodeGeriDon = (Button) findViewById(R.id.barcodeGeriDon);

        //change activity by button
        barcodeGeriDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(DataLists.this, MainActivity.class);
                startActivityForResult(intent, RESULT_OK); */
                //i use finish for turn back barcode read & DB filter activity
                finish();

            }
        });

        Button veriAktar = (Button) findViewById(R.id.veriAktar);

        //change activity by button
        veriAktar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stokAktar();

             }
        });



    }

    @Override
    public void onRefresh() {
        itemList.clear();
        adapter.notifyDataSetChanged();
        callVolley();
    }

    // sent edit text to form
    private void kosong(){
        txt_id.setText(null);
        txt_urunAdi.setText(null);
        txt_urunMarka.setText(null);
        txt_urunAdet.setText(null);
    }

    // get values to dialog form
    private void DialogForm(String idx, String urunadix, String urunmarkax, String urunadetx, String button) {
        dialog = new AlertDialog.Builder(DataLists.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_update, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Ürün Düzenle");

        txt_id      = (EditText) dialogView.findViewById(R.id.txt_id);
        txt_urunAdi = (TextView) dialogView.findViewById(R.id.txt_urunAdi);
        txt_urunMarka = (TextView) dialogView.findViewById(R.id.txt_urunMarka);
        txt_urunAdet = (EditText) dialogView.findViewById(R.id.txt_urunAdet);

        if (!idx.isEmpty()){
            txt_id.setText(idx);
            txt_urunAdi.setText(urunadix);
            txt_urunMarka.setText(urunmarkax);
            txt_urunAdet.setText(urunadetx);

        } else {
            kosong();
        }

        dialog.setPositiveButton(button, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                id      = txt_id.getText().toString();
                urunAdi    = txt_urunAdi.getText().toString();
                urunMarka  = txt_urunMarka.getText().toString();
                urunAdet = txt_urunAdet.getText().toString();

                simpan_update();
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("İptal", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                kosong();
            }
        });

        dialog.show();
    }

    // items to listview
    private void callVolley(){
        itemList.clear();
        adapter.notifyDataSetChanged();
        swipe.setRefreshing(true);

        // request JSON
        JsonArrayRequest jArr = new JsonArrayRequest(url_select, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());

                // Parsing json
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);

                        Data item = new Data();

                        item.setId(obj.getString(TAG_ID));
                        item.setUrunAdi(obj.getString(TAG_URUN_ADI));
                        item.setUrunMarka(obj.getString(TAG_MARKA));
                        item.setUrunAdet(obj.getString(TAG_ADET));
                        // item to array
                        itemList.add(item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // refresh listview when add data
                adapter.notifyDataSetChanged();

                swipe.setRefreshing(false);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                swipe.setRefreshing(false);
            }
        });

        // request to request queue
        AppController.getInstance().addToRequestQueue(jArr);
    }

    // fungsi untuk menyimpan atau update
    private void simpan_update() {
        String url;
        // inster or update action (i'm not use instert function this application)
        if (id.isEmpty()){
            url = url_insert;
        } else {
            url = url_update;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check error getting json data
                    if (success == 1) {
                        Log.d("Add/update", jObj.toString());

                        callVolley();
                        kosong();

                        Toast.makeText(DataLists.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(DataLists.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(DataLists.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to post url
                Map<String, String> params = new HashMap<String, String>();
                // change values depends on update or inster action
                if (id.isEmpty()){
                    params.put("urunAdet",urunAdet);
                    params.put("urunAdi",urunAdi);
                    params.put("urunMarka",urunMarka);


                } else {
                    params.put("id", id);
                    params.put("urunAdet",urunAdet);
                    params.put("urunAdi",urunAdi);
                    params.put("urunMarka",urunMarka);
                }

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    //  get edit data
    private void edit(final String idx){
        StringRequest strReq = new StringRequest(Request.Method.POST, url_edit, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // check error json value
                    if (success == 1) {
                        Log.d("get edit data", jObj.toString());
                        String idx      = jObj.getString(TAG_ID);
                        String urunadix    = jObj.getString(TAG_URUN_ADI);
                        String urunmarkax  = jObj.getString(TAG_MARKA);
                        String urunadetx  = jObj.getString(TAG_ADET);


                        DialogForm(idx, urunadix, urunmarkax, urunadetx,"Güncelle");

                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(DataLists.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(DataLists.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", idx);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    // delete listview and mysql value
    private void delete(final String idx){
        StringRequest strReq = new StringRequest(Request.Method.POST, url_delete, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // check error json values
                    if (success == 1) {
                        Log.d("delete", jObj.toString());

                        callVolley();

                        Toast.makeText(DataLists.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(DataLists.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(DataLists.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", idx);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }


    // delete listview and mysql value
    private void stokAktar(){
        StringRequest strReq = new StringRequest(Request.Method.POST, url_total_update, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // check error json values
                    if (success == 1) {

                        Toast.makeText(DataLists.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();


                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(DataLists.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(DataLists.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

}