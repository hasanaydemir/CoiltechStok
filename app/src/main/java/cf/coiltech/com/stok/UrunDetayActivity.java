package cf.coiltech.com.stok;
/**
 * Created by Hasan Aydemir 10.04.2018
 * */

import android.app.ProgressDialog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import java.util.ArrayList;

import java.util.List;


import cf.coiltech.com.stok.data.MySingleton;


public class UrunDetayActivity extends AppCompatActivity {

    // LogCat tag
    private static final String TAG = UrunDetayActivity.class.getSimpleName();

    public static final int REQUEST_CODE = 100;
    public static final int PERMISSION_REQUEST = 200;

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;


    Button urunEkle;
    private Button btnCapturePicture;
    private EditText urunAra;
    TextView urunAdi, urunID, urunMarka,adetLbl;
    EditText urunAdet;
    ImageView urunResmi;
    private ProgressDialog pd;
    String urunAydi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urun_detay);
        //Create objects for database select and insert process
        urunMarka = (TextView) findViewById(R.id.urunMarka) ;
        urunAdi = (TextView) findViewById(R.id.urunAdi) ;
        urunID = (TextView) findViewById(R.id.urunID) ;
         urunAra = (EditText) findViewById(R.id.urunAra) ;
        adetLbl = (TextView) findViewById(R.id.adetLbl) ;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnCapturePicture = (Button) findViewById(R.id.btnCapturePicture);

/**
 * Capture image button click event
 */
        btnCapturePicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PhotoUpload.class);
                intent.putExtra("urunID", urunAra.getText().toString());
                startActivity(intent);


            }
        });


        urunResmi= (ImageView) findViewById(R.id.urunResmi);
        pd = new ProgressDialog(UrunDetayActivity.this);
        //Loading message
        pd.setMessage("Yükleniyor...");
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);




        // DB filter textView watcher
        urunAra.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
/*
                if(s.length()>2) {
                    urunAydi = urunAra.getText().toString().trim();
                    getSqlDetails();
                }
*/
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });



        urunAydi = getIntent().getExtras().getString("urunID").trim();

        urunAra.setText(urunAydi);
        getSqlDetails();

    }










    // DB filter by QR Code value
    private void getSqlDetails() {

        String url= String.format("http://192.168.2.22/hm/api/urunArar.php?searchQuery="+urunAydi);
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        pd.hide();


                        try {

                            JSONArray jsonarray = new JSONArray(response);

                            for(int i=0; i < jsonarray.length(); i++) {

                                JSONObject jsonobject = jsonarray.getJSONObject(i);


                                String id = jsonobject.getString("id");
                                String brand = jsonobject.getString("marka");
                                String isim = jsonobject.getString("model");
                                String kucukResim = jsonobject.getString("kucuk_resim");
                                String imgURL ="http://192.168.2.22/hm/"+kucukResim;
                                urunAdi.setText(isim);
                                urunID.setText(id);
                                urunMarka.setText(brand);
                                Picasso.get().load("http://192.168.2.22/hm/"+kucukResim).into(urunResmi);

                                //new DownLoadImageTask(urunResmi).execute(imgURL);

 if(urunAdi.toString()!="") {

      adetLbl.setVisibility(View.VISIBLE);
     btnCapturePicture.setVisibility(View.VISIBLE);
     urunResmi.setVisibility(View.VISIBLE);
 }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();


                        }




                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error != null){

                            Toast.makeText(getApplicationContext(), "Veri tabanına bağlanılamadı", Toast.LENGTH_LONG).show();
                        }
                    }
                }

        );

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



}






