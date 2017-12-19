package groupk.contentprovider;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import database.Base;

/**
 * Content Provider of Base (Coach-Nutrition App)
 */
public class MyContentProvider extends ContentProvider {
    private static final String authority="coachnutrition.contentprovider";
    private Base base;

    //Test LOG
    private static final String LOG_TAG = "coachnutrition_contentprovider";
    
    //UriMatcher
    private static final int CODE_MEAL = 1;
    private static final int CODE_HISTORY = 2;
    private static final int CODE_HISTORY_MEAL = 3;
    private static final int CODE_ALL_TABLES = 4;
    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        matcher.addURI(authority,Base.TAB_MEAL,CODE_MEAL);
        matcher.addURI(authority,Base.TAB_HISTORY,CODE_HISTORY);
        matcher.addURI(authority,Base.TAB_HISTORY_MEAL,CODE_HISTORY_MEAL);
        matcher.addURI(authority,"all_tables/#",CODE_ALL_TABLES);
    }

    public MyContentProvider() {
    }

    @SuppressLint("LongLogTag")
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = base.getWritableDatabase();
        int code  = matcher.match(uri);
        Log.d(LOG_TAG, "Uri=" + uri.toString());
        int count = 0;
        switch (code){
            case CODE_MEAL:
                count = database.delete(Base.TAB_MEAL, selection, selectionArgs);
                Log.d(LOG_TAG,"delete meal"+count);
                break;
            case CODE_HISTORY_MEAL:
                count = database.delete(Base.TAB_HISTORY_MEAL, selection, selectionArgs);
                Log.d(LOG_TAG,"delete history_meals"+count);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        if(count>0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @SuppressLint("LongLogTag")
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase database = base.getWritableDatabase();
        int code = matcher.match(uri);
        Log.d(LOG_TAG, "Uri=" + uri.toString());
        String  path;
        long id;
        Log.d(LOG_TAG,"code="+code);
        switch(code){
            case CODE_MEAL:
                id = database.insertOrThrow(Base.TAB_MEAL,null,values);
                path = Base.TAB_MEAL;
                Log.d("Path: meals","id="+id);
                break;
            case CODE_HISTORY:
                id = database.insertOrThrow(Base.TAB_HISTORY,null,values);
                path = Base.TAB_HISTORY;
                Log.d("Path: history","id="+id);
                break;
            case CODE_HISTORY_MEAL:
                id = database.insertOrThrow(Base.TAB_HISTORY_MEAL,null,values);
                path = Base.TAB_HISTORY_MEAL;
                Log.d("Path: history_meals","id="+id);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        Uri.Builder builder=(new Uri.Builder()).authority(authority).appendPath(path);
        return ContentUris.appendId(builder,id).build();
    }

    @Override
    public boolean onCreate() {
        base = new Base(getContext());
        return true;
    }

    @SuppressLint("LongLogTag")
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = base.getReadableDatabase();
        int code = matcher.match(uri);
        Log.d(LOG_TAG, "Uri=" + uri.toString());
        Cursor curseur;
        String  path;
        switch(code){
            case CODE_MEAL:
                curseur = database.query(Base.TAB_MEAL, projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case CODE_HISTORY:
                curseur = database.query(Base.TAB_HISTORY, projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case CODE_HISTORY_MEAL:
                curseur = database.query(Base.TAB_HISTORY_MEAL, projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case CODE_ALL_TABLES:
                long id = ContentUris.parseId(uri);
                curseur = database.rawQuery(
                    " SELECT " +
                        Base.TAB_MEAL + "." + Base.COL_ID_MEAL + " as _id , "+
                        Base.COL_QUANTITY  + " , " +
                        Base.COL_NAME +
                    " FROM " +
                        Base.TAB_HISTORY_MEAL + " , " + Base.TAB_MEAL +
                    " WHERE " +
                        Base.TAB_HISTORY_MEAL + "." + Base.COL_ID_HISTORY + " = " + (int) id +
                        " AND " +
                        Base.TAB_HISTORY_MEAL + "." + Base.COL_ID_MEAL + " = " + Base.TAB_MEAL + "." + Base.COL_ID_MEAL +
                    " ORDER BY " +
                            Base.COL_NAME,
                    null
                );
                break;
            default:
                Log.d(LOG_TAG, "default, Uri=" + uri.toString());
                throw new UnsupportedOperationException("Not yet implemented");
        }
        curseur.setNotificationUri(getContext().getContentResolver(), uri);
        return curseur;
    }

    @SuppressLint("LongLogTag")
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d("content_provider","update");
        SQLiteDatabase database = base.getWritableDatabase();
        int code = matcher.match(uri);
        Log.d(LOG_TAG, "Uri=" + uri.toString());
        int count = 0;
        Log.d("Content_Provider","update_code"+code);
        switch (code) {
            case CODE_MEAL:
                count = database.update(Base.TAB_MEAL, values, selection, selectionArgs);
                break;
            case CODE_HISTORY:
                count = database.update(Base.TAB_HISTORY, values, selection, selectionArgs);
                break;
            case CODE_HISTORY_MEAL:
                count = database.update(Base.TAB_HISTORY_MEAL, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(LOG_TAG,"update_count= "+count);
        return count;
    }
}
