package org.sanchain.examples;

import org.junit.Test;
import org.sanchain.client.api.exception.APIException;
import org.sanchain.client.api.util.Utils;

/**
 * Created by nike on 17/9/30.
 */
public class LedgetTest {

    @Test
    public void testLedgerClosed(){
        System.out.println(Utils.ledgerClosed());
    }

    @Test
    public void testCompletedLedgers(){
        try {
            System.out.println(Utils.completeLedgers());
        } catch (APIException e) {
            e.printStackTrace();
        }
    }
}
