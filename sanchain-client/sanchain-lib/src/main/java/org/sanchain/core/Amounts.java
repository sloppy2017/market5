package org.sanchain.core;


import org.sanchain.core.fields.Field;

public class Amounts extends STArray{
    public void add(Entry val){
        STObject st = new STObject();
        st.put(Field.Entry,val);
        super.add(st);
    }
    public void add(Amount amount){
        Entry entry = new Entry();
        entry.Amount(amount);
        add(entry);
    }
}
