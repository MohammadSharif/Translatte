package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
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
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import mohammadsharif.com.spiik.R;
import mohammadsharif.com.spiik.TranscriptActivity;

public class TranscriptMainFragment extends Fragment implements View.OnClickListener, TextToSpeech.OnInitListener {
    private Button transcript_translate, transcript_play_button;
    private TextToSpeech tts;
    private Spinner spinner;
    private HashMap<String, Locale> localeMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transcript_main, container, false);

        transcript_translate = (Button) view.findViewById(R.id.transcript_translate_button);
        transcript_translate.setOnClickListener(this);

        tts = new TextToSpeech(this.getActivity().getBaseContext(), this);
        spinner = (Spinner) view.findViewById(R.id.spinner);

        //Check if connected to the internet

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.transcript_translate_button:
                ((TranscriptActivity)getActivity()).addTranslateFragment();
                break;
            case R.id.transcript_play_button:
                speakOut();
                break;
        }
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
                transcript_translate.setEnabled(true);
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }

        Set<Locale> langs = tts.getAvailableLanguages();
        List<Locale> list = new ArrayList<Locale>(langs);
        List<String> displayLangs = new ArrayList<String>();
        localeMap = new HashMap<>();
        for(int i = 0; i < list.size(); i++) {
            String dispLang = list.get(i).getDisplayLanguage();
            localeMap.put(dispLang, list.get(i));
            if(!displayLangs.contains(dispLang)) {
                displayLangs.add(dispLang);
            }
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this.getActivity().getBaseContext(), android.R.layout.simple_spinner_item, displayLangs);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    private void speakOut() {
        String locLang = spinner.getSelectedItem().toString();
        Locale loc = localeMap.get(locLang);
        Log.v(locLang, "test");
        tts.setLanguage(loc);
        String text = "hello my name is arjun";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }
}
