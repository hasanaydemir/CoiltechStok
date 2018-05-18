package cf.coiltech.com.stok;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView urunIDno =(TextView) findViewById(R.id.urunIDno);

        String urunID = getIntent().getStringExtra("urunID");

        urunIDno.setText(urunID);
    }
}
