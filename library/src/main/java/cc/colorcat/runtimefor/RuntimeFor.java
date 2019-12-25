package cc.colorcat.runtimefor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Author: cxx
 * Date: 2019-10-28
 * GitHub: https://github.com/ccolorcat
 */
public final class RuntimeFor {
    public static final String TAG = "RuntimeFor";

    static class Holder {
        final static RuntimeFor INSTANCE = new RuntimeFor();
    }

    public static void handleRequestPermissionsResult(
            @NonNull Activity activity,
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        Holder.INSTANCE.handlePermissionsResult(activity, requestCode, permissions, grantResults);
    }

    public static void handleActivityResult(
            @NonNull Activity activity,
            int requestCode,
            int resultCode,
            @Nullable Intent data
    ) {
        Holder.INSTANCE.handleIntentResult(activity, requestCode, resultCode, data);
    }

    @NonNull
    public static RuntimeFor once(@NonNull Context context) {
        Holder.INSTANCE.mContextRef = new WeakReference<>(requireNonNull(context, "context == null"));
        return Holder.INSTANCE;
    }


    private final Map<Integer, RuntimeForPermissions> mRuntimePermissions = new WeakHashMap<>(4);
    private final Map<Integer, RuntimeForResult> mRuntimeForResults = new WeakHashMap<>(4);
    private Reference<Context> mContextRef;

    @NonNull
    public RuntimeForPermissions permissions(@NonNull String... permissions) {
        if (permissions.length == 0) {
            throw new IllegalArgumentException("permissions is empty.");
        }
        return new RuntimeForPermissions(getAndClear(), permissions);
    }

    @NonNull
    public RuntimeForResult intent(@NonNull Intent intent) {
        return new RuntimeForResult(getAndClear(), requireNonNull(intent, "intent is null."));
    }

    @NonNull
    private Context getAndClear() {
        Context context = mContextRef != null ? mContextRef.get() : null;
        if (context == null) {
            throw new IllegalStateException("The context is null or recycled.");
        }
        mContextRef.clear();
        mContextRef = null;
        return context;
    }

    private void handlePermissionsResult(
            @NonNull Activity activity,
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        boolean handled = true;
        try {
            RuntimeForPermissions rp = mRuntimePermissions.get(requestCode);
            if (rp != null) {
                handled = rp.handlePermissionsResult(requestCode, permissions, grantResults);
            } else {
                handled = false;
            }
        } finally {
            if (handled) {
                mRuntimePermissions.remove(requestCode);
            }
        }
    }

    private void handleIntentResult(
            @NonNull Activity activity,
            int requestCode,
            int resultCode,
            @Nullable Intent data
    ) {
        boolean handled = true;
        try {
            RuntimeForResult rfr = mRuntimeForResults.get(requestCode);
            if (rfr != null) {
                handled = rfr.handleIntentResult(requestCode, resultCode, data);
            } else {
                handled = false;
            }
        } finally {
            if (handled) {
                mRuntimeForResults.remove(requestCode);
            }
        }
    }

    int enqueue(@NonNull RuntimeForPermissions runtimeForPermissions) {
        return enqueue(mRuntimePermissions, runtimeForPermissions);
    }

    int enqueue(@NonNull RuntimeForResult runtimeForResult) {
        return enqueue(mRuntimeForResults, runtimeForResult);
    }

    private RuntimeFor() {
    }


    private static <T> int enqueue(@NonNull Map<Integer, T> array, @NonNull T t) {
        int requestCode = array.size();
        array.put(requestCode, t);
        return requestCode;
    }

    private static <T> T requireNonNull(T t, String msg) {
        if (t == null) {
            throw new NullPointerException(msg);
        }
        return t;
    }
}
