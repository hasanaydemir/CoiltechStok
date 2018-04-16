package cf.coiltech.com.stok;
/**
 * Created by Hasan Aydemir 10.04.2018
 * */
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;
import android.app.ProgressDialog;
 import android.webkit.WebViewClient;

 public class webBased extends AppCompatActivity   {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_based);


        // webView'i tasarımdakiyle bağlıyoruz.
        webView = (WebView) findViewById(R.id.webView1);

        // webView'i JavaScript kodlarını çalıştıracak şekilde set ediyoruz.
        webView.getSettings().setJavaScriptEnabled(true);

        // Sayfanın yüklendiğinin anlaşılması için ProgressDialog açıyoruz.
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Coiltech Stok",
                "Sayfa Yükleniyor...", true);

        webView.setWebViewClient(new WebViewClient() {

            // Sayfa Yüklenirken bir hata oluşursa kullanıcıyı uyarıyoruz.
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), "Sayfa Yüklenemedi!",
                        Toast.LENGTH_SHORT).show();
            }

            // Sayfanın yüklenme işlemi bittiğinde progressDialog'u kapatıyoruz.
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });

        //Web sayfamızın url'ini webView'e yüklüyoruz.
        webView.loadUrl("http://192.168.2.22/up/sayimci.html");

    }

    }

