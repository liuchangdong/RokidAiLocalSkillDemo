package com.rokid.ai.skill.localdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.rokid.localskills.sdk.LocalSdkConfig;
import com.rokid.localskills.sdk.handy.HandyManager;
import com.rokid.localskills.sdk.utils.Logger;


public class GlassHandyActivity extends Activity {

    private static final String TAG = GlassHandyActivity.class.getSimpleName();

    private TextView mTextView;

    private HandyManager mHandyManager;

    /**
     * 技能ID, 开发者网站创建技能时会自动生成
     */
    private static final String SKILL_ID = "R399AFF45CA74EA08C839CF64189F364";

    /**
     * 技能类型,scene为其中一种
     */
    private static final String SKILL_TYPE_SCENE = "scene";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glass_handy);
        mTextView = findViewById(R.id.handy_text);
        mHandyManager = new HandyManager(getApplicationContext(), SKILL_ID, SKILL_TYPE_SCENE);
        mHandyManager.setHandyListener(mHandyListener);

        dispatchIntent(getIntent());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mHandyManager != null) {
            mHandyManager.updateStack();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        dispatchIntent(intent);
    }

    /**
     * 解析nlp,并且处理intent
     * @param intent
     */
    public void dispatchIntent(Intent intent) {
        if (mHandyManager != null) {
            mHandyManager.handyIntent(intent);
        }
    }

    private HandyManager.IHandyListener mHandyListener = new HandyManager.IHandyListener() {
        @Override
        public boolean dispatchLocal(LocalSdkConfig.LocalBean localBean) {
            return false;
        }

        @Override
        public boolean dispatchNlp(String nlp, LocalSdkConfig.LocalBean localBean) {
            return false;
        }

        @Override
        public boolean dispatchIntent(String intent, LocalSdkConfig.LocalBean localBean) {
            Logger.d(TAG, "dispatchIntent " + intent + ", asr = " + localBean.asr);
            setText(localBean.asr + "\n" + intent);
            switch (intent) {
                case LocalSdkConfig.INTENT_WELCOME:
                    return true;
                case "Testing_Exit":
                    if (mHandyManager != null) {
                        mHandyManager.clearAllStack();
                    }
                    finish();
                    return true;
                default:
                    break;
            }
            return false;
        }
    };

    private void setText(final String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mTextView != null) {
                    mTextView.setText(content);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mHandyManager != null) {
            mHandyManager.onDestroy();
            mHandyManager = null;
        }
        super.onDestroy();

    }
}
