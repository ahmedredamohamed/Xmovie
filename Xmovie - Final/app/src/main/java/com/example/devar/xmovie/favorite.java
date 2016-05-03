package com.example.devar.xmovie;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class favorite extends SQLiteOpenHelper {
        static String db_name="favorite";
        SQLiteDatabase db;
        public favorite(Context context) {
            super(context, db_name, null, 4);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table favorite(id int primary key,film_id int,poster text,title text,overview text,vote_average real,releasedate text,background text)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("Drop table if exists favorite");
            onCreate(db);
        }
        public void add(Movie item)
        {
            ContentValues content=new ContentValues();
            content.put("film_id",item.id);
            content.put("poster",item.getPoster());
            content.put("title",item.getTitle());
            content.put("overview",item.getOverview());
            content.put("vote_average",item.getRating());
            content.put("releasedate",item.getDate());
            content.put("background",item.getBackground());
            db=getWritableDatabase();
            db.insert("favorite", null, content);
            db.close();

        }
        public Cursor Fetch_all()
        {
            db=getReadableDatabase();
            Cursor cursor=db.rawQuery("select * from favorite",null);
            if(cursor.getCount()!=0)
            {
                cursor.moveToFirst();

            }
            else
            {
                cursor=null;
            }
            db.close();
            return cursor;
        }
    public Boolean ifexist(String title)
    {
        db=getReadableDatabase();
        Cursor cursor=db.rawQuery("select * from favorite where title=?", new String[]{title});
        if(cursor.getCount()!=0) {
            db.close();
            return true;
        }
        return false;
    }
}
