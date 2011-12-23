/* -*- coding: utf-8 -*-
 *
 * Copyright (C) 2011-2011 fortitude.zhang
 * 
 * Author: fortitude.zhang@gmail.com
 */

/* 本文件用于查询新词，根据用户选项确定从本地查询或查询 */
package com.fortitude.recitedictcn;

import com.fortitude.recitedictcn.LocalQueryHelper;
import com.fortitude.recitedictcn.NetQueryHelper;

public class QueryWord {

    private LocalQueryHelper localQueryHelper;
    private NetQueryHelper netQueryHelper;

    public QueryWord () {
        localQueryHelper = new LocalQueryHelper();
        netQueryHelper = new NetQueryHelper();
    }

    public String QueryWordWithChoice(String newWord, boolean queryNetwork) {
        if (true == queryNetwork) {
            return netQueryHelper.query(newWord);
        }
        else {
            return localQueryHelper.query(newWord);
        }
    }
}

