package mohammadsharif.com.spiik;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import layout.TranscriptMainFragment;
import layout.TranscriptTranslateFragment;

public class TranscriptActivity extends FragmentActivity  {
    private static final String TAG = "TranscriptActivity: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcript);

        // Get Read Text, get transcript language from OCR Bundle
        Bundle transcriptBundle = getIntent().getExtras();
        ArrayList ocrOutput = (ArrayList) transcriptBundle.get("readText");
        String transcriptLanguage = (String) transcriptBundle.get("language");
        Log.v(TAG, transcriptLanguage);

        ((TextView) findViewById(R.id.transcript_original_text)).setText(ocrOutput.toString());



    }

    // Show Transcript Fragment
    public void addTranscriptFragment(){
        if(getFragmentManager().findFragmentByTag(getString(R.string.fragment_transcript_main)) == null){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            TranscriptMainFragment translateFragment = new TranscriptMainFragment();
            fragmentTransaction.add(R.id.activity_transcript, translateFragment, getString(R.string.fragment_transcript_main));
            fragmentTransaction.commit();

        }
    }

    // Show translate Fragment
    public void addTranslateFragment(){
        if(getFragmentManager().findFragmentByTag(getString(R.string.fragment_transcript_translate)) == null){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                    .setCustomAnimations( R.animator.exit_from_bottom, R.animator.enter_from_bottom);
            TranscriptTranslateFragment translateFragment = new TranscriptTranslateFragment();
            fragmentTransaction.add(R.id.activity_transcript, translateFragment, getString(R.string.fragment_transcript_translate));
            fragmentTransaction.commit();
        }
    }

    // KILL translate fragment
    public void killTranslateFragment() {
        if (getFragmentManager().findFragmentByTag(getString(R.string.fragment_transcript_translate)) != null) {
            getFragmentManager().beginTransaction()
                   .remove(getFragmentManager().findFragmentByTag(getString(R.string.fragment_transcript_translate))).commit();
        }

    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    public void passTranslatedJSON(String text) {
        ((TextView) findViewById(R.id.transcript_translated_text)).setText(text);
    }
}
