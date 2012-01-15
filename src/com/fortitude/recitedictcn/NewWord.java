/* -*- coding: utf-8 -*-
 *
 * Copyright (C) 2011-2011 fortitude.zhang
 * 
 * Author: fortitude.zhang@gmail.com
 */

// Maintain new word insert operation.
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
                Toast t = Toast.makeText(NewWord.this, "抱歉，熟悉度只支持1~5，请修改", Toast.LENGTH_SHORT);
                t.show();
                db.close();
                finish();
                return;
            }

            // Check local dictionary prsent if user choose local dictionary.
            if (queryMethod == R.id.localQuery) {
                if (!checkLocalDictPresent()) {
                    Toast t = Toast.makeText(NewWord.this, "注意，本地词典不存在，请查看软件的帮助文档安装本地词典后重试选择本地查询方式", Toast.LENGTH_SHORT);
                    t.show();
                    db.close();
                    finish();
                    return;
                }

                queryResult = queryWord.QueryWordWithChoice(word, false);
            }
            else {
                queryResult = queryWord.QueryWordWithChoice(word, true);                    
            }

            if (0 == queryResult.length()) {
                Toast t = Toast.makeText(NewWord.this, "抱歉，查询失败", Toast.LENGTH_SHORT);
                t.show();
                db.close();
                finish();
                return;                    
            }

            // Store new word
            db.insertWord(word, nFamiliar, queryResult);
            db.close();

            NewWord.this.setResult(0);
            finish();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_word);

        // Create and Open db instance,wait write query result back.
        db = new DataBase(this);
        db.open();

        queryWord = new QueryWord();

        Button button = (Button)findViewById(R.id.addNewButton);
        button.setOnClickListener(buttonListener);

        wordTv = (EditText)findViewById(R.id.wordNameInput);
        familiarTv = (EditText)findViewById(R.id.wordFamiliarityInput);
        queryMethodRG = (RadioGroup)findViewById(R.id.queryMethodRG);

        // Local query as default.
        queryMethodRG.check(R.id.localQuery);
    }

    private boolean checkLocalDictPresent() {
        // AssetManager assetManager = getAssets();

        // /* FIXME:
        //  * 1. create a thread to do such work.
        //  * 2. to be user-friendly.
        //  * 3. using API 10 to get sd card state,and create directory automatically instead of static specified.
        //  */
        // try { 
        //     /* Instantiate an AM and store a list files under /assets/bin 
        //      *  in an array. 
        //      */ 
        //     String[] sourceFiles = assetManager.list("dict"); 
        //     for (String src : sourceFiles) { 
        //         File targetFile = new File("/mnt/sdcard"+"/recitedictcn", src); 
        //         /* If the target file does not exist, create a copy from the 
        //          *  bundled asset. 
        //          */ 
        //         if (!targetFile.exists()) { 
        //             BufferedOutputStream out = new BufferedOutputStream(new 
        //                                                                 FileOutputStream(targetFile)); 
        //             BufferedInputStream in = new 
        //                 BufferedInputStream(assetManager.open("dict/" + src, AssetManager.ACCESS_STREAMING)); 
        //             int len; 
        //             byte[] buf = new byte[1024]; 
        //             while((len = in.read(buf)) != -1) { 
        //                 out.write(buf, 0, len); 
        //             } 
        //             Log.i("[COPY_ASSET]", "Wrote file " + targetFile.toString()); 
        //             in.close(); 
        //             out.close(); 
        //         } else { 
        //             Log.i("[COPY_ASSET]", src + ": File already exists. Nothing to be done."); 
        //         } 
        //     } 
        // } catch (IOException io) { 
        //     Log.e("[COPY_ASSET]", "Error! Install failed."); 
        //     Log.e("[COPY_ASSET]", io.getMessage()); 
        //     return false;
        // } catch (Exception ex) { 
        //     Log.e("[COPY_ASSET]", "Error! Install failed."); 
        //     Log.e("[COPY_ASSET]", ex.getMessage()); 
        //     return false;
        // } 

        // return true;

        // Only check the target directory is present or not,if not present,
        // let user download the dict from internet manually.
        File targetFile = new File("/mnt/sdcard/recitedictcn/"); 
        return targetFile.exists();
    }
}
