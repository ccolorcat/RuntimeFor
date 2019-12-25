package cc.colorcat.runtimefor.sample;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Arrays;

import cc.colorcat.runtimefor.PermissionsHandler;
import cc.colorcat.runtimefor.ResultHandler;
import cc.colorcat.runtimefor.RuntimeFor;
import cc.colorcat.runtimefor.RuntimeForActivity;
import cc.colorcat.runtimefor.RuntimeForResult;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends RuntimeForActivity {
    private String[] mPermissions1 = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private String[] mPermissions2 = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION};

    private ImageView mForResultIv;
    private Intent mPicImage;

    {
        mPicImage = new Intent(Intent.ACTION_GET_CONTENT);
        mPicImage.setType("image/*");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mForResultIv = findViewById(R.id.image_view);

        findViewById(R.id.btn_permissions_application).setOnClickListener(mClick);
        findViewById(R.id.btn_permissions_activity).setOnClickListener(mClick);

        findViewById(R.id.btn_result_application).setOnClickListener(mClick);
        findViewById(R.id.btn_result_activity).setOnClickListener(mClick);
    }

    private View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_permissions_application:
                    RuntimeFor.once(getApplicationContext())
                            .permissions(mPermissions1)
                            .request(mPermissionsHandler);
                    break;
                case R.id.btn_permissions_activity:
                    RuntimeFor.once(MainActivity.this)
                            .permissions(mPermissions2)
                            .request(mPermissionsHandler);
                    break;
                case R.id.btn_result_application:
                    RuntimeFor.once(getApplicationContext())
                            .intent(mPicImage)
                            .forResult(mResultHandler);
                    break;
                case R.id.btn_result_activity:
                    RuntimeFor.once(MainActivity.this)
                            .intent(mPicImage)
                            .forResult(mResultHandler);
                    break;
                default:
                    break;
            }
        }
    };

    private PermissionsHandler mPermissionsHandler = new PermissionsHandler() {
        @Override
        public void onAllGranted(@NonNull String[] permissions) {
            Log.d(RuntimeFor.TAG, "onAllGranted " + Arrays.toString(permissions));
        }

        @Override
        public void onDeniedOccur(@NonNull String[] granted, @NonNull String[] denied) {
            Log.w(RuntimeFor.TAG, "onDeniedOccur granted: " + Arrays.toString(granted) + ", denied: " + Arrays.toString(denied));
        }
    };

    private ResultHandler mResultHandler = new ResultHandler() {
        @Override
        public void onResult(int resultCode, @Nullable Intent data) {
            Log.d(RuntimeFor.TAG, resultCode + ", " + data);
            if (resultCode == RuntimeForResult.RESULT_OK && data != null) {
                mForResultIv.setImageURI(data.getData());
            }
        }
    };
}
