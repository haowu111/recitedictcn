/* -*- coding: utf-8 -*-
 *
 * Copyright (C) 2011-2011 fortitude.zhang
 * 
 * Author: fortitude.zhang@gmail.com
 */

package com.fortitude.recitedictcn;

import com.fortitude.recitedictcn.StarDict;

public class LocalQueryHelper {

    private StarDict stardict;

    public LocalQueryHelper() {
        stardict = new StarDict();
    }

    public String query(String newWord) {
        return stardict.getExplanation(newWord);
    }
}
