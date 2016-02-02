package youmo.threadkiller;

import android.app.ActivityManager;
import android.app.ListActivity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import Core.FileHelper;
import Core.ProcessModel;
import Core.SPHelper;
import Core.ThreadHelper;

public class MainActivity extends AppCompatActivity {

    ThreadHelper th;
    ThreadAdapter ta;
    ListView lv;
    Boolean IsListSelect=false;
    List<ProcessModel> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            e.printStackTrace();
        }
        lv=(ListView)findViewById(R.id.DataList);
        th =new ThreadHelper(this);

        if (Build.VERSION.SDK_INT<21)
            data=th.OnlyOneProcess(th.getRunningProcess());
        else
            data=th.OnlyOneProcess(th.getRunningProcess(UsageStatsManager.INTERVAL_DAILY));
        ta =new ThreadAdapter(this,R.layout.thread_list,data);
        lv.setAdapter(ta);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!IsListSelect)
                {
                    ProcessModel p= ta.getItem(position);
                    new ThreadHelper(getApplicationContext()).execute(new ArrayList<String>(Arrays.asList(p.processName)));
                    ta.remove(p);
                    ta.notifyDataSetChanged();
                }
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (IsListSelect)
                    IsListSelect=false;
                else
                    IsListSelect=true;
                ta.notifyDataSetChanged();
                //通知toolbar重画
                invalidateOptionsMenu();
                return true;
            }
        });
    }

    public void Click_1(View v)
    {
        List<String> list=new ArrayList<String>();
        for (ProcessModel p:data)
        {
            list.add(p.processName);
        }
        new ThreadHelper(getApplicationContext()).execute(list);
    }
    public void Click_2(View v)
    {
        ta.clear();
        ta.addAll(th.getRunningProcess(UsageStatsManager.INTERVAL_DAILY));
        ta.notifyDataSetChanged();
    }
    public class ThreadAdapter extends ArrayAdapter<ProcessModel>
    {
        private int resource;
        public ThreadAdapter(Context context, int resource, List<ProcessModel> objects) {
            super(context, resource, objects);
            this.resource=resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ProcessModel p=getItem(position);

            View view;

            //重用VIEW
            if (convertView!=null)
            {
                view=convertView;
            }
            else
            {
                LayoutInflater inflater=getLayoutInflater();
                view=inflater.inflate(resource,null);
            }


            CheckBox cb=(CheckBox)view.findViewById(R.id.thread_check);
            ImageView iv=(ImageView)view.findViewById(R.id.thread_image);
            TextView tv1=(TextView)view.findViewById(R.id.package_name);
            TextView tv2=(TextView)view.findViewById(R.id.process_name);


            if (IsListSelect)
            {
                cb.setVisibility(View.VISIBLE);
                iv.setVisibility(View.GONE);
                cb.setChecked(p.isSelect);
                final ProcessModel finalP = p;
                cb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (finalP.isSelect)
                            finalP.isSelect=false;
                        else
                            finalP.isSelect=true;
                    }
                });
            }
            else
            {
                cb.setVisibility(View.GONE);
                iv.setVisibility(View.VISIBLE);
                if (p.icon!=null)
                    iv.setImageDrawable(p.icon);
            }

            tv1.setText(p.processName);
            tv2.setText(p.name);
            return view;
        }
    }

    //绑定XML
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem mi=menu.findItem(R.id.action_edit);
        MenuItem mi2=menu.findItem(R.id.action_write);
        //判断列表是否长按
        if (IsListSelect)
        {
            mi2.setVisible(true);
            mi.setVisible(true);
        }
        else
        {
            mi2.setVisible(false);
            mi.setVisible(false);
        }
        return true;
    }

    //监听菜单点击
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this,RecyclerActivity.class));
            return true;
        }
        if (id == R.id.action_power_settings) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            List<String> str=new ArrayList<String>();
            for (int i=ta.getCount()-1;i>=0;i--)
            {
                if (ta.getItem(i).isSelect)
                {
                    str.add(ta.getItem(i).processName);
                    ta.remove(ta.getItem(i));
                }
            }
            new ThreadHelper(this).execute(str);
            ta.notifyDataSetChanged();
            return true;
        }
        if (id == R.id.action_write) {
            SPHelper sph=new SPHelper(this);
            File file=new File(getApplicationContext().getFilesDir(),"BlackList.txt");
            //file.delete();
            List<String> str=FileHelper.GetBlackListFile(file);
            if (str==null)
                str=new ArrayList<String>();
            try
            {
                for (int i=ta.getCount()-1;i>0;i--)
                {
                    Log.i("信息",String.valueOf(i));
                    ProcessModel p=ta.getItem(i);
                    if (p!=null&&p.isSelect)
                    {
                        str.add(p.processName);
                    }

                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            str=new ArrayList<String>(new HashSet<String>(str));
            FileHelper.SetBlackListFile(this,file,str);
            Toast.makeText(this,"黑名单数量:"+str.size(),Toast.LENGTH_SHORT);
            Log.i("信息","黑名单数量:"+str.size());
        }

        return super.onOptionsItemSelected(item);
    }


}
