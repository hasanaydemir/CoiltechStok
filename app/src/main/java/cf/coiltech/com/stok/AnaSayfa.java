package cf.coiltech.com.stok;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class AnaSayfa extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana_sayfa);


        //stock-take activity button action
        ImageButton cikisYapButton = (ImageButton) findViewById(R.id.cikisYapButton);

        //change activity by button
        cikisYapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnaSayfa.this, CikisYapActivity.class);
                startActivityForResult(intent, RESULT_OK);
            }
        });

        //stock-take activity button action
        ImageButton sayimYYYapButton = (ImageButton) findViewById(R.id.sayimYYYapButton);

        //change activity by button
        sayimYYYapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnaSayfa.this, SayimActivity.class);
                startActivityForResult(intent, RESULT_OK);
            }
        });


        //change activity for search activity

        ImageButton urunAraButton = (ImageButton) findViewById(R.id.urunAraButton);
        //change activity by button
        urunAraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnaSayfa.this, RemoteSearchActivity.class);
                startActivityForResult(intent, RESULT_OK);
            }
        });



    }
}
