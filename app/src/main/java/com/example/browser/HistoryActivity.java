package com.example.browser;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import static com.example.browser.MainActivity.dbhandler;
import static com.example.browser.MainActivity.historyArrayList;
import static com.example.browser.MainActivity.webView;

public class HistoryActivity extends AppCompatActivity {
    ListView historyListview;
    public static HistoryArrayAdapter adapter;
    public EditText searchHistoryEditView;
    public ImageView deleteFullHistory;
    Boolean clickOnListview=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        historyListview = findViewById(R.id.historyListView);
        adapter = new HistoryArrayAdapter(this, R.layout.list_element, historyArrayList);
        historyListview.setAdapter(adapter);
        historyListview.setTextFilterEnabled(true);
        searchHistoryEditView = findViewById(R.id.searchEditView);
        deleteFullHistory = findViewById(R.id.deleteFullHistory);
        deleteFullHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbhandler.deleteAllHistory();
                historyArrayList.clear();
                adapter.updateInternalData();
                adapter.notifyDataSetChanged();
                webView.clearCache(true);
                webView.clearHistory();
                webView.clearFormData();
                webView.clearSslPreferences();
                webView.clearFocus();
                webView.clearMatches();
            }
        });
        searchHistoryEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence.toString());
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        View.OnClickListener editTextClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                if (v.getId() == searchHistoryEditView.getId()) {
                    searchHistoryEditView.setCursorVisible(true);
                }
            }
        };
        searchHistoryEditView.setOnClickListener(editTextClickListener);
        searchHistoryEditView.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                searchHistoryEditView.setCursorVisible(false);
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(searchHistoryEditView.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
        historyListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent returnIntent=new Intent();
                returnIntent.putExtra("urlPosition",position);
                setResult(Activity.RESULT_OK,returnIntent);
                clickOnListview=true;
                finish();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(clickOnListview==false) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("urlPosition", -1);
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
    }


}

