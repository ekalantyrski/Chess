package com.Eric;

import java.util.ArrayList;

/**
 * Created by erickalantyrski on 2016-05-05.
 */
public class DrawData {

    ArrayList<Element> elementArray;

    public DrawData(ArrayList<Element> elementArray)
    {
        this.elementArray = elementArray;
    }

    public void setElementArray(ArrayList<Element> elementArray)
    {
        this.elementArray = elementArray;
    }

    public ArrayList<Element> getElementArray()
    {
        return elementArray;
    }
}
