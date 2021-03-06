package cf.coiltech.com.stok;
/**
 * Created by Hasan Aydemir 10.04.2018
 * */

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.vision.barcode.Barcode;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cf.coiltech.com.stok.app.AppController;
import cf.coiltech.com.stok.data.MySingleton;


public class SayimActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 100;
    public static final int PERMISSION_REQUEST = 200;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    String tag_json_obj = "json_obj_req";
    private static final String TAG = CikisYapActivity.class.getSimpleName();
    int success;
    ImageView urunResmi;
    Button veriListele,scanbtn,urunEkle,tarihButton;
     private EditText urunAra;
    TextView urunAdi, urunID, urunMarka,adetLbl,teslimTarihi;
    EditText urunAdet;
     private ProgressDialog pd;
    String urunAydi;
    String ServerURL = "http://192.168.2.22/hm/api/sayim/sayimEkle2.php" ;
    String TempUrunAdi, TempUrunAdet, TempUrunID,TempTeslimTarihi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sayim);
        //Create objects for database select and insert process
        urunMarka = (TextView) findViewById(R.id.urunMarka) ;
        urunAdi = (TextView) findViewById(R.id.urunAdi) ;
        urunID = (TextView) findViewById(R.id.urunID) ;
        urunAdet = (EditText) findViewById(R.id.urunAdet) ;
        urunEkle = (Button) findViewById(R.id.urunEkle);
        urunAra = (EditText) findViewById(R.id.urunAra) ;
        adetLbl = (TextView) findViewById(R.id.adetLbl) ;
        urunResmi= (ImageView) findViewById(R.id.urunResmi);

        pd = new ProgressDialog(SayimActivity.this);
        pd.setMessage("Yükleniyor...");
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);

        scanbtn = (Button) findViewById(R.id.scanbtn);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST);
        scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SayimActivity.this, ScanActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });



        //stock-take activity button action
        ImageButton anaSSayfaButton = (ImageButton) findViewById(R.id.anaSSayfaButton);

        //change activity by button
        anaSSayfaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SayimActivity.this, AnaSayfa.class);
                startActivityForResult(intent, RESULT_OK);
            }
        });

        //stock-take activity button action
        ImageButton cikisYYapButton = (ImageButton) findViewById(R.id.cikisYYapButton);

        //change activity by button
        cikisYYapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SayimActivity.this, CikisYapActivity.class);
                startActivityForResult(intent, RESULT_OK);
            }
        });

        //change activity for search activity

        ImageButton urunAraButton = (ImageButton) findViewById(R.id.urunAraButton);
        //change activity by button
        urunAraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SayimActivity.this, RemoteSearchActivity.class);
                startActivityForResult(intent, RESULT_OK);
            }
        });


        // DB filter textView watcher
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
         if(urunAdet.getText().toString().equals("") || teslimTarihi.getText().toString().equals("")     )
                {
                    Toast.makeText(SayimActivity.this,"Adet veya Tarih alanları boş bırakılamaz girmediniz!", Toast.LENGTH_LONG).show();
                }
               else {
                GetData();
                urunEkle();
                  //InsertData(TempUrunAdi, TempUrunAdet,TempUrunID,TempTeslimTarihi);

                }


            }
        });

        //listele button action
        Button veriListele = (Button) findViewById(R.id.veriListele);

        //change activity by floating button
        veriListele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SayimActivity.this,SayimLists.class);
                startActivityForResult(intent, RESULT_OK);
            }
        });

        //Add XML referans for Button and TextView
        tarihButton = (Button) findViewById(R.id.tarihButton);
        teslimTarihi = (TextView) findViewById(R.id.teslimTarihi);


        tarihButton.setOnClickListener(new View.OnClickListener() {//tarihButona Click Listener ekliyoruz

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int year = mcurrentTime.get(Calendar.YEAR);//current year
                int month = mcurrentTime.get(Calendar.MONTH);//current month
                int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);//current month
                 DatePickerDialog datePicker;//Datepicker object
                datePicker = new DatePickerDialog(SayimActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {

         int ay = monthOfYear+1;

          teslimTarihi.setText( dayOfMonth + "/" + ay + "/"+year);//Click Ayarla button to write EditTExt

                    }
                },year,month,day);//set values on starting
                datePicker.setTitle("Tarih Seçiniz");
                datePicker.setButton(DatePickerDialog.BUTTON_POSITIVE, "Seç", datePicker);
                datePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", datePicker);
                datePicker.show();

            }
        });


    }



// Add value to temporary variable
    public void GetData(){

        TempUrunAdi = urunAdi.getText().toString();
        TempUrunID = urunID.getText().toString();
        TempUrunAdet = urunAdet.getText().toString();
        TempTeslimTarihi = teslimTarihi.getText().toString();

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
                                String kucukResim = jsonobject.getString("kucuk_resim");
                                String imgURL ="http://192.168.2.22/hm/"+kucukResim;
                                urunAdi.setText(isim);
                                urunID.setText(id);
                                urunMarka.setText(brand);
                                Picasso.get().load("http://192.168.2.22/hm/"+kucukResim).into(urunResmi);
 if(urunAdi.toString()!="") {

     urunAdet.setVisibility(View.VISIBLE);
     adetLbl.setVisibility(View.VISIBLE);
     urunEkle.setVisibility(View.VISIBLE);
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

    // clear and hide inputs
    private void resetInputs(){
        urunMarka.setText("");
        urunAdi.setText("");
        urunID.setText("");
        urunAdet.setText("");
        urunAdet.setVisibility(View.INVISIBLE);
        adetLbl.setVisibility(View.INVISIBLE);
        urunEkle.setVisibility(View.INVISIBLE);
       urunResmi.setVisibility(View.INVISIBLE);
        urunAra.setText("");
    }

    //add input values to mysql table
    private void urunEkle() {
        String url = ServerURL;
        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check error getting json data
                    if (success == 1) {


                        AlertDialog.Builder builder = new AlertDialog.Builder(SayimActivity.this);

                        //Setting message manually and performing action on button click
                        builder.setMessage(jObj.getString(TAG_MESSAGE))
                                .setCancelable(false)
                                .setNegativeButton("Tamam", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //  Action for 'NO' Button
                                        dialog.cancel();
                                    }
                                });

                        //Creating dialog box
                        AlertDialog alert = builder.create();
                        //Setting the title manually
                        alert.setTitle("Hata!");
                        alert.show();

                    }

                    else if (success == 2) {
                        Log.d("Add", jObj.toString());

                        resetInputs();

                        Toast.makeText(SayimActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();


                    }
                    else {
                        Toast.makeText(SayimActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
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
                Toast.makeText(SayimActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to post url
                Map<String, String> params = new HashMap<String, String>();
                // change values depends on update or inster action

                params.put("urunID", urunID.getText().toString());
                params.put("urunAdet", urunAdet.getText().toString());
                params.put("teslimTarihi",teslimTarihi.getText().toString());
                params.put("urunAdi",urunAdi.getText().toString());


                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

}



