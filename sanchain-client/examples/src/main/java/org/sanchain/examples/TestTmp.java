package org.sanchain.examples;

import com.sun.jmx.mbeanserver.Util;
import org.junit.Test;
import org.sanchain.client.api.util.Utils;

/**
 * Created by nike on 17/9/30.
 */
public class TestTmp {

    @Test
    public void tets1(){
        System.out.println("test print");
    }


    @Test
    public void tetsLedgerClosed(){
        System.out.println(Utils.ledgerClosed());
    }
}
