package layout;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import mohammadsharif.com.spiik.R;
import mohammadsharif.com.spiik.TranscriptActivity;

import com.rapidapi.rapidconnect.Argument;
import com.rapidapi.rapidconnect.RapidApiConnect;

public class TranscriptMainFragment extends Fragment implements View.OnClickListener, TextToSpeech.OnInitListener {
    private ImageButton transcript_translate, transcript_play;
    public TextView transcript_original_text;
    private TextToSpeech tts;
    private Spinner spinner;
    private HashMap<String, Locale> localeMap;
    private TranscriptActivity parentActivity;
    private String currentTranslatedText;
    public String currentText;
    RapidApiConnect connect = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transcript_main, container, false);

        parentActivity = ((TranscriptActivity)getActivity());

        transcript_translate = (ImageButton) view.findViewById(R.id.transcript_translate_button);
        transcript_play = (ImageButton) view.findViewById(R.id.transcript_play_button);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        transcript_original_text = (TextView) view.findViewById(R.id.transcript_original_text);



        currentText = (String)transcript_original_text.getText();

        //setOnclickListeners
        transcript_translate.setOnClickListener(this);
        transcript_play.setOnClickListener(this);

        tts = new TextToSpeech(this.getActivity().getBaseContext(), this);
        spinner = (Spinner) view.findViewById(R.id.spinner);

        //Check if connected to the internet
        if(!parentActivity.isConnected()){
            transcript_original_text.setText("Oops! Something went wrong.. No Internet Connectivity");
        }

        connect = new RapidApiConnect("Translatte", "b4d6cbb4-e279-49f9-b1d4-b49d53428d91");
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.transcript_translate_button:
                //call translate api and pass to addtranslatefragment
                TranslateText request = new TranslateText(transcript_original_text.getText().toString(), "en", getSpinnerLanguage());
                request.execute();
                String curLocaleStr = this.spinner.getSelectedItem().toString();
                Locale curLoc = localeMap.get(curLocaleStr);
                parentActivity.addTranslateFragment(curLoc);
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
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this.getActivity().getBaseContext(), android.R.layout.simple_spinner_item, displayLangs = displayLangs.subList(1,displayLangs.size()));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    private void speakOut() {
        //assume the text from the ocr is english
        tts.setLanguage(localeMap.get("English"));
        Log.i("speaking", (String)transcript_original_text.getText());
        tts.speak(transcript_original_text.getText(), TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private String getSpinnerLanguage(){
        String defLang = spinner.getSelectedItem().toString();
        Locale loc = localeMap.get(defLang);
        Log.v("TEST", loc.getLanguage());
        return loc.getLanguage();
    }

    private class TranslateText extends AsyncTask<Void, Void, Map<String, Object>>{
        private String readText, sourceLang, targetLang;
        final String TRANSLATE_BASE_URL = "https://translation.googleapis.com/language/translate/v2?";
        final String TRANSLATE_API_KEY = "key";
        final String SOURCE_LANG = "source";
        final String TARGET_LANG = "target";
        final String READ_TEXT = "q";


        public TranslateText (String text, String sLang, String tLang ){
            super();
            readText = text;
            sourceLang = sLang;
            targetLang = tLang;
        }

        @Override
        protected Map<String, Object> doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
//                Uri.Builder builder = new Uri.Builder();
//                builder.scheme("https")
//                        .authority("translation.googleapis.com")
//                        .appendPath("language")
//                        .appendPath("translate")
//                        .appendPath("v2")
//                        .appendQueryParameter(TRANSLATE_API_KEY, "AIzaSyDG12FxjQPxtgoe3sKLv-meHkkYlZQT4YM")
//                        .appendQueryParameter(SOURCE_LANG, sourceLang)
//                        .appendQueryParameter(TARGET_LANG, targetLang)
//                        .appendQueryParameter(READ_TEXT, readText)
//                        .build();
//                Log.v("TEST", builder.toString());
//                URL url = new URL(builder.toString());
//                // Create the request to OpenWeatherMap, and open the connection
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                // Read the input stream into a String
//                InputStream inputStream = urlConnection.getInputStream();
//                StringBuffer buffer = new StringBuffer();
//
//
//                if(urlConnection.getInputStream() == null){
//                    return "Sorry, cannot translate this language.";
//                }
//
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
//                    // But it does make debugging a *lot* easier if you print out the completed
//                    // buffer for debugging.  + "\n"
//                    buffer.append(line);
//                }
//
//                if (buffer.length() == 0) {
//                    // Stream was empty.  No point in parsing.
//                    return null;
//                }
//                return buffer.toString();
                Map<String, Argument> body = new HashMap<String, Argument>();

                body.put("apiKey", new Argument("data", "AIzaSyDG12FxjQPxtgoe3sKLv-meHkkYlZQT4YM"));
                body.put("string", new Argument("data", readText));
                body.put("targetLanguage", new Argument("data", targetLang));
//                body.put("sourceLanguage", new Argument("data", sourceLang));
                Map<String, Object> response = connect.call("GoogleTranslate", "Translate", body);
                if (response.get("success") != null) {
                    Log.e("DANG:", response.toString());
                    return response;
                } else {
                    Log.e("Momo", "The Translate API had an error responding");
                    return null;
                }

            } catch (IOException e) {
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                e.printStackTrace();
//            } finally{
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (final IOException e) {
//                        Log.e("PlaceholderFragment", "Error closing stream", e);
//                    }
//                }
//            }
            }
            return null;
        }



        @Override
        protected void onPostExecute(Map<String, Object> map) {
            super.onPostExecute(map);

            try {

                if(map != null) {
                    JSONObject translatedJSON = new JSONObject(map);
//                parentActivity.passTranslatedJSON( translatedJSON);
//                JSONObject data = translatedJSON.getJSONObject("contextWrites");
                    String translations = translatedJSON.getString("success");
                    Log.i("JSON", translatedJSON.toString());


                    currentTranslatedText = "";
//                for(int i = 0; i < translations.length(); i++) {
//                    JSONObject curTranslation = translations.getJSONObject(i);
//                    String curTranslatedText = curTranslation.get("translatedText").toString();
//                    currentTranslatedText += curTranslatedText;
//                }
                    parentActivity.passTranslatedJSON(translations);
                    Log.i("translated data", translations);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("json", map + "d");
        }
    }





}
