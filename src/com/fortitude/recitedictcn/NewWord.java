/* -*- coding: utf-8 -*-
 *
 * Copyright (C) 2011-2011 fortitude.zhang
 * 
 * Author: fortitude.zhang@gmail.com
 */

/* 本文件用于维护添加新词的activity */
package com.fortitude.recitedictcn;

import java.util.List;
import java.util.ArrayList;
import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.database.Cursor;
import android.util.Log;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import android.os.Environment;
import android.util.Log;
import android.content.res.AssetManager;
import android.content.Context;
import java.io.IOException;

import com.fortitude.recitedictcn.DataBase;
import com.fortitude.recitedictcn.QueryWord;

public class NewWord extends Activity {
    DataBase db;
    QueryWord queryWord;
    EditText wordTv;
    EditText familiarTv;
    RadioGroup queryMethodRG;

    private OnClickListener buttonListener = new OnClickListener() {
            public void onClick(View v) {
                String word = wordTv.getText().toString();
                String familiar = familiarTv.getText().toString();
                int queryMethod = queryMethodRG.getCheckedRadioButtonId();
                String queryResult;

                Integer nFamiliar = Integer.parseInt(familiar);
                if ((1 > nFamiliar) || (5 < nFamiliar)) {
                    Toast t = Toast.makeText(NewWord.this, "兄弟，不带你这么玩的，熟悉度只支持1~5", Toast.LENGTH_SHORT);
                    t.show();
                    return;
                }

                /* dispatch user query */
                if (queryMethod == R.id.localQuery) {
                    queryResult = queryWord.QueryWordWithChoice(word, false);
                }
                else {
                    queryResult = queryWord.QueryWordWithChoice(word, true);                    
                }

                if (0 == queryResult.length()) {
                    Toast t = Toast.makeText(NewWord.this, "Query Failed", Toast.LENGTH_SHORT);
                    t.show();
                    return;                    
                }

                /* store the new word into db */
                db.insertWord(word, nFamiliar, queryResult);
                db.close();

                /* set activity result */
                NewWord.this.setResult(0);
                finish();
            }
        };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_word);

        /* 创建database实例，并打开数据库，等待用户查询结果写入*/
        db = new DataBase(this);
        db.open();

        /* When run firstly,copy asset dict file to sdcard 
         * FIXME: check return code
         */
        if (!copyAssets())
        {
            /* FIXME: Failed, alert user */
            Toast t = Toast.makeText(NewWord.this, "拷贝字典数据至sdcard失败，请重试!", Toast.LENGTH_SHORT);
            t.show();
            return;
        }

        queryWord = new QueryWord();

        Button button = (Button)findViewById(R.id.addNewButton);
        button.setOnClickListener(buttonListener);

        wordTv = (EditText)findViewById(R.id.wordNameInput);
        familiarTv = (EditText)findViewById(R.id.wordFamiliarityInput);
        queryMethodRG = (RadioGroup)findViewById(R.id.queryMethodRG);

        /* use local query as default */
        queryMethodRG.check(R.id.localQuery);
    }

    private boolean copyAssets() {
        AssetManager assetManager = getAssets();

        /* FIXME:
         * 1. create a thread to do such work.
         * 2. to be user-friendly.
         * 3. using API 10 to get sd card state,and create directory automatically instead of static specified.
         */
        try { 
            /* Instantiate an AM and store a list files under /assets/bin 
             *  in an array. 
             */ 
            String[] sourceFiles = assetManager.list("dict"); 
            for (String src : sourceFiles) { 
                File targetFile = new File("/mnt/sdcard"+"/recitedictcn", src); 
                /* If the target file does not exist, create a copy from the 
                 *  bundled asset. 
                 */ 
                if (!targetFile.exists()) { 
                    BufferedOutputStream out = new BufferedOutputStream(new 
                                                                        FileOutputStream(targetFile)); 
                    BufferedInputStream in = new 
                        BufferedInputStream(assetManager.open("dict/" + src, AssetManager.ACCESS_STREAMING)); 
                    int len; 
                    byte[] buf = new byte[1024]; 
                    while((len = in.read(buf)) != -1) { 
                        out.write(buf, 0, len); 
                    } 
                    Log.i("[COPY_ASSET]", "Wrote file " + targetFile.toString()); 
                    in.close(); 
                    out.close(); 
                } else { 
                    Log.i("[COPY_ASSET]", src + ": File already exists. Nothing to be done."); 
                } 
            } 
        } catch (IOException io) { 
            Log.e("[COPY_ASSET]", "Error! Install failed."); 
            Log.e("[COPY_ASSET]", io.getMessage()); 
            return false;
        } catch (Exception ex) { 
            Log.e("[COPY_ASSET]", "Error! Install failed."); 
            Log.e("[COPY_ASSET]", ex.getMessage()); 
            return false;
        } 

        return true;
    }

}
