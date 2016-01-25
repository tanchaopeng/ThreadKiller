package Core;

import android.graphics.drawable.Drawable;

/**
 * Created by tanch on 2016/1/8.
 */
public class ProcessModel {
    //最后 使用时间
    public long lastTime;
    //进程所在的用户ID
    public int uid;
    //进程名
    public String processName;
    //图标
    public Drawable icon;
    //程序名
    public String name;
    //是否被选中
    public boolean isSelect;
    public ProcessModel()
    {
        this.isSelect=false;
    }
}