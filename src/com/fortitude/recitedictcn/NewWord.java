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

public class NewWord extends Activity {
    DataBase db;
    EditText wordTv;
    EditText familiarTv;

    private OnClickListener buttonListener = new OnClickListener() {
            public void onClick(View v) {
                String word = wordTv.getText().toString();
                String familiar = familiarTv.getText().toString();

                Integer nFamiliar = Integer.parseInt(familiar);
                if ((1 > nFamiliar) || (5 < nFamiliar)) {
                    Toast t = Toast.makeText(NewWord.this, "兄弟，不带你这么玩的，熟悉度只支持1~5", Toast.LENGTH_SHORT);
                    t.show();
                    return;
                }
                
                /* TODO: 调用QueryWord类实现单词释义查询，通过判断查询结果是否为空串给出提示信息 */
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

        Button button = (Button)findViewById(R.id.AddNewButton);
        button.setOnClickListener(buttonListener);

        wordTv = (EditText)findViewById(R.id.WordNameInput);
        familiarTv = (EditText)findViewById(R.id.WordFamiliarityInput);
    }


}
