package com.example.browser;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class MainActivity extends AppCompatActivity {
    EditText searchEdit;
    public static WebView webView;
    Button searchButton;
    LinearLayout homePage;
    Boolean backPress = false;
    List<WebView> allTabs;
    ArrayList<String> searchEngines;
    int currentSearchEngine;
    Boolean fullscreen = false;
    ProgressBar loadProgressBar;
    public static DBhandler dbhandler;
    public static ArrayList<History> historyArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_main);
        searchEngines = new ArrayList<>();
        searchEngines.add(getString(R.string.Google));
        searchEngines.add(getString(R.string.DDGo));
        searchEngines.add(getString(R.string.Bing));
        currentSearchEngine = 0;
        loadProgressBar = findViewById(R.id.loadProgressBar);
        homePage = findViewById(R.id.homePage);
        searchEdit = findViewById(R.id.searchEditView);
        webView = findViewById(R.id.webView);
        dbhandler = new DBhandler(this);
        historyArrayList = new ArrayList<History>();
        historyArrayList = dbhandler.getAllHistory();
        Collections.reverse(historyArrayList);
        searchButton = findViewById(R.id.searchbutton);
        searchButton.setText("Google");
        loadProgressBar.setMax(100);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        setWebViewAttributes();

        webView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view,int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                LinearLayout footer = findViewById(R.id.footer);
                LinearLayout header = findViewById(R.id.header);
                if (fullscreen == false && scrollY-oldScrollY>50) {
                    footer.setVisibility(View.GONE);
                    header.setVisibility(View.GONE);
                    fullscreen = true;
                }
                if(fullscreen == true && scrollY-oldScrollY<-100){
                    header.setVisibility(View.VISIBLE);
                    footer.setVisibility(View.VISIBLE);
                    fullscreen = false;
                }
            }
        });
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDescription, String mimeType, long l) {
                DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(url));
                downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                downloadManager.enqueue(downloadRequest);
                Toast.makeText(getApplicationContext(),"Downloading ",Toast.LENGTH_SHORT).show();
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(searchEdit.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                String url = searchEdit.getText().toString();
                searchEdit.clearFocus();
                searchOrUrl(url);
            }
        });
        searchButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popup = new PopupMenu(MainActivity.this, searchButton);
                popup.getMenuInflater()
                        .inflate(R.menu.menu_searchengines, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        searchButton.setText(item.getTitle());
                        currentSearchEngine = Integer.parseInt(item.getTitleCondensed().toString());
                        return true;
                    }
                });
                popup.show();
                return true;
            }
        });
        searchEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEdit.selectAll();
            }
        });
        searchEdit.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                searchEdit.setCursorVisible(false);
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(searchEdit.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    String url = searchEdit.getText().toString();
                    searchEdit.clearFocus();
                    searchOrUrl(url);
                }
                return false;
            }
        });
    }

    private void setWebViewAttributes() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                searchEdit.setText(url);

            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    loadProgressBar.setVisibility(View.GONE);
                    if(webView.getVisibility()!=View.GONE){
                        webView.setVisibility(View.VISIBLE);
                        homePage.setVisibility(View.GONE);
                    }
                } else {
                    loadProgressBar.setVisibility(View.VISIBLE);
                }
                loadProgressBar.setProgress(newProgress);
            }
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                String url = webView.getUrl();
                homePage.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                History history = new History(title, url);
                historyArrayList.add(0, history);
                dbhandler.addHistory(history);
            }
            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }
        });
    }

    @Override
    public void onBackPressed() {
        goBack(null);
    }

    public void goBack(View view) {
        if (webView.canGoBack()) {
            webView.goBack();
            backPress = false;
        } else {
            if (backPress == true) {
                finish();
            }
            else {
                if(fullscreen==true) {
                    LinearLayout footer = findViewById(R.id.footer);
                    LinearLayout header = findViewById(R.id.header);
                    header.setVisibility(View.VISIBLE);
                    footer.setVisibility(View.VISIBLE);
                    searchEdit.setText("");
                    fullscreen = false;
                }
                backPress = true;
                Toast toast = Toast.makeText(getApplicationContext(), "Press Again to Exit", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public void goForward(View view) {
        backPress = false;
        if (webView.canGoForward()) {
            webView.goForward();
        }
    }

    public static boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void searchOrUrl(String localURL) {
        if (!isValidURL(localURL)) {
            localURL.replace(" ", "+");
            localURL = searchEngines.get(currentSearchEngine).concat(localURL);
        }
        backPress = false;
        searchEdit.setText(localURL);
        webView.loadUrl(localURL);
    }
    public void refreshTab(View view) {
        String refreshUrl = searchEdit.getText().toString();
        if (!refreshUrl.equalsIgnoreCase("")) {
            searchOrUrl(refreshUrl);
        }
    }
    public void resetTab(View view) {
        Toast toast = Toast.makeText(getApplicationContext(), "New Tab Started", Toast.LENGTH_SHORT);
        webView.clearCache(true);
        webView.clearHistory();
        webView.clearFormData();
        webView.clearSslPreferences();
        webView.clearFocus();
        webView.clearMatches();
        webView.setVisibility(View.GONE);
        homePage.setVisibility(View.VISIBLE);
        searchEdit.setText("");
        //setWebViewAttributes();
        if(fullscreen==true) {
            fullscreen = false;
            LinearLayout footer = findViewById(R.id.footer);
            LinearLayout header = findViewById(R.id.header);
            header.setVisibility(View.VISIBLE);
            footer.setVisibility(View.VISIBLE);
        }
        toast.show();
    }
    public void openHistory(View view) {
        Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
        startActivityForResult(intent, 2);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 2:
                    if (resultCode == RESULT_OK) {
                        try {
                            Integer value = data.getIntExtra("urlPosition",0);
                            String urlLocal = historyArrayList.get(value).getHistoryURL();
                            searchEdit.setText(urlLocal);
                            searchOrUrl(urlLocal);
                        }
                        catch (Exception e){
                            Toast.makeText(this, "Error Occurred.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    }

}
