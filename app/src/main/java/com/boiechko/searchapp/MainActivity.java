package com.boiechko.searchapp;

import android.Manifest.permission;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.boiechko.searchappsample.R;

public class MainActivity extends AppCompatActivity implements MaterialSearchView.SearchViewSearchListener, MaterialSearchView.SearchViewVoiceListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private LinearLayout mRootLayout;
    private MaterialSearchView mMaterialSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRootLayout = findViewById(R.id.root_view);
        mMaterialSearchView = findViewById(R.id.material_search_view);
        Button clearSearchHistory = findViewById(R.id.clear_search_history_button);
        clearSearchHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMaterialSearchView.clearSearchHistory();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMaterialSearchView.addListener((MaterialSearchView.SearchViewSearchListener) this);
        mMaterialSearchView.addListener((MaterialSearchView.SearchViewVoiceListener) this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (VERSION.SDK_INT >= VERSION_CODES.N) {
            mMaterialSearchView.removeListener((MaterialSearchView.SearchViewSearchListener) this);
            mMaterialSearchView.removeListener((MaterialSearchView.SearchViewVoiceListener) this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (VERSION.SDK_INT < VERSION_CODES.N) {
            mMaterialSearchView.removeListener((MaterialSearchView.SearchViewSearchListener) this);
            mMaterialSearchView.removeListener((MaterialSearchView.SearchViewVoiceListener) this);
        }
    }

    @Override
    public void onBackPressed() {
        if (!mMaterialSearchView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mMaterialSearchView.handlePermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onSearch(@NonNull String searchTerm) {
        Log.d(TAG, "SEARCH TERM: " + searchTerm);
        Snackbar.make(mRootLayout, searchTerm, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onVoiceSearch(@NonNull String searchTerm) {
        Log.d(TAG, "VOICE SEARCH TERM: " + searchTerm);
        Snackbar.make(mRootLayout, searchTerm, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onVoiceSearchError(int error) {
        Log.d(TAG, "VOICE_SEARCH_ERROR: " + error);
        switch (error) {
            case VOICE_SEARCH_ERROR_PERMISSION_NEEDED:
                if (VERSION.SDK_INT >= VERSION_CODES.M) {
                    requestPermissions(new String[]{permission.RECORD_AUDIO},
                            MaterialSearchView.REQUEST_CODE_PERMISSION_RECORD_AUDIO);
                }
                break;
            case VOICE_SEARCH_ERROR_MISSING_CONTEXT:
                Log.d(TAG, "VOICE_SEARCH_ERROR: Missing Context");
                break;
            case VOICE_SEARCH_ERROR_UNAVAILABLE:
                AlertDialog.Builder builder = new Builder(this);
                builder.setTitle(R.string.voice_search_unavailable_title);
                builder.setMessage(R.string.voice_search_unavailable_message);
                builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
                break;
        }
    }
}
