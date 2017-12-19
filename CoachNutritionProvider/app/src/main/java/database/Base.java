package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Group K
 * 
 * @author Slimane SIROUKANE
 * @author Fatima CHIKH
 * 
 * Base of Coach-Nutrition App
 */
public class Base extends SQLiteOpenHelper {
    /* BASE DE DONNEE NUTRITION*/
    public final static int VERSION = 1;
    public final static String DB_NAME = "coach_nutrition";

    /*TABLE MEAL*/
    public final static String TAB_MEAL = "meal";
    public final static String COL_ID_MEAL = "id_meal";
    public final static String COL_NAME = "name";
    public final static String COL_CALORIE= "calorie";

    /*TABLE HISTORY*/
    public final static String TAB_HISTORY = "history";
    public final static String COL_ID_HISTORY= "id_history";
    public final static String COL_DATE = "date";
    public final static String COL_MAX = "max";
    public final static String COL_MIN = "min";
    public final static String COL_TOTAL_CALORIES_H = "total_calorie_h";
    public final static String COL_MARK = "mark";

    /*TABLE HISTORY_MEAL*/
    public final static String TAB_HISTORY_MEAL= "history_meal";
    public final static String COL_QUANTITY = "quantity";
    public final static String COL_TOTAL_CALORIES_HM= "total_calorie_hm";

    public final static String CREATE_MEAL = " create TABLE " + TAB_MEAL + "(" +
            COL_ID_MEAL +  " integer primary key, "  +
            COL_NAME +    " string, "   +
            COL_CALORIE +   " real "   +  ");" ;

    public final static String CREATE_HISTORY = "create TABLE " + TAB_HISTORY + "(" +
            COL_ID_HISTORY + " integer primary key, " +
            COL_DATE +   " string  , "    +
            COL_MIN+     " real    , "    +
            COL_MAX +    " real    , "    +
            COL_TOTAL_CALORIES_H + "  real  ,"  + 
            COL_MARK +    " real "    +  ");" ;

    public final static String CREATE_HISTORYMEAL= "create TABLE " + TAB_HISTORY_MEAL+ "(" +
            COL_ID_MEAL + "  integer  references " + TAB_MEAL + ", " +
            COL_ID_HISTORY + " integer  references " + TAB_HISTORY + "," +
            COL_QUANTITY +   "   real  ,  " +
            COL_TOTAL_CALORIES_HM + "  real , " +
            " primary key( " + COL_ID_MEAL + ", " + COL_ID_HISTORY + " )); ";

    private static Base ourInstance;

    public static Base getInstance(Context context) {
        if (ourInstance == null)
            ourInstance = new Base(context);
        return ourInstance;
    }

    public Base(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MEAL);
        db.execSQL(CREATE_HISTORY);
        db.execSQL(CREATE_HISTORYMEAL);
        Log.d("Create Base", " Done");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL(" drop TABLE if exists " + TAB_MEAL);
            db.execSQL(" drop TABLE if exists  " + TAB_HISTORY);
            db.execSQL(" drop TABLE if exists  " + TAB_HISTORY_MEAL);
            Log.d("Update Base", "Done");
            onCreate(db);
        }
    }
}