package cf.coiltech.com.stok;
/**
 * Created by Hasan Aydemir 10.04.2018
 * */
import android.Manifest;
import android.app.ProgressDialog;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cf.coiltech.com.stok.data.MySingleton;


public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 100;
    public static final int PERMISSION_REQUEST = 200;

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    Button veriListele;
    Button scanbtn;
    private EditText urunAra;
    TextView urunAdi, urunID, urunMarka,adetLbl;
    EditText urunAdet;
    Button urunEkle;
    private ProgressDialog pd;
    String urunAydi;
    String ServerURL = "http://192.168.2.22/hm/api/urunEkle.php" ;
    String TempUrunAdi, TempUrunAdet, TempUrunID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Create objects for database select and insert process
        urunMarka = (TextView) findViewById(R.id.urunMarka) ;
        urunAdi = (TextView) findViewById(R.id.urunAdi) ;
        urunID = (TextView) findViewById(R.id.urunID) ;
        urunAdet = (EditText) findViewById(R.id.urunAdet) ;
        urunEkle = (Button) findViewById(R.id.urunEkle);
        urunAra = (EditText) findViewById(R.id.urunAra) ;
        adetLbl = (TextView) findViewById(R.id.adetLbl) ;

        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Yükleniyor...");
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);

        scanbtn = (Button) findViewById(R.id.scanbtn);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST);
        scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });


        urunAra.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                if(s.length() > 2) {
                    urunAydi = urunAra.getText().toString().trim();

                    getSqlDetails();
                }
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

        urunEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GetData();
                InsertData(TempUrunAdi, TempUrunAdet,TempUrunID);

            }
        });


        /*
        //Floating button action
        FloatingActionButton kayarButon = (FloatingActionButton) findViewById(R.id.fab);

        //change activity by floating button
        kayarButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DataLists.class);
                startActivityForResult(intent, RESULT_OK);
            }
        });
*/


        //listele button action
        Button veriListele = (Button) findViewById(R.id.veriListele);

        //change activity by floating button
        veriListele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DataLists.class);
                startActivityForResult(intent, RESULT_OK);
            }
        });

    }

// Add value to temporary variable
    public void GetData(){

        TempUrunAdi = urunAdi.getText().toString();
        TempUrunID = urunID.getText().toString();
        TempUrunAdet = urunAdet.getText().toString();

    }

//QR code value setText filter TextView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                final Barcode barcode = data.getParcelableExtra("barcode");
                urunAra.post(new Runnable() {
                    @Override
                    public void run() {
                        urunAra.setText(barcode.displayValue);

                    }
                });
            }
        }
    }

    // DB filter by QR Code value
    private void getSqlDetails() {

        String url= "http://192.168.2.22/hm/api/urunArar.php?searchQuery="+urunAydi;
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
                                urunAdi.setText(isim);
                                urunID.setText(id);
                                urunMarka.setText(brand);
 if(urunAdi.toString()!="") {

     urunAdet.setVisibility(View.VISIBLE);
     adetLbl.setVisibility(View.VISIBLE);
     urunEkle.setVisibility(View.VISIBLE);

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

// insert TextView and EditText values to database
    public void InsertData(final String turunAdi, final String turunAdet, final String turunID){

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                String UrunAdiHolder = turunAdi ;
                String UrunAdetHolder = turunAdet ;
                String UrunIDHolder =  turunID;

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("urunAdi", UrunAdiHolder));
                nameValuePairs.add(new BasicNameValuePair("urunID", UrunIDHolder));
                nameValuePairs.add(new BasicNameValuePair("urunAdet", UrunAdetHolder));


                try {
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpPost httpPost = new HttpPost(ServerURL);

                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));

                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    HttpEntity httpEntity = httpResponse.getEntity();


                } catch (ClientProtocolException e) {

                } catch (IOException e) {

                }
                return "Data Inserted Successfully";
            }

            @Override
            protected void onPostExecute(String result) {

                super.onPostExecute(result);
                urunMarka.setText("");
                urunAdi.setText("");
                urunID.setText("");
                urunAdet.setText("");
                urunAdet.setVisibility(View.INVISIBLE);
                adetLbl.setVisibility(View.INVISIBLE);
                urunEkle.setVisibility(View.INVISIBLE);
                urunAra.setText("");
                Toast.makeText(MainActivity.this, "Ürün başarıyla listeye eklendi", Toast.LENGTH_LONG).show();

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();

        sendPostReqAsyncTask.execute(turunAdi,turunAdet,turunID);
    }




}



