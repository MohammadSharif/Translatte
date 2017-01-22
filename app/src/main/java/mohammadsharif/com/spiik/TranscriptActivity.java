package mohammadsharif.com.spiik;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import layout.TranscriptTranslateFragment;

public class TranscriptActivity extends FragmentActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcript);
    }

    // Show translate View
    public void addTranscriptFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        TranscriptTranslateFragment translateFragment = new TranscriptTranslateFragment();
        fragmentTransaction.add(R.id.activity_transcript, translateFragment, "fragment_transcript_translate");
        fragmentTransaction.commit();
    }


}
