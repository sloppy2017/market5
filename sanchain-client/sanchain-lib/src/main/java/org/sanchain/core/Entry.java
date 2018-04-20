package org.sanchain.core;


import org.sanchain.core.fields.Field;
import org.sanchain.core.uint.UInt32;

public class Entry extends STObject{

    public void Amount(Amount val){
        super.put(Field.Amount,val);
    }

    public void LimitAmount(Amount val){
        super.put(Field.LimitAmount,val);
    }

    public void Flags(UInt32 val){
        super.put(Field.Flags,val);
    }
}
