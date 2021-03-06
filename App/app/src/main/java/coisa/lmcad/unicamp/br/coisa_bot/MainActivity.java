package coisa.lmcad.unicamp.br.coisa_bot;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressBar;

    @android.webkit.JavascriptInterface
    public void onData(String value) {
        if (CoisaBluetooth.getInstance() != null && CoisaBluetooth.getInstance().isConnected()) ThingMonitor.instance.sendBinary(value);
        else CoisaBluetooth.getInstance().connect();
    }

    @android.webkit.JavascriptInterface
    public void onReset() {
        if (CoisaBluetooth.getInstance() != null && CoisaBluetooth.getInstance().isConnected()) ThingMonitor.instance.reset();
        else CoisaBluetooth.getInstance().connect();
    }

    public void makeToast(String text) {
        final String message = text;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CoisaBluetooth.getInstance().setMain(this);
        ThingMonitor.instance.setMain(this);

        final android.webkit.WebView webview = (android.webkit.WebView) findViewById(R.id.webView);
//        webview.clearCache(true);
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        webview.addJavascriptInterface(this, "android");
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setUseWideViewPort(true);
//        settings.setPluginState(PluginState.ON);
//        settings.setJavaScriptEnabled(true);
//        settings.setSupportZoom(true);
//        settings.setBuiltInZoomControls(true);
        webview.setScrollBarStyle(android.webkit.WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        webview.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage cm) {
                onData(cm.message()); //Way of bypassing the bug on javascript interface happening on the galaxy tab
                Log.d("MyApplication", cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId());
                return true;
            }
        });

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        progressBar = ProgressDialog.show(MainActivity.this, "WebView Example", "Loading...");

        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
                Log.i("WebView", "Processing webview url click...");
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(android.webkit.WebView view, String url) {
                Log.i("WebView", "Finished loading URL: " + url);
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }

            public void onReceivedError(android.webkit.WebView view, int errorCode, String description, String failingUrl) {
                Log.e("WebView", "Error: " + description);
                Toast.makeText(MainActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
                alertDialog.setTitle("Error");
                alertDialog.setMessage(description);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                alertDialog.show();
            }
        });

        webview.loadUrl("http://cmillani.github.io/blockly/android.html");
    }
}
