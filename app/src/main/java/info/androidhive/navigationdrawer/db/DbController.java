package info.androidhive.navigationdrawer.db;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.widget.Toast;


public class DbController extends SQLiteOpenHelper {


    public static final String dbFileFinder = "dbFileFinder"; // Dtabasename


    public static final String tblAccessedPaths = "tblAccessedPaths";// tablename

    public static final String fileId = "fileId"; // auto generated ID column
    public static final String filePath = "filePath";//the text insides the sticky
//    public static final String noteColor = "noteColor";//the color of the sticky
//    public static final String noteTimeHours = "noteTimeHours";//the time Hours of the sticky
//    public static final String noteTimeMinutes = "noteTimeMinutes";//the time Minutes of the sticky
//    public static final String noteTimePmOrAm = "noteTimePmOrAm";//the formt of the sticky
//    public static final String noteFontSize = "noteFontSize";//the font size  of the sticky

    public static final int versioncode = 2; // versioncode of the database start from >= 1


//    String[] statements = new String[]{
//
//    };
//    String[] tableNames = new String[]{tblNotes};


    // constructor
    public DbController(Context context) {
        super(context, dbFileFinder, null, versioncode);

    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(//Personal  Details Table
                "CREATE TABLE IF NOT EXISTS "
                        + tblAccessedPaths + "(" +
                        this.fileId + " integer primary key, " +
                        this.filePath + " text " +
//                        this.noteColor + " text, " +
//                        this.noteTimeMinutes + " text, " +
//                        this.noteTimeHours + " text, " +
//                        this.noteTimePmOrAm + " text, " +
//                        this.noteFontSize + " text " +

                        ")"
        );

    }


//    public long insertDataInDb(String noteBody, String noteColor, String noteDate, String noteFontSize) {
//        long l = 0;
//
//        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(this.noteBody, noteBody);
//        contentValues.put(this.noteColor, noteColor);
//        contentValues.put(this.noteTimePmOrAm, no);
//        contentValues.put(this.noteTimeMinutes noteDate);
//        contentValues.put(this.noteTimeHours, noteDate);
//        contentValues.put(this.noteFontSize, noteFontSize);
//        l = sqLiteDatabase.insert(this.tblNotes, null, contentValues);
//
//        return l;
//    }


    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        database.execSQL("DROP TABLE IF EXISTS " + tblAccessedPaths);
        onCreate(database);

    }


    public boolean exeQuery(String sql) {
        try {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            sqLiteDatabase.execSQL(sql);
            return true;
        } catch (SQLException e) {
            return false;

        }

    }


    public ArrayList<HashMap<String, String>> getData(String selectQuery) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(this.fileId, cursor.getString(0));
                map.put(this.filePath, cursor.getString(1));
//                map.put(this.noteColor, cursor.getString(2));
//                map.put(this.noteTimeMinutes, cursor.getString(3));
//                map.put(this.noteTimeHours, cursor.getString(4));
//                map.put(this.noteTimePmOrAm, cursor.getString(5));
//                map.put(this.noteFontSize, cursor.getString(6));
                arrayList.add(map);
            } while (cursor.moveToNext());
        }
        // return contact list
        return arrayList;
    }


}



