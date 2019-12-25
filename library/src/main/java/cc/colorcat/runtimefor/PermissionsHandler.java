package cc.colorcat.runtimefor;

import androidx.annotation.NonNull;

/**
 * Author: cxx
 * Date: 2019-10-28
 * GitHub: https://github.com/ccolorcat
 */
public interface PermissionsHandler {
    void onAllGranted(@NonNull String[] permissions);

    void onDeniedOccur(@NonNull String[] granted, @NonNull String[] denied);
}
