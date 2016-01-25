package youmo.threadkiller;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import Core.FileHelper;
import Core.SPHelper;
import Core.ThreadHelper;

/**
 * Created by tanch on 2016/1/11.
 */
public class KillReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ThreadHelper th= new ThreadHelper(context);
        SPHelper sph=new SPHelper(context);
        List<String> BlackList=FileHelper.GetBlackListFile(new File(context.getFilesDir(),"BlackList.txt"));
        if (BlackList!=null&&BlackList.size()>0)
        {
            th.execute(BlackList);
            Toast.makeText(context,"已停止"+String.valueOf(BlackList.size())+"个",Toast.LENGTH_SHORT);
        }
        Log.i("信息","黑名单："+BlackList.size());
        Log.i("信息","ACTION_TIME_TICK");
    }

    public static boolean isBackground(Context context,String PackageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                Log.i("后台", appProcess.processName);
                return true;
            } else {
                Log.i("前台", appProcess.processName);
                return false;
            }
//            if (appProcess.processName.equals(PackageName)) {
//                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
//                    Log.i("后台", appProcess.processName);
//                    return true;
//                } else {
//                    Log.i("前台", appProcess.processName);
//                    return false;
//                }
//            }
        }
        return false;
    }
}
