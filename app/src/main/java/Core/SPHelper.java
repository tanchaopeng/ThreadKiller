package Core;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * 配置工具类
 * Created by YouMo on 2015/12/29.
 */
public class SPHelper {
    private String SpName = "SevenConfig";
    private Context SpContent;
    private SharedPreferences Sp;
    private SharedPreferences.Editor SpEditor;

    /**
     * 初始化SharedPreferences帮助类
     * @param content   Content对象
     */
    public SPHelper(Context content) {
        SpContent = content;
        Sp =SpContent.getSharedPreferences(SpName,Context.MODE_PRIVATE);
        SpEditor= Sp.edit();
    }
    /**
    * 初始化SharedPreferences帮助类
    * @param content   Content对象
    * @param FileName  文件保存名称
    */
    public SPHelper(Context content, String FileName) {
        SpContent = content;
        SpName = FileName;
        Sp =SpContent.getSharedPreferences(SpName,Context.MODE_PRIVATE);
        SpEditor= Sp.edit();
    }

    /**
     * 查询某个key是否已经存在
     * @param Key 检测键
     */
    public boolean Contains(String Key)
    {
        return Sp.contains(Key);
    }

    /**
     * 移除某个key值已经对应的值
     * @param Key 移除键
     */
    public void remove(String Key)
    {
        SpEditor.remove(Key);
    }

    /**
     * 保存
     * @param key   保存键
     * @param value 保存值
     */
    public  void Save(String key, Object value)
    {
        if (value instanceof String)
        {
            SpEditor.putString(key, (String) value);
        } else if (value instanceof Integer)
        {
            SpEditor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean)
        {
            SpEditor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float)
        {
            SpEditor.putFloat(key, (Float) value);
        } else if (value instanceof Long)
        {
            SpEditor.putLong(key, (Long) value);
        } else
        {
            SpEditor.putString(key, value.toString());
        }
        //卡UI
        //SpEditor.apply();
        SpEditor.apply();

    }

    /**
     * 读取
     * @param Key   保存键
     * @param OutObj 取出值
     */
    public void Read(String Key,Object OutObj)
    {
        if (OutObj instanceof String)
        {
            OutObj= Sp.getString(Key, (String) OutObj);
        } else if (OutObj instanceof Integer)
        {
            OutObj= Sp.getInt(Key, (Integer) OutObj);
        } else if (OutObj instanceof Boolean)
        {
            OutObj= Sp.getBoolean(Key, (Boolean) OutObj);
        } else if (OutObj instanceof Float)
        {
            OutObj= Sp.getFloat(Key, (Float) OutObj);
        } else if (OutObj instanceof Long)
        {
            OutObj= Sp.getLong(Key, (Long) OutObj);
        }
        OutObj=null;
    }

    /**
     * 返回所有的键值对
     */
    public Map<String, ?> GetAll()
    {
        return Sp.getAll();
    }

    /**
     * 清除所有数据
     */
    public void clear()
    {
        SpEditor.clear();
    }


}
