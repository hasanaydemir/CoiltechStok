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


        // create webview object from layout
        webView = (WebView) findViewById(R.id.webView1);

        // webView'i JavaScript kodlarını çalıştıracak şekilde set ediyoruz.
        webView.getSettings().setJavaScriptEnabled(true);

        // create processdialog about page loading process
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Coiltech Stok",
                "Sayfa Yükleniyor...", true);

        webView.setWebViewClient(new WebViewClient() {

            // Check error when loading page
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), "Sayfa Yüklenemedi!",
                        Toast.LENGTH_SHORT).show();
            }

            // close process dialog when open page without error
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });

        //load URL in webview object
        webView.loadUrl("http://192.168.2.22/up/sayimci.html");

    }

    }

