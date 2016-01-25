package Core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tanch on 2016/1/11.
 */
public class SqlHelper extends SQLiteOpenHelper {

    //CREATE TABLE 数据表名称(字段1 类型1(长度),字段2 类型2(长度) …… )
    public static final String TABLE_NAME = "KillerDb";

    final private String CREATE_TABLE_SQL ="CREATE TABLE " + TABLE_NAME + " (" +
            "_id integer primary key," +
            "processName varchar," +
            ")";

    public SqlHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
