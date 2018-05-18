package cf.coiltech.com.stok;
/**
 * Created by Hasan Aydemir 10.04.2018
 * */

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cf.coiltech.com.stok.data.MySingleton;


public class AramaActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 100;
    public static final int PERMISSION_REQUEST = 200;

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    Button scanbtn;

    TextView urunID, urunMarka, adetLbl;
    EditText urunAdet, urunAra, urunAdi;
    ;
    ImageView urunResmi;
    private ProgressDialog pd;
    String urunAydi;
    String urunIsmi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arama);
        //Create objects for database select and insert process
        urunMarka = (TextView) findViewById(R.id.urunMarka);
        urunAdi = (EditText) findViewById(R.id.urunAdi);
        urunID = (TextView) findViewById(R.id.urunID);
        urunAdet = (EditText) findViewById(R.id.urunAdet);
        urunAra = (EditText) findViewById(R.id.urunAra);
        adetLbl = (TextView) findViewById(R.id.adetLbl);
        urunResmi = (ImageView) findViewById(R.id.urunResmi);
        pd = new ProgressDialog(AramaActivity.this);
        pd.setMessage("Yükleniyor...");
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);

        scanbtn = (Button) findViewById(R.id.scanbtn);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST);
        scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AramaActivity.this, ScanActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });


        Button araButton = (Button) findViewById(R.id.araButton);

        araButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urunIsmi = urunAdi.getText().toString().trim();

                getSqlDetails();
            }
        });


        //Go home button action
        ImageButton anaSayfaButton = (ImageButton) findViewById(R.id.anaSayfaButton);

        //change activity by button
        anaSayfaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AramaActivity.this, AnaSayfa.class);
                startActivityForResult(intent, RESULT_OK);
            }
        });

        //stock-take activity button action
        ImageButton sayimYapButton = (ImageButton) findViewById(R.id.sayimYapButton);

        //change activity by button
        sayimYapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AramaActivity.this, SayimActivity.class);
                startActivityForResult(intent, RESULT_OK);
            }
        });

/*
        // DB filter textView watcher
        urunAdi.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 2) {
                    urunIsmi = urunAdi.getText().toString().trim();

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
*/

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

    // DB filter by product name value
    private void getSqlDetails() {

        String url = "http://192.168.2.22/hm/api/urunIsimArar.php?searchQuery=" + urunIsmi;
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        pd.hide();


                        try {

                            JSONArray jsonarray = new JSONArray(response);

                            for (int i = 0; i < jsonarray.length(); i++) {

                                JSONObject jsonobject = jsonarray.getJSONObject(i);
                                String id = jsonobject.getString("id");
                                String brand = jsonobject.getString("marka");
                                String isim = jsonobject.getString("model");
                                String kucukResim = jsonobject.getString("kucuk_resim");
                                String imgURL = "http://192.168.2.22/hm/" + kucukResim;
                                urunAdi.setText(isim);
                                urunID.setText(id);
                                urunMarka.setText(brand);
                                Picasso.get().load("http://192.168.2.22/hm/" + kucukResim).into(urunResmi);

                                //new DownLoadImageTask(urunResmi).execute(imgURL);
                                if (urunAdi.toString() != "") {
                                    urunAdet.setVisibility(View.VISIBLE);
                                    adetLbl.setVisibility(View.VISIBLE);
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
                        if (error != null) {

                            Toast.makeText(getApplicationContext(), "Veri tabanına bağlanılamadı", Toast.LENGTH_LONG).show();
                        }
                    }
                }

        );

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}




