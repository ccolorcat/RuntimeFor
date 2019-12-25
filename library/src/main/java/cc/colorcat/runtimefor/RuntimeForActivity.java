package cc.colorcat.runtimefor;

import android.content.Intent;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Author: cxx
 * Date: 2019-10-29
 * GitHub: https://github.com/ccolorcat
 */
public abstract class RuntimeForActivity extends AppCompatActivity {

    @CallSuper
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        RuntimeFor.handleRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @CallSuper
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        RuntimeFor.handleActivityResult(this, requestCode, resultCode, data);
    }
}
