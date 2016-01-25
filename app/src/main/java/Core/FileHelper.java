package Core;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tanch on 2016/1/11.
 */
public class FileHelper {
    public static List<String> GetBlackListFile(File file)
    {
        if (file.exists())
        {
            try {
                FileInputStream fis=new FileInputStream(file);
                byte[] buf=new byte[fis.available()];
                fis.read(buf);
                String str=new String(buf,"UTF-8");
                fis.close();
                if (!str.isEmpty())
                {
                    String[] list=str.split("[|]");
                    return new ArrayList<String>(Arrays.asList(list));
                }
                return null;
            }
            catch (Exception e)
            {
                return null;
            }
        }
        return null;
    }

    public static void SetBlackListFile(Context context, File file, List<String> Lstr)
    {
        try
        {
            FileOutputStream fos=context.openFileOutput(file.getName(),Context.MODE_PRIVATE);
            StringBuffer sb=new StringBuffer();
            for (String s:Lstr)
            {
                sb.append(s);
                sb.append("|");
            }
            fos.write(sb.substring(0,sb.length()-1).getBytes());
            fos.close();
        }
        catch (Exception e)
        {

        }
    }
}
