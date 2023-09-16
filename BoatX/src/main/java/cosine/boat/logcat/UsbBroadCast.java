package cosine.boat.logcat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

public class UsbBroadCast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (Objects.requireNonNull(intent.getAction())) {

            case Intent.ACTION_MEDIA_MOUNTED: {

                break;
            }
            case Intent.ACTION_MEDIA_UNMOUNTED: {

                break;
            }
            default:
                break;
        }

    }

}
