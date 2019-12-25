package cc.colorcat.runtimefor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.IntDef;
import androidx.annotation.MainThread;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author: cxx
 * Date: 2019-10-28
 * GitHub: https://github.com/ccolorcat
 */
public final class RuntimeForResult {
    public static final int RESULT_COMPONENT_ABSENT = -1000;
    public static final int RESULT_OK = Activity.RESULT_OK;
    public static final int RESULT_CANCELED = Activity.RESULT_CANCELED;
    public static final int RESULT_FIRST_USER = Activity.RESULT_FIRST_USER;

    @IntDef({RESULT_OK, RESULT_CANCELED, RESULT_COMPONENT_ABSENT, RESULT_FIRST_USER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Result {
    }

    /**
     * the requestCode must be lower 16 bits.
     */
    private static final int REQUEST_CODE_IGNORE_RESULT = Integer.MAX_VALUE >> 17;
    private final Context mContext;
    private final Intent mIntent;
    private int mRequestCode = REQUEST_CODE_IGNORE_RESULT;
    private ResultHandler mHandler;

    RuntimeForResult(Context context, Intent intent) {
        mContext = context;
        mIntent = intent;
    }

    @MainThread
    public void forResult(ResultHandler handler) {
        mHandler = handler;
        boolean canHandle = hasComponent();
        if (mHandler == null) {
            if (canHandle) {
                directForResult();
            }
        } else {
            if (canHandle) {
                enqueueAndForResult();
            } else {
                mHandler.onResult(RESULT_COMPONENT_ABSENT, null);
            }
        }
    }

    boolean handleIntentResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (mRequestCode == requestCode) {
            if (mHandler != null) {
                mHandler.onResult(resultCode, data);
            }
            return true;
        }
        return false;
    }

    private void directForResult() {
        if (mContext instanceof Activity) {
            ((Activity) mContext).startActivityForResult(mIntent, REQUEST_CODE_IGNORE_RESULT);
        } else {
            RuntimeForCompatActivity.startForResult(mContext, mIntent, REQUEST_CODE_IGNORE_RESULT);
        }
    }

    private void enqueueAndForResult() {
        mRequestCode = RuntimeFor.Holder.INSTANCE.enqueue(this);
        if (mContext instanceof RuntimeForActivity) {
            ((RuntimeForActivity) mContext).startActivityForResult(mIntent, mRequestCode);
        } else {
            RuntimeForCompatActivity.startForResult(mContext, mIntent, mRequestCode);
        }
    }

    private boolean hasComponent() {
        return mIntent.resolveActivity(mContext.getPackageManager()) != null;
    }
}
