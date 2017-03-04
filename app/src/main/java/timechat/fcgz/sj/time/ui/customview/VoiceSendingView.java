package timechat.fcgz.sj.time.ui.customview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 发送语音提示控件
 */
public class VoiceSendingView extends RelativeLayout {


    private AnimationDrawable frameAnimation;

    public VoiceSendingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(timechat.fcgz.sj.time.R.layout.voice_sending, this);
        ImageView img = (ImageView)findViewById(timechat.fcgz.sj.time.R.id.microphone);
        img.setBackgroundResource(timechat.fcgz.sj.time.R.drawable.animation_voice);
        frameAnimation = (AnimationDrawable) img.getBackground();

    }

    public void showRecording(){
        frameAnimation.start();
    }

    public void showCancel(){
        frameAnimation.stop();
    }

    public void release(){
        frameAnimation.stop();
    }
}
