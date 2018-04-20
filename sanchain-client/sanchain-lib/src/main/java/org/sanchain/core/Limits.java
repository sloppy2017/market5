package org.sanchain.core;


import org.sanchain.core.fields.Field;

public class Limits extends STArray{
    public void add(Entry val){
        STObject st = new STObject();
        st.put(Field.Entry,val);
        super.add(st);
    }
}
