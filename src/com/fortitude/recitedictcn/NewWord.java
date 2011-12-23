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

        queryWord = new QueryWord();

        Button button = (Button)findViewById(R.id.addNewButton);
        button.setOnClickListener(buttonListener);

        wordTv = (EditText)findViewById(R.id.wordNameInput);
        familiarTv = (EditText)findViewById(R.id.wordFamiliarityInput);
        queryMethodRG = (RadioGroup)findViewById(R.id.queryMethodRG);

        /* use local query as default */
        queryMethodRG.check(R.id.localQuery);
    }
}
