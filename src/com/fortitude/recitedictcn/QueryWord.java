/* -*- coding: utf-8 -*-
 *
 * Copyright (C) 2011-2011 fortitude.zhang
 * 
 * Author: fortitude.zhang@gmail.com
 */

// Query word interface.
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

