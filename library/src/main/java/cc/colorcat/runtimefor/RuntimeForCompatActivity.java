package cc.colorcat.runtimefor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

/**
 * Author: cxx
 * Date: 2019-10-29
 * GitHub: https://github.com/ccolorcat
 */
public class RuntimeForCompatActivity extends RuntimeForActivity {
    private static final String KEY_ACTION = "action";
    private static final String ACTION_RUNTIME_FOR_PERMISSIONS = "action_for_permissions";
    private static final String ACTION_RUNTIME_FOR_RESULT = "action_for_result";

    private static final String KEY_PERMISSIONS = "permissions"; // the value type is String[]
    private static final String KEY_INTENT = "intent"; // the value type is Intent

    private static final String KEY_REQUEST_CODE = "request_code"; // the value type is int

    static void startForPermissions(@NonNull Context context, @NonNull String[] permissions, int requestCode) {
        Intent intent = new Intent(context, RuntimeForCompatActivity.class);
        intent.putExtra(KEY_REQUEST_CODE, requestCode);
        intent.putExtra(KEY_ACTION, ACTION_RUNTIME_FOR_PERMISSIONS);
        intent.putExtra(KEY_PERMISSIONS, permissions);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    static void startForResult(@NonNull Context context, @NonNull Intent intent, int requestCode) {
        Intent i = new Intent(context, RuntimeForCompatActivity.class);
        i.putExtra(KEY_REQUEST_CODE, requestCode);
        i.putExtra(KEY_ACTION, ACTION_RUNTIME_FOR_RESULT);
        i.putExtra(KEY_INTENT, intent);
        if (!(context instanceof Activity)) {
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(i);
    }

    private String mAction;
    private String[] mPermissions;
    private Intent mForResultIntent;
    private int mRequestCode = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseIntent();
        check();
        emitAction();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        mRequestCode = intent.getIntExtra(KEY_REQUEST_CODE, -1);
        mAction = intent.getStringExtra(KEY_ACTION);
        if (ACTION_RUNTIME_FOR_PERMISSIONS.equals(mAction)) {
            mPermissions = intent.getStringArrayExtra(KEY_PERMISSIONS);
        } else if (ACTION_RUNTIME_FOR_RESULT.equals(mAction)) {
            mForResultIntent = intent.getParcelableExtra(KEY_INTENT);
        } else {
            throw new IllegalArgumentException("key " + KEY_ACTION + " has invalid value(" + mAction + ").");
        }
    }

    private void check() {
        if (mRequestCode == -1) {
            throw new IllegalArgumentException("no request code.");
        }
        if (ACTION_RUNTIME_FOR_PERMISSIONS.equals(mAction) && mPermissions == null) {
            throw new IllegalArgumentException("action is " + mAction + ", but no permissions.");
        }
        if (ACTION_RUNTIME_FOR_RESULT.equals(mAction) && mForResultIntent == null) {
            throw new IllegalArgumentException("action is " + mAction + ", but no intent.");
        }
    }

    private void emitAction() {
        if (ACTION_RUNTIME_FOR_PERMISSIONS.equals(mAction)) {
            ActivityCompat.requestPermissions(this, mPermissions, mRequestCode);
        } else if (ACTION_RUNTIME_FOR_RESULT.equals(mAction)) {
            startActivityForResult(mForResultIntent, mRequestCode);
        }
    }
}
