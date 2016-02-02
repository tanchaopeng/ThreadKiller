package Core;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by tanch on 2016/1/8.
 */
public class ThreadHelper extends AsyncTask<Object,Integer,String>{
    private Context context;
    ActivityManager am;
    PackageManager pm;
    UsageStatsManager usm;

    public ThreadHelper(Context _context)
    {
        this.context=_context;
        this.am= (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        this.pm =context.getPackageManager();
        this.usm=(UsageStatsManager)context.getSystemService(Context.USAGE_STATS_SERVICE);
    }
    //正在运行的
    //su 取得运行的包名
    public List<ProcessModel> getRunningProcess(){
        List<ProcessModel> list = new ArrayList<ProcessModel>();
        PackagesInfo pi = new PackagesInfo(context);

        List<String> ss=ThreadHelper.ExecShell_List("ps");
        for (int i=1;i<ss.size()-1;i++)
        {
            String s=null;
            int strat=ss.get(i).indexOf(" ");
            s=ss.get(i).substring(0,strat);
            //非用户组的剔除
            if (s.indexOf("u0_")==-1)
                continue;
            ProcessModel p=new ProcessModel();
            strat=ss.get(i).indexOf(" ",10);
            s=ss.get(i).substring(10,strat);
            //p.pid=Integer.valueOf(s);
            strat=ss.get(i).lastIndexOf(" ");
            s=ss.get(i).substring(strat+1,ss.get(i).length());
            //包名没有.的剔除
            if (s.indexOf(".")==-1)
                continue;
            p.processName=s;
            if (pi.getInfo(p.processName)!=null)
            {
                p.icon=pi.getInfo(p.processName).loadIcon(pm);
                p.name=pi.getInfo(p.processName).loadLabel(pm).toString();
            }

            list.add(p);
        }
        return list;
    }

    //去重复
    public List<ProcessModel> OnlyOneProcess( List<ProcessModel> data)
    {
        Log.i("信息","数量:"+data.size());
        List<ProcessModel> ret=new ArrayList<ProcessModel>();
        Set<String> list=new HashSet<>();
        for (ProcessModel p:data)
        {
            list.add(p.processName);
        }

        for (ProcessModel p:data)
        {
            if (list.contains(p.processName))
            {
                ret.add(p);
                list.remove(p.processName);
            }
        }
        Log.i("信息","数量:"+ret.size());
        return ret;
    }

    public List<ProcessModel> getRunningProcess(int type)
    {
        List<ProcessModel> list = new ArrayList<ProcessModel>();
        List<UsageStats> Lus=getUsageStatistics(UsageStatsManager.INTERVAL_DAILY);
        for (UsageStats us:Lus)
        {
            try
            {
                ProcessModel p = new ProcessModel();
                p.lastTime=us.getLastTimeUsed();
                p.processName=us.getPackageName();
                p.icon=context.getPackageManager().getApplicationIcon(p.processName);
                p.name=(String)context.getPackageManager().getApplicationInfo(p.processName, PackageManager.GET_META_DATA).loadLabel(pm);
                list.add(p);
            }
            catch (Exception e)
            {

            }
        }
        return list;
    }
    private List<UsageStats> getUsageStatistics(int intervalType) {
        // Get the app statistics since one year ago from the current time.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);

        List<UsageStats> queryUsageStats = usm.queryUsageStats(intervalType, cal.getTimeInMillis(),System.currentTimeMillis());
        return queryUsageStats;
    }
    @Override
    protected String doInBackground(Object... params) {
        List<String> LPackageName=(List<String>)params[0];
        KillThread(LPackageName);
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        Toast.makeText(context, "完成", Toast.LENGTH_SHORT).show();
    }

    public class PackagesInfo {
        private List<ApplicationInfo> appList;

        public PackagesInfo(Context context){
            //通包管理器，检索所有的应用程序（甚至卸载的）与数据目录
            PackageManager pm = context.getApplicationContext().getPackageManager();
            appList = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        }
        /**
         * 通过一个程序名返回该程序的一个Application对象。
         * @param name 程序名
         * @return ApplicationInfo
         */
        public ApplicationInfo getInfo(String name){
            if(name == null){
                return null;
            }
            for(ApplicationInfo appinfo : appList){
                if(name.equals(appinfo.processName)){
                    return appinfo;
                }
            }
            return null;
        }

    }

    public static final int FILTER_ALL_APP = 0; // 所有应用程序
    public static final int FILTER_SYSTEM_APP = 1; // 系统程序
    public static final int FILTER_THIRD_APP = 2; // 第三方应用程序
    public static final int FILTER_SDCARD_APP = 3; // 安装在SDCard的应用程序

    public List<ProcessModel> QueryFilterAppInfo(int filter) {
        // 查询所有已经安装的应用程序
        List<ApplicationInfo> listAppcations = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations,new ApplicationInfo.DisplayNameComparator(pm));// 排序
        List<ProcessModel> Lpm = new ArrayList<ProcessModel>(); // 保存过滤查到的ProcessModel
        // 根据条件来过滤
        switch (filter) {
            case FILTER_ALL_APP: // 所有应用程序
                Lpm.clear();
                for (ApplicationInfo app : listAppcations) {
                    Lpm.add(getAppInfo(app));
                }
                return Lpm;
            case FILTER_SYSTEM_APP: // 系统程序
                Lpm.clear();
                for (ApplicationInfo app : listAppcations) {
                    if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        Lpm.add(getAppInfo(app));
                    }
                }
                return Lpm;
            case FILTER_THIRD_APP: // 第三方应用程序
                Lpm.clear();
                for (ApplicationInfo app : listAppcations) {
                    //非系统程序
                    if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                        Lpm.add(getAppInfo(app));
                    }
                    //本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了
                    else if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0){
                        Lpm.add(getAppInfo(app));
                    }
                }
                break;
            case FILTER_SDCARD_APP: // 安装在SDCard的应用程序
                Lpm.clear();
                for (ApplicationInfo app : listAppcations) {
                    if ((app.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                        Lpm.add(getAppInfo(app));
                    }
                }
                return Lpm;
            default:
                return null;
        }
        return Lpm;
    }
    // 构造一个AppInfo对象 ，并赋值
    private ProcessModel getAppInfo(ApplicationInfo app) {
        ProcessModel p = new ProcessModel();
        p.name=(String)app.loadLabel(pm);
        p.icon=app.loadIcon(pm);
        p.processName=app.packageName;
        return p;
    }

    public void KillThread(List<String> LPackageName)
        {
            //强行停止
            for(int i=0;i<LPackageName.size();i++)
            {
                LPackageName.set(i,"am force-stop "+LPackageName.get(i));
            }
            ExecShell(LPackageName);
    }

    public static String execShell(String cmd) {
        String result=null;
        try {
            Process p = Runtime.getRuntime().exec("su");
            OutputStream outputStream = p.getOutputStream();

            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();

            InputStream inputStream = p.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                result += line;
            }
            inputStream.close();
            return reader.readLine();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }
    public static List<String> ExecShell_List(String cmd)
    {
        List<String> result=new ArrayList<String>();
        try {
            Process p = Runtime.getRuntime().exec("su");
            OutputStream outputStream = p.getOutputStream();

            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();

            InputStream inputStream = p.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
            inputStream.close();
            return result;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }
    public static void ExecShell(List<String> cmd)
    {
        try
        {
            Process p = Runtime.getRuntime().exec("su");
            OutputStream outputStream = p.getOutputStream();

            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            for (String s:cmd)
            {
                dataOutputStream.writeBytes(s+"\n");
                dataOutputStream.flush();
            }
            dataOutputStream.close();
            outputStream.close();

        }catch (Throwable t) {
            t.printStackTrace();
        }

    }
}


