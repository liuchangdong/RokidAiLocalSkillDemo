package com.rokid.ai.skill.localdemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.rokid.ai.tts.ITtsCallback;
import com.rokid.localskills.sdk.LocalSdkConfig;
import com.rokid.localskills.sdk.LocalSdkManager;
import com.rokid.localskills.sdk.nlp.NlpAsrBean;
import com.rokid.localskills.sdk.utils.Logger;


public class MainActivity extends Activity {

    /**
     * 申请权限
     */
    private PermissionsManager mPermissionsManager;

    private static final String TAG = MainActivity.class.getSimpleName();
    private ImageView mImageView;

    /**
     * Intent 集合, 和云端对应
     */
    private static final String UP_PRESS_ITENT = "up_press";
    private static final String NEXT_PRESS_INTENT = "next_press";
    private static final String EAT_INTENT = "eat";
    private static final String EXITSKILL_INTENT = "exitskill";
    private static final String CONFIRMEAT_INTENT = "confirmeat";
    private static final String ROKID_WELCOME_INTENT = "ROKID.INTENT.WELCOME";
    private static final String WEBCLOME_INTENT = "webclome";

    /**
     * TTS bindtts 包名字,类名字
     */
    private static final String PACKAGE = "com.rokid.cloudappclient";
    private static final String CLS = "com.rokid.tts.TtsService";

    /**
     * 技能类型,scene为其中一种
     */
    private static final String SKILL_TYPE_SCENE = "scene";

    private ServiceConnection mServiceConnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initPermission();
    }

    private void initPermission() {
        mPermissionsManager = new PermissionsManager();
        mPermissionsManager.requestPermissions(this ,new PermissionsManager.IPermissionsCall() {

            @Override
            public void onDoSuccess() {
                Logger.d(TAG, "onDoSuccess call");
                LocalSdkManager.newInstance(MainActivity.this.getApplicationContext());

                initButtonControl();
                dealIntent(getIntent());
            }
        });
    }

    private void initButtonControl() {
        mImageView = findViewById(R.id.show_number_imageview);
    }

    private void bindTts() {
        Logger.d(TAG, "the init call");
        Intent mIntent = new Intent();
        mIntent.setComponent(new ComponentName(PACKAGE, CLS));
    }

    /**
     * 处理Intent
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        dealIntent(intent);
        super.onNewIntent(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionsManager != null) {
            mPermissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 解析nlp,并且处理intent
     * @param intent
     */
    public void dealIntent(Intent intent) {
        if (intent == null) {
            Logger.d(TAG, "the intent is null");
            return;
        }
        if (LocalSdkConfig.SEND_CMD_VALUE.equals(intent.getStringExtra(LocalSdkConfig.SEND_PARAM_CMD))) {
            String id = intent.getStringExtra(LocalSdkConfig.SEND_PARAM_ID);
            String action = intent.getStringExtra(LocalSdkConfig.SEND_PARAM_ACTION);
            String type = intent.getStringExtra(LocalSdkConfig.SEND_PARAM_TYPE);
            String extra = intent.getStringExtra(LocalSdkConfig.SEND_PARAM_EXTRA);
            String form = intent.getStringExtra(LocalSdkConfig.SEND_PARAM_FORM);

            Logger.d(TAG, "dispatchIntent id = " + id);
            Logger.d(TAG, "dispatchIntent action = " + action);
            Logger.d(TAG, "dispatchIntent type = " + type);
            Logger.d(TAG, "dispatchIntent form = " + form);
            Logger.d(TAG, "dispatchIntent extra = " + extra);
        }

        String nlp = intent.getStringExtra(LocalSdkConfig.SEND_PARAM_NLP);

        Logger.d(TAG, "dispatchIntent nlp = " + nlp);

        if (TextUtils.isEmpty(nlp)) {
            Logger.d(TAG, "the nlp is null");
            return;
        }

        final NlpAsrBean mNlpAsrBean = LocalSdkManager.analysisNlp(nlp);
        Logger.d(TAG, "the NlpAsrBean is" + mNlpAsrBean.toString());

        String intentEvent = mNlpAsrBean.getIntent();
        LocalSdkManager.reportSkillStack(mNlpAsrBean.getAppId(), SKILL_TYPE_SCENE);

        switch (intentEvent) {
            case ROKID_WELCOME_INTENT:
                Toast.makeText(MainActivity.this, ROKID_WELCOME_INTENT, Toast.LENGTH_LONG).show();
                mImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.apple));
                break;
            case UP_PRESS_ITENT:
                Toast.makeText(MainActivity.this, UP_PRESS_ITENT, Toast.LENGTH_LONG).show();
                mImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.apple_alpha));
                break;
            case NEXT_PRESS_INTENT:
                Logger.d(TAG, NEXT_PRESS_INTENT);
                Toast.makeText(MainActivity.this, NEXT_PRESS_INTENT, Toast.LENGTH_LONG).show();
                mImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.apple));
                break;
            case WEBCLOME_INTENT:
                Logger.d(TAG, WEBCLOME_INTENT);
                Toast.makeText(MainActivity.this, WEBCLOME_INTENT, Toast.LENGTH_LONG).show();
                mImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.apple_alpha));
                break;
            case EXITSKILL_INTENT:
                Logger.d(TAG, EXITSKILL_INTENT);
                finish();
                break;
            case CONFIRMEAT_INTENT:
                Logger.d(TAG, CONFIRMEAT_INTENT);
                try {
                    LocalSdkManager.speakTTS("你喜欢喝什么", new ITtsCallback.Stub() {
                        @Override
                        public void onStart(int id) throws RemoteException {

                        }

                        @Override
                        public void onComplete(int id) throws RemoteException {
                            Logger.d(TAG, "the onComplete callback");
                            LocalSdkManager.requestConfirm(EAT_INTENT,"food", mNlpAsrBean.getAppId(), "1");
                        }

                        @Override
                        public void onCancel(int id) throws RemoteException {

                        }

                        @Override
                        public void onError(int id, int err) throws RemoteException {

                        }

                        @Override
                        public void onFilterOut(String content, String key, int existId) throws RemoteException {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case EAT_INTENT:
            case "ROKID.INTENT.CONFIRM_RETRY":
                LocalSdkManager.speakTTS("我知道了你喜欢喝" + mNlpAsrBean.getAsr() + "了");
                Logger.d(TAG, EAT_INTENT + "call");
                break;
                default:
                    break;
        }
    }

    @Override
    protected void onDestroy() {
        if (mPermissionsManager != null) {
            mPermissionsManager.onDestroy();
            mPermissionsManager = null;
        }
        if (mServiceConnect != null) {
            unbindService(mServiceConnect);
        }
        LocalSdkManager.onDestory();
        super.onDestroy();

    }
}
