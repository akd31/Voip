package searchrescue.voip;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

public class IncomingCallActivity extends AppCompatActivity {
    private String callId;
    private Call call ;
    private AudioPlayer audioPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        call = CurrentCall.currentCall;
        if (call != null) {
            audioPlayer = new AudioPlayer(this);
            audioPlayer.playRingtone();
            call.addCallListener(new SinchCallListener());
            TextView remoteUser = (TextView) findViewById(R.id.remoteUser);
            remoteUser.setText(call.getRemoteUserId());
        } else {
            finish();
        }
        Button answer = (Button) findViewById(R.id.answerButton);
        answer.setOnClickListener(mClickListener);
        Button decline = (Button) findViewById(R.id.declineButton);
        decline.setOnClickListener(mClickListener);

        callId = getIntent().getStringExtra("CALL_ID");
    }

    private void answerClicked() {
        audioPlayer.stopRingtone();
        if (call != null) {
            call.answer();
            Intent intent = new Intent(this, CallScreenActivity.class);
            intent.putExtra("CALL_ID", callId);
            intent.addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(CurrentCall.currentCall  == null) {
            finish();
        }
    }

    private void declineClicked() {
        audioPlayer.stopRingtone();
        if (call != null) {
            call.hangup();
        }
        CurrentCall.currentCall = null;
        finish();
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {

            audioPlayer.stopRingtone();
            CurrentCall.currentCall = null;
            finish();
        }

        @Override
        public void onCallEstablished(Call call) {

        }

        @Override
        public void onCallProgressing(Call call) {

        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.answerButton:
                    answerClicked();
                    break;
                case R.id.declineButton:
                    declineClicked();
                    break;
            }
        }
    };
}
