package com.iopatterns.popularmovies.movieDataSystem;
/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.iopatterns.popularmovies.movieDataSystem.DataBaseContract.FavouriteEntry;

//package com.example.android.todolist.data;
//import com.example.android.todolist.data.TaskContract.TaskEntry;


public class DataBaseHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "favouriteFilmsDB.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 1;


    // Constructor
    DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    /**
     * Called when the tasks database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create tasks table (careful to follow SQL formatting rules)
        final String CREATE_TABLE = "CREATE TABLE "  + FavouriteEntry.TABLE_NAME + " (" +
                FavouriteEntry._ID                + " INTEGER PRIMARY KEY, " +
                FavouriteEntry.COLUMN_MOVIE_ID    + " TEXT NOT NULL, " +
                FavouriteEntry.COLUMN_JPG_URL     + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }


    /**
     * This method discards the old table of data and calls onCreate to recreate a new one.
     * This only occurs when the version number for this database (DATABASE_VERSION) is incremented.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavouriteEntry.TABLE_NAME);
        onCreate(db);
    }
}