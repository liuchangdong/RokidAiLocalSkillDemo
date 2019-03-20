package com.rokid.ai.skill.localdemo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

public class PermissionsManager {

    public static final int EXTERNAL_STORAGE_REQ_CODE = 0;

    private IPermissionsCall mPermissionsCall;

    public  void requestPermissions(Activity activity, IPermissionsCall call) {
        this.mPermissionsCall = call;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            /* 如果App的权限申请曾经被用户拒绝过，就需要在这里跟用户做出解释 */
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(activity,"please give me the permission", Toast.LENGTH_SHORT).show();
            } else {
                /* 进行权限请求 */
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE },
                        EXTERNAL_STORAGE_REQ_CODE);
            }
        } else {
            if (mPermissionsCall != null) {
                mPermissionsCall.onDoSuccess();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PermissionsManager.EXTERNAL_STORAGE_REQ_CODE) {
            boolean isAllGranted = true;
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            if(!isAllGranted) {
                Log.d("CloudAppClient", "onRequestPermissionsResult run run run run !!");
                System.exit(0);
            } else {
                if (mPermissionsCall != null) {
                    mPermissionsCall.onDoSuccess();
                }
            }
        }
    }

    public interface IPermissionsCall {
        void onDoSuccess();
    }

    public void onDestroy() {
        mPermissionsCall = null;
    }

}
