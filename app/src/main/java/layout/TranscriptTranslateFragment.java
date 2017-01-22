package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONObject;

import mohammadsharif.com.spiik.R;
import mohammadsharif.com.spiik.TranscriptActivity;


public class TranscriptTranslateFragment extends Fragment implements View.OnClickListener{
    private ImageButton transcript_translate_cancel_button;
    private TextView transcript_translated_text;
    private JSONObject translatedJSON;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transcript_translate, container, false);

        transcript_translated_text = (TextView) view.findViewById(R.id.transcript_translated_text);
        transcript_translate_cancel_button = (ImageButton) view.findViewById(R.id.transcript_translate_cancel_button);
        transcript_translate_cancel_button.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.transcript_translate_cancel_button:
                ((TranscriptActivity)getActivity()).killTranslateFragment();
                break;
        }
    }

    public void setTranslatedJSON(JSONObject tJSON){
        //set JSON
        translatedJSON = tJSON;

        //display on text view
        transcript_translated_text.setText(tJSON.toString());

    }
}
