package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import mohammadsharif.com.spiik.R;
import mohammadsharif.com.spiik.TranscriptActivity;

public class TranscriptMainFragment extends Fragment implements View.OnClickListener {
    private Button transcript_translate;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transcript_main, container, false);

        transcript_translate = (Button) view.findViewById(R.id.transcript_translate_button);
        transcript_translate.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.transcript_translate_button:
                Log.v("Test","Not");
                ((TranscriptActivity)getActivity()).addTranscriptFragment();
                break;

        }
    }
}
