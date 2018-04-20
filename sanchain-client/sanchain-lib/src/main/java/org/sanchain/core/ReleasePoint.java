package org.sanchain.core;

import org.sanchain.core.fields.Field;
import org.sanchain.core.uint.UInt32;

/**
 * Created by ac
 * since 15/5/14.
 */
public class ReleasePoint extends STObject{
    public ReleasePoint(UInt32 exp, UInt32 rel){
        super();
        expiration(exp);
        releaseRate(rel);
    }

    public void expiration(UInt32 val){super.put(Field.Expiration, val);}
    public void releaseRate(UInt32 val){super.put(Field.ReleaseRate, val);}
}
