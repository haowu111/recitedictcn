/* -*- coding: utf-8 -*-
 *
 * Copyright (C) 2011-2011 fortitude.zhang
 * 
 * Author: fortitude.zhang@gmail.com
 */

/* 基本界面显示activity，用于显示BANNER以及字典 */
package com.fortitude.recitedictcn;

import java.util.List;
import java.util.ArrayList;
import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.database.Cursor;
import android.util.Log;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnLongClickListener;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;

import com.fortitude.recitedictcn.DataBase;
import com.fortitude.recitedictcn.NewWord;
import com.fortitude.recitedictcn.ActionItem;
import com.fortitude.recitedictcn.QuickAction;

public class MainDisplay extends Activity 
{
    ListView innerLv;
    DataBase db;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		innerLv = (ListView)findViewById(R.id.MainDisplayList);
		// Once user begin typing, filter the list item.
		innerLv.setTextFilterEnabled(true);

		innerLv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
					// TODO: design a better icon(current icons are not correct).
					ActionItem viewWordItem = new ActionItem(0, "查看单词", getResources().getDrawable(R.drawable.ic_up));
					ActionItem decFamiItem = new ActionItem(1, "减少熟悉度", getResources().getDrawable(R.drawable.ic_up));
					ActionItem incFamiItem 	= new ActionItem(2, "增加熟悉度", getResources().getDrawable(R.drawable.ic_up));
					ActionItem delWordItem = new ActionItem(3, "删除单词", getResources().getDrawable(R.drawable.ic_up));	       

					final QuickAction mQuickAction = new QuickAction(getApplicationContext());

					mQuickAction.addActionItem(viewWordItem);
					mQuickAction.addActionItem(decFamiItem);
					mQuickAction.addActionItem(incFamiItem);		    
					mQuickAction.addActionItem(delWordItem);

					mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
							@Override
							public void onItemClick(QuickAction quickAction, int pos, int actionId) {
								// ActionItem actionItem = quickAction.getActionItem(pos);
								String selected = (String)innerLv.getItemAtPosition(position);
								if (null != selected) {
									int indexCN = selected.indexOf("熟悉度");
									int indexFamiliarity = selected.indexOf("-");
									String key = selected.substring(0, indexCN);
									String familiarity = selected.substring(indexFamiliarity + 1);
									int newFamiliarity = Integer.parseInt(familiarity);

									switch (actionId) {
									case 0: {
										Intent i = new Intent(getApplicationContext(), ShowNewWord.class);
										i.putExtra("key", key);
										startActivityForResult(i, 0x1986);
									}
										break;
									case 1: {
										if (1 <= newFamiliarity - 1) 
										{
											db.updateWord(key, newFamiliarity - 1);
											refreshListView();
										}
									}
										break;
									case 2: {
										if (5 >= newFamiliarity + 1) 
										{
											db.updateWord(key, newFamiliarity + 1);
											refreshListView();
										}
									}
										break;
									case 3: {
										db.deleteWord(key);
										refreshListView();
									}
										break;
									default:
										break;
									}
								}
							}
						});
		
					mQuickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
							@Override
							public void onDismiss() {
								// Toast.makeText(getApplicationContext(), "Ups..dismissed", Toast.LENGTH_SHORT).show();
							}
						});

					mQuickAction.show(view);
				}
			});

		// No context menu for list view, registerForContextMenu(innerLv);

		db = new DataBase(this);
		db.open();

		refreshListView();
	}

    /* reload db data to list */
    public void refreshListView() {
        Cursor c = db.getAllWords();
        List<String> data = new ArrayList<String>();

        while (c.moveToNext()) {
            String str = c.getString(0) + "熟悉度***-" + c.getString(1);
            data.add(str);
        }           

        innerLv.setAdapter(new ArrayAdapter<String>(this, R.layout.list_view_item, data));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.maindisplay_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.addNewword: {
            /* 启动添加新词的activity */
            Intent i = new Intent(this, NewWord.class);
            startActivityForResult(i, 0x1985);

            return true;                
        }
        case R.id.about: {
            Toast t = Toast.makeText(this, "关于这个软件嘛，没有啥好说的，目前仅为张东亚和老婆使用。", Toast.LENGTH_SHORT);
            t.show();
            return true;                    
        }
        case R.id.cleanDatabase: {
            Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("危险---清空数据库！！！");
            builder.setMessage("请根据实际情况慎重选择是否！！！");
            builder.setCancelable(true);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        db.deleteAllWord();
                        refreshListView();
                    }
                });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });   
            builder.create().show();
            return true;                    
        }
        default:
            return false;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maindisplay_context_menu, menu);
    }

    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        if (requestCode == 0x1985) {
            if (resultCode == 0) {
                /* 更新list，待补充完整 */
                refreshListView();
            } else {
                /* 啥也不做 */
            }
        }
    }
}
