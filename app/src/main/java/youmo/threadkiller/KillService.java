package youmo.threadkiller;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import Core.SPHelper;
import Core.ThreadHelper;

/**
 * Created by tanch on 2016/1/11.
 */
public class KillService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
    super.onCreate();
        Log.i("信息","ServiceCreate");
        IntentFilter intentFilter=new IntentFilter(Intent.ACTION_TIME_TICK);
        this.registerReceiver(new KillReceiver(),intentFilter);
    }
}
