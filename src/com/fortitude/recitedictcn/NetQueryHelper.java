/* -*- coding: utf-8 -*-
 *
 * Copyright (C) 2011-2011 fortitude.zhang
 * 
 * Author: fortitude.zhang@gmail.com
 */

// Web word query using dict.cn 's API.
package com.fortitude.recitedictcn;

import java.net.URL;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Parser;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

import com.fortitude.recitedictcn.DictcnXMLHandler;

public class NetQueryHelper {
    public String query(String newWord) {
        try {
            URL url = new URL("http://dict.cn/ws.php?utf8=true&q=" + newWord);

            // Get a SAXParser from the SAXPArserFactory.
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();

            // Get the XMLReader of the SAXParser we created.
            XMLReader xr = sp.getXMLReader();
            // Create a new ContentHandler and apply it to the XML-Reader.
            DictcnXMLHandler myHandler = new DictcnXMLHandler();
            xr.setContentHandler(myHandler);

            // Parse xml data from the server.
            xr.parse(new InputSource(url.openStream()));

            // Get result.
            String text = myHandler.getWordContent();

            return text;
        } 
        catch (Exception e) {
            // TODO: exception handling.
            return "";
        }
    }
}
