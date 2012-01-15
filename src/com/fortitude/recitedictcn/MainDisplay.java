/* -*- coding: utf-8 -*-
 *
 * Copyright (C) 2011-2011 fortitude.zhang
 * 
 * Author: fortitude.zhang@gmail.com
 */

// Main ui.
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
import android.content.Context;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnLongClickListener;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.graphics.drawable.Drawable;

import com.fortitude.recitedictcn.DataBase;
import com.fortitude.recitedictcn.NewWord;
import com.fortitude.recitedictcn.ActionItem;
import com.fortitude.recitedictcn.QuickAction;

public class MainDisplay extends Activity {
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
                        WordItem wordItem = (WordItem)innerLv.getItemAtPosition(position);
                        String word = wordItem.getText();
                        Cursor c = db.getWord(word);
                        if (null != c) {
                            int familiarity = c.getInt(1);

                            switch (actionId) {
                                case 0: {
                                    Intent i = new Intent(getApplicationContext(), ShowNewWord.class);
                                    i.putExtra("key", word);
                                    startActivityForResult(i, 0);
                                    break;
                                }
                                case 1: {
                                    if (1 <= familiarity - 1) {
                                        db.updateWord(word, familiarity - 1);
                                        refreshListView();
                                    }
                                    break;
                                }
                                case 2: {
                                    if (5 >= familiarity + 1) {
                                        db.updateWord(word, familiarity + 1);
                                        refreshListView();
                                    }
                                    break;
                                }
                                case 3: {
                                    db.deleteWord(word);
                                    refreshListView();
                                    break;
                                }
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

    private class WordItem extends Object {
        String _word;
        Integer _familiarity;
        Integer _id;

        public WordItem(String word, Integer familiarity, Integer id) {
            _word = word;
            _familiarity = familiarity;
            _id = id;
        }

        public Integer getId() {
            return this._id;
        }

        public String getText() {
            return this._word;
        }

        public Integer getFamiliarity() {
            return this._familiarity;
        }
    }

    private class WordItemAdapter extends BaseAdapter {
        Context _context;
        List<WordItem> _wordList;

        public WordItemAdapter(Context context, List<WordItem> wl) {
            _context = context;
            _wordList = wl;
        }

        @Override
        public int getCount() {
            return _wordList.size();
        }
     
        @Override
        public Object getItem(int arg0) {
            return _wordList.get(arg0);
        }
    
        @Override
        public long getItemId(int arg0) {
            return _wordList.get(arg0).getId();
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            LayoutInflater inflater = getLayoutInflater();
            View wordItem = inflater.inflate(R.layout.list_view_item, null);
            ImageView icon = (ImageView)wordItem.findViewById(R.id.word_item_icon);

            Drawable drawable;
            // Get appropriate icon for familiarity.
            switch (_wordList.get(arg0).getFamiliarity())
            {
                case 1:
                    drawable = getResources().getDrawable(R.drawable.fami_1);
                    break;
                case 2:
                    drawable = getResources().getDrawable(R.drawable.fami_2);
                    break;
                case 3:
                    drawable = getResources().getDrawable(R.drawable.fami_3);
                    break;
                case 4:
                    drawable = getResources().getDrawable(R.drawable.fami_4);
                    break;
                default:
                    // Default is familiarity 5.
                    drawable = getResources().getDrawable(R.drawable.fami_5);  
                    break;
            }

            icon.setImageDrawable(drawable);

            TextView tv = (TextView)wordItem.findViewById(R.id.word_item_text);
            tv.setText(_wordList.get(arg0).getText());

            return wordItem;
        }
    }

    // Reload db to list view.
    public void refreshListView() {
        Cursor c = db.getAllWords();

        List<WordItem> wordList = new ArrayList<WordItem>();

        Integer i = 0;
        while (c.moveToNext()) {
            WordItem wordItem  = new WordItem(c.getString(0), c.getInt(1), i++);
            wordList.add(wordItem);
        }
    
        WordItemAdapter adapter = new WordItemAdapter(this, wordList);
        innerLv.setAdapter(adapter);
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
                Intent i = new Intent(this, NewWord.class);
                startActivityForResult(i, 0);

                return true;                
            }
            case R.id.about: {
                Toast t = Toast.makeText(this, "基于熟悉度的单词背诵软件，期望助您学习英语一臂之力。", Toast.LENGTH_SHORT);
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
        if (resultCode == 0) {
            // Refresh list.
            refreshListView();
        } else {
            // Do nothing.
        }
    }
}
