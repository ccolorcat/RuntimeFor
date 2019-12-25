package cc.colorcat.runtimefor;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: cxx
 * Date: 2019-10-28
 * GitHub: https://github.com/ccolorcat
 */
public final class RuntimeForPermissions {
    /**
     * the requestCode must be lower 16 bits.
     */
    private static final int REQUEST_CODE_IGNORE_RESULT = Integer.MAX_VALUE >> 16;

    private final Context mContext;
    private final String[] mPermissions;
    private int mRequestCode = REQUEST_CODE_IGNORE_RESULT;
    private PermissionsHandler mHandler;

    RuntimeForPermissions(Context context, String[] permissions) {
        mContext = context;
        mPermissions = permissions;
    }

    @MainThread
    public void request(PermissionsHandler handler) {
        mHandler = handler;
        boolean allGranted = hasAllPermissions();
        if (mHandler == null) {
            if (!allGranted) {
                directRequest();
            }
        } else {
            if (allGranted) {
                mHandler.onAllGranted(mPermissions);
            } else {
                enqueueAndRequest();
            }
        }
    }

    boolean handlePermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mRequestCode == requestCode && (Arrays.equals(mPermissions, permissions) || permissions.length == 0)) {
            if (mHandler != null) {
                Map<Boolean, String[]> group = groupPermissions();
                String[] granted = group.get(Boolean.TRUE);
                String[] denied = group.get(Boolean.FALSE);
                assert denied != null;
                if (denied.length == 0) {
                    mHandler.onAllGranted(mPermissions);
                } else {
                    assert granted != null;
                    mHandler.onDeniedOccur(granted, denied);
                }
            }
            return true;
        }
        return false;
    }

    private void directRequest() {
        if (mContext instanceof Activity) {
            requestPermissions((Activity) mContext, REQUEST_CODE_IGNORE_RESULT);
        } else {
            RuntimeForCompatActivity.startForPermissions(mContext, mPermissions, REQUEST_CODE_IGNORE_RESULT);
        }
    }

    private void enqueueAndRequest() {
        mRequestCode = RuntimeFor.Holder.INSTANCE.enqueue(this);
        if (mContext instanceof RuntimeForActivity) {
            requestPermissions((RuntimeForActivity) mContext, mRequestCode);
        } else {
            RuntimeForCompatActivity.startForPermissions(mContext, mPermissions, mRequestCode);
        }
    }

    private Map<Boolean, String[]> groupPermissions() {
        final int length = mPermissions.length;
        List<String> granted = new ArrayList<>(length);
        List<String> denied = new ArrayList<>(length);
        for (String p : mPermissions) {
            if (hasPermission(p)) {
                granted.add(p);
            } else {
                denied.add(p);
            }
        }
        String[] grantedArray = new String[granted.size()];
        String[] deniedArray = new String[denied.size()];
        granted.toArray(grantedArray);
        denied.toArray(deniedArray);
        Map<Boolean, String[]> result = new HashMap<>(2);
        result.put(Boolean.TRUE, grantedArray);
        result.put(Boolean.FALSE, deniedArray);
        return result;
    }

    private boolean hasAllPermissions() {
        for (String p : mPermissions) {
            if (!hasPermission(p)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasPermission(String permission) {
        return ActivityCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity, mPermissions, requestCode);
    }
}
