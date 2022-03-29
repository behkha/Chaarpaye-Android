package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import event.Event;
import place.Place;

/**
 * Created by User on 3/22/2018.
 */

public class DatabaseController extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "BOOKMARKS";
    private final static int DATABASE_VERSION = 1;
    private final String PLACE_TABLE = "BOOKMARKED_PLACES";
    private final String EVENT_TABLE = "BOOKMARKED_EVENTS";
    private final String PLACE_ID = "place_id";
    private final String EVENT_ID = "event_id";
    private final String COLLECTION_ID = "collection_id";

    public DatabaseController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_BOOKMARKED_PLACE_TABLE = "CREATE TABLE " + PLACE_TABLE + "("
                + PLACE_ID + " INTEGER,"
                + COLLECTION_ID + " INTEGER)";
        sqLiteDatabase.execSQL(CREATE_BOOKMARKED_PLACE_TABLE);

        String CREATE_BOOKMARKED_EVENT_TABLE = "CREATE TABLE " + EVENT_TABLE + "("
                + EVENT_ID + " INTEGER,"
                + COLLECTION_ID + " INTEGER)";
        sqLiteDatabase.execSQL(CREATE_BOOKMARKED_EVENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PLACE_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EVENT_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void removeAllPlaces(){
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + PLACE_TABLE;
        db.execSQL(deleteQuery);
        db.close();
    }
    public void removeAllEvents(){
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + EVENT_TABLE;
        db.execSQL(deleteQuery);
        db.close();
    }

    public void addPlace(String place_id , String collection_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PLACE_ID,place_id);
        cv.put(COLLECTION_ID,collection_id);
        db.insert( PLACE_TABLE , null , cv );
        db.close();
    }
    public void removePlace(String place_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete( PLACE_TABLE , PLACE_ID + " = ?" , new String[]{place_id} );
        db.close();
    }
    public void removePlace(String place_id , String collection_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete( PLACE_TABLE , PLACE_ID + " = ?" + " AND " + COLLECTION_ID + " = ?" , new String[]{place_id,collection_id} );
        db.close();
    }
    public boolean hasPlace(String place_id){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + PLACE_TABLE + " WHERE " + PLACE_ID + " = " + place_id;
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
    public boolean hasPlace(String place_id , String collection_id){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + PLACE_TABLE + " WHERE " + PLACE_ID + " = " + place_id +
                " AND " + COLLECTION_ID + " = " + collection_id;
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public void addEvent(String event_id , String collection_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(EVENT_ID , event_id);
        cv.put(COLLECTION_ID,collection_id);
        db.insert( EVENT_TABLE , null , cv );
        db.close();
;    }
    public void removeEvent(String event_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete( EVENT_TABLE , EVENT_ID + " = ?" , new String[]{event_id} );
        db.close();
    }
    public void removeEvent(String event_id , String collection_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(EVENT_TABLE , EVENT_ID + " = ?" + " AND " + COLLECTION_ID + " = ?" , new String[]{event_id,collection_id});
        db.close();
    }
    public boolean hasEvent(String event_id){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + EVENT_TABLE + " WHERE " + EVENT_ID + " = " + event_id;
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
    public boolean hasEvent(String event_id , String collection_id){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + EVENT_TABLE + " WHERE " + EVENT_ID + " = " + event_id +
                " AND " + COLLECTION_ID + " = " + collection_id;
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public void removeCollection(String collection_id){
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + PLACE_TABLE + " WHERE " + COLLECTION_ID + " = " + collection_id;
        db.execSQL(deleteQuery);
        deleteQuery = "DELETE FROM " + EVENT_TABLE + " WHERE " + COLLECTION_ID + " = " + collection_id;
        db.execSQL(deleteQuery);
        db.close();
    }
}
