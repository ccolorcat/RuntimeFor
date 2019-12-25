package cc.colorcat.runtimefor;

import android.content.Intent;

import androidx.annotation.Nullable;

/**
 * Author: cxx
 * Date: 2019-10-29
 * GitHub: https://github.com/ccolorcat
 */
public interface ResultHandler {
    void onResult(@RuntimeForResult.Result int resultCode, @Nullable Intent data);
}
