package intermediatecontentprovider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import modules.History;
import modules.HistoryMeal;
import modules.Meal;

/**
 * Class used to connect with database of "coachnutrition.contentprovider" authority
 * by content resolver
 */
public class IntermediateCoachNutrition {
    public Context context;
    public ContentResolver contResolver;
    public static final String authority = "coachnutrition.contentprovider";

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

    public IntermediateCoachNutrition(Context context){
        this.context=context;
        contResolver=context.getContentResolver();
    }

    /**
     * Method to insert a new Meal on database
     * 
     * @param m 
     */
    public void insertLineMeal(Meal m) {
        try {
            ContentValues row = new ContentValues();
            row.put(COL_NAME, m.getNom());
            row.put(COL_CALORIE, m.getCalorie());

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("content").authority(authority).appendPath(TAB_MEAL).build();
            Uri uri = builder.build();
            Log.d("InsertLineMrals", "Uri=" + uri.toString());

            contResolver.insert(uri, row);
            Log.d("insertLineMeal", "row : "+row);

            notification(context, "Meal has been inserted", 0);
        } catch (SQLException e) {
            Log.d("insertLineMeal", "Erreur", e);
        }
    }

    /**
     * Method to insert a new History on database
     * 
     * @param h
     */
    public void insertLineHistory(History h) {
        try {
            ContentValues row = new ContentValues();
            row.put(COL_DATE, History.getDate(h.getDate()));
            row.put(COL_MIN, h.getMin());
            row.put(COL_MAX, h.getMax());
            row.put(COL_TOTAL_CALORIES_H, h.getTotalCal());
            row.put(COL_MARK, h.getMark());

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("content").authority(authority).appendPath(TAB_HISTORY).build();
            Uri uri = builder.build();
            Log.d("InsertLineHistory", "Uri=" + uri.toString());

            contResolver.insert(uri,row);
            Log.d("InsertLineHistory", "row : "+row);

            notification(context, "History has been inserted", 0);
        } catch (SQLException e) {
            Log.d("InsertLineHistory", "Erreur", e);
        }
    }

    /**
     * Method to insert a new row on pivot table "History_Meal"
     * 
     * @param hm
     */
    public void insertLineHistoryMeal(HistoryMeal hm) {
        try {
            ContentValues row = new ContentValues();
            row.put(COL_ID_MEAL, hm.getIdMeal());
            row.put(COL_ID_HISTORY, hm.getIdHistory());
            row.put(COL_QUANTITY, hm.getQuantity());
            row.put(COL_TOTAL_CALORIES_HM, hm.getTotalCaloriesHM());

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("content").authority(authority).appendPath(TAB_HISTORY_MEAL).build();
            Uri uri = builder.build();
            Log.d("InsertLineHistMeal", "Uri=" + uri.toString());

            contResolver.insert(uri,row);
            Log.d("InsertLineHistMeal", "row : "+row);

            notification(context, "Meal has been inserted on selected History", 0);
        } catch (SQLException e) {
            Log.d("InsertLineHistMeal", "Erreur", e);
        }
    }

    /**
     * Method to get a specified Meal by its Id
     * 
     * @param  id
     * @return
     */
    public Meal getMealById(int id) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath(TAB_MEAL).build();
        Uri uri = builder.build();
        Log.d("getMealById", "Uri=" + uri.toString());

        Meal m = null;

        String[] str_args = {COL_NAME, COL_CALORIE};
        String where = COL_ID_MEAL + " = ? ";
        String[] selection_argms = {String.valueOf(id)};

        Cursor cursor = contResolver.query(uri, str_args, where, selection_argms, null);
        Log.d("Cursor", "Query_Get_Meal, id:" + id);
        
        if (cursor.getCount() == 0) {
            Log.d("GetMealById","cursor is null");
        } else {
            cursor.moveToFirst();
            m = new Meal(
                id,
                cursor.getString(cursor.getColumnIndex(COL_NAME)),
                cursor.getFloat(cursor.getColumnIndex(COL_CALORIE))
            );
            Log.d("GetMealById", "" + m.toString());
        }
        cursor.close();

        return m;
    }

    /**
     * Method to get a specified History by its date
     * 
     * @param  date
     * @return
     */
    public History getHistoryByDate(String date) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath(TAB_HISTORY).build();
        Uri uri = builder.build();
        Log.d("GetHistoryByDate", "Uri=" + uri.toString());

        History h = null;

        String where = COL_DATE + " = ? ";
        String[] selection_argms = {date};
        Log.d("Cursor", "Query_Get_History, date:" + date);

        Cursor cursor = contResolver.query(uri,null, where, selection_argms, null);
        if (cursor.getCount() == 0) {
            Log.d("GetHistoryByDate","cursor is empty");
        } else {
            cursor.moveToFirst();
            h = new History(
                cursor.getInt(cursor.getColumnIndex(COL_ID_HISTORY)),
                History.setDate(date),
                cursor.getFloat(cursor.getColumnIndex(COL_MIN)),
                cursor.getFloat(cursor.getColumnIndex(COL_MAX)),
                cursor.getFloat(cursor.getColumnIndex(COL_TOTAL_CALORIES_H)),
                cursor.getFloat(cursor.getColumnIndex(COL_MARK))
            );
            Log.d("GetHistoryByDate", "" + h.toString());
        }
        cursor.close();

        return h;
    }

    /**
     * Method that check whether Meals exist on database or not
     *
     * @return
     */
    public boolean checkMeals(){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath(TAB_MEAL).build();
        Uri uri = builder.build();
        Log.d("checkMeals", "Uri=" + uri.toString());

        Cursor c = contResolver.query(uri, null, null, null, null);
        boolean check_meals = (c.getCount() != 0);

        c.close();
        return  check_meals;
    }

    /**
     * Method to check if specified Meal is related with any History
     * on pivot table "Histoy_Meal" by its id (Meal_id)
     * 
     * @param  idm 
     * @return     
     */
    public boolean checkHistoryMealByIDMeal(int idm) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath(TAB_HISTORY_MEAL).build();
        Uri uri = builder.build();
        Log.d("GetHistoryMealByIDH", "Uri=" + uri.toString());

        String where = COL_ID_MEAL + " = ? ";
        String[] selection_argms = {String.valueOf(idm)};
        Log.d("Cursor", "Query_Get_HistoryMeal by idm:" + idm);

        Cursor cursor = contResolver.query(uri, null, where, selection_argms, null);
        boolean b_check = (cursor.getCount() != 0);
        cursor.close();
        return b_check;
    }

    /**
     * Method to update Meal
     * 
     * @param m
     */
    public void updateMeal(Meal m){
        try {
            ContentValues row = new ContentValues();
            row.put(COL_NAME, m.getNom());
            row.put(COL_CALORIE, m.getCalorie());
            Log.d("Update_Meal", "values=" + row);

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("content").authority(authority).appendPath(TAB_MEAL).build();
            Uri uri = builder.build();
            Log.d("Update_Meal", "Uri=" + uri.toString());

            String where = COL_ID_MEAL + " = ? ";
            String[] selection_argms = {m.getId()+""};

            int count = contResolver.update(uri, row, where, selection_argms);
            Log.d("Update_Meal", "Meal(" + row + ")");

            notification(context, "Meal has been updated", 0);
        }catch (SQLException e) {
            Log.d("Update_Meal", "Erreur", e);
        }
    }

    /**
     * Method to update History
     * 
     * @param h
     */
    public void updateHitory(History h){
        try {
            ContentValues row = new ContentValues();
            row.put(COL_MIN, h.getMin());
            row.put(COL_MAX, h.getMax());
            row.put(COL_TOTAL_CALORIES_H, h.getTotalCal());
            row.put(COL_MARK, h.getMark());
            Log.d("Update_History", "values  " + row);

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("content").authority(authority).appendPath(TAB_HISTORY).build();
            Uri uri = builder.build();
            Log.d("Update_History", "Uri=" + uri.toString());

            String where = COL_ID_HISTORY + " = ? ";
            String[] selection_argms = {h.getId()+""};

            int count = contResolver.update(uri, row, where, selection_argms);
            Log.d("Update_History", "" + count);

            notification(context, "History has been updated", 0);
        }catch (SQLException e) {
            Log.d("Update_History", "Erreur", e);
        }
    }

    /**
     * Method to update pivot line
     * 
     * @param hm
     */
    public void updateHistoryMeal(HistoryMeal hm){
        try {
            ContentValues row = new ContentValues();
            row.put(COL_QUANTITY, hm.getQuantity());
            row.put(COL_TOTAL_CALORIES_HM, hm.getTotalCaloriesHM());
            Log.d("Update_Meal", "values=" + row);

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("content").authority(authority).appendPath(TAB_HISTORY_MEAL).build();
            Uri uri = builder.build();
            Log.d("Update_HistoryMeal", "Uri=" + uri.toString());

            String where = COL_ID_HISTORY + " = ?  and  " + COL_ID_MEAL + " = ? ";
            String[] selection_argms = {hm.getIdHistory()+"", hm.getIdMeal()+""};

            int count = contResolver.update(uri, row, where, selection_argms);
            Log.d("Update_HistoryMeal", "HistoryMeal(" + row + ")");

            notification(context, "Meal has been updated on selected History", 0);
        }catch (SQLException e) {
            Log.d("Update_HistoryMeal", "Erreur", e);
        }
    }

    /**
     * Method to delete specified Meal by its id
     * 
     * @param id
     */
    public void deleteMeal(int id){
        if(checkHistoryMealByIDMeal(id)){
            notification(context, "Delete impossible : the meal is related to a History !!", 1);
        }else{
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("content").authority(authority).appendPath(TAB_MEAL).build();
            Uri uri=builder.build();

            String selection = COL_ID_MEAL + " = ? ";
            String[] selection_argms = {id + ""};

            if(0 < contResolver.delete(uri, selection,selection_argms)){
                Log.d("Delete_Meal", "" + id + " is deleted from the liste of Meal");
                notification(context, "Meal has been deleted", 0);
            }else{
                Log.d("Delete_Meal", "Erreur");
            }
        }
    }

    /**
     * Method to delete pivot ligne on "History_Meal" by
     * both Meal_id and History_id
     * 
     * @param idm 
     * @param idh 
     */
    public void deleteMealHistoryMeal(int idm,int idh){
        Uri.Builder builder=new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath(TAB_HISTORY_MEAL).build();
        Uri uri=builder.build();

        String selection = COL_ID_MEAL + " = " + idm + " and " + COL_ID_HISTORY + " = " + idh;

        if(0 < contResolver.delete(uri, selection,null)){
            Log.d("Delete_HistoryMeal", "_idm "+idm+",_idh "+idh);
            notification(context, "Meal has been deleted from History", 0);
        }else{
            Log.d("Delete_HistoryMeal", "Erreur");
        }
    }

    /**
     * Method to get pivot line from "History_Meal" by
     * both Meal_id and History_id
     *  
     * @param  id_hist 
     * @param  id_meal 
     * @return         
     */
    public HistoryMeal getHistoryMeal(int id_hist, int id_meal){
        HistoryMeal hm = null;

        Uri.Builder builder=new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath(TAB_HISTORY_MEAL).build();
        Uri uri=builder.build();

        String selection = COL_ID_MEAL + " = ? and " + COL_ID_HISTORY + " = ?";
        String[] selection_argms = {id_meal+"", id_hist+""};

        Cursor cursor = contResolver.query(uri,null,selection, selection_argms, null);

        if (cursor.getCount() == 0) {
            Log.d("GetHistoryMeal","cursor is empty");
        } else {
            cursor.moveToFirst();
            hm = new HistoryMeal(
                cursor.getInt(cursor.getColumnIndex(COL_ID_MEAL)),
                cursor.getInt(cursor.getColumnIndex(COL_ID_HISTORY)),
                cursor.getFloat(cursor.getColumnIndex(COL_QUANTITY)),
                cursor.getFloat(cursor.getColumnIndex(COL_TOTAL_CALORIES_HM))
            );
        }
        cursor.close();

        return hm;
    }

    /**
     * Method to get all pivot lines from "History_Meal" 
     * for specified History selected by its id
     * 
     * @param  id_hist
     * @return        
     */
    public Cursor getHistoryMeal(int id_hist){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content")
                .authority(authority)
                .appendPath("all_tables");
        builder = ContentUris.appendId(builder, id_hist);
        Uri uri = builder.build();
        Log.d("uri=", uri.toString());

        return contResolver.query(uri, null,null, null, null);
    }

    /**
     * Method to get last 7 days (week) Histories,
     * ending by selected date
     * 
     * @param  date 
     * @return      
     */
    public Cursor getWeekHistory(String date){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content")
                .authority(authority)
                .appendPath(TAB_HISTORY);
        Uri uri=builder.build();

        Cursor cursor = contResolver.query(
            uri, 
            new String[]{"rowid as _id", COL_DATE, COL_MIN, COL_MAX, COL_TOTAL_CALORIES_H},
            COL_DATE + " <= '" + date + "'", 
            null, 
            COL_DATE + " DESC LIMIT 7"
        );

        return cursor;
    }

    /**
     * Method to get last History
     * 
     * @return
     */
    public History getLastHistory(){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content")
                .authority(authority)
                .appendPath(TAB_HISTORY);
        Uri uri = builder.build();

        Cursor cursor = contResolver.query(uri, null, null, null, COL_DATE + " DESC LIMIT 1");
        History h = null;
        if(cursor.getCount() != 0 ) {
            cursor.moveToFirst();
            h = new History(
                    cursor.getInt(cursor.getColumnIndex(COL_ID_HISTORY)),
                    History.setDate(cursor.getString(cursor.getColumnIndex(COL_DATE))),
                    cursor.getFloat(cursor.getColumnIndex(COL_MIN)),
                    cursor.getFloat(cursor.getColumnIndex(COL_MAX)),
                    cursor.getFloat(cursor.getColumnIndex(COL_TOTAL_CALORIES_H)),
                    cursor.getFloat(cursor.getColumnIndex(COL_MARK))
            );
        }
        cursor.close();

        return h;
    }

    /**
     * Method to help pop up notification for user
     * 
     * @param message 
     * @param time    
     */
    public static void notification(Context context, String message, int time){
        Toast.makeText(
            context,
            message, 
            (time == 1) ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT
        ).show();
    }
}