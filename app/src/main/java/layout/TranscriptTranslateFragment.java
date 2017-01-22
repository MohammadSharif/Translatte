package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import mohammadsharif.com.spiik.R;
import mohammadsharif.com.spiik.TranscriptActivity;

import android.speech.tts.TextToSpeech;


public class TranscriptTranslateFragment extends Fragment implements View.OnClickListener, TextToSpeech.OnInitListener {
    private ImageButton transcript_translate_cancel_button, transcript_play_button;
    private TextView transcript_translated_text;
    private JSONObject translatedJSON;
    private Locale translatedLocale;
    private TextToSpeech tts;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transcript_translate, container, false);

        transcript_translated_text = (TextView) view.findViewById(R.id.transcript_translated_text);
        transcript_translate_cancel_button = (ImageButton) view.findViewById(R.id.transcript_translate_cancel_button);
        transcript_translate_cancel_button.setOnClickListener(this);
        transcript_play_button = (ImageButton) view.findViewById(R.id.transcript_play_button);
        transcript_play_button.setOnClickListener(this);

        tts = new TextToSpeech(this.getActivity().getBaseContext(), this);
        return view;
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                transcript_play_button.setEnabled(true);
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.transcript_translate_cancel_button:
                ((TranscriptActivity)getActivity()).killTranslateFragment();
                break;
            case R.id.transcript_play_button:
                this.speakOutTranslatedLang(transcript_translated_text.getText().toString());
                break;
        }
    }

    public void setTranslatedJSON(String tJSON){
        //display on text view
        transcript_translated_text.setText(tJSON);
    }

    public void setTranslatedLocale(Locale locale) {
        this.translatedLocale = locale;
    }

    public Locale getTranslatedLocale() {
        return this.translatedLocale;
    }

    private void speakOutTranslatedLang(String translatedText) {
        tts.setLanguage(this.getTranslatedLocale());
        Log.i("speaking", translatedText);
        tts.speak(translatedText, TextToSpeech.QUEUE_FLUSH, null, null);
    }
}
