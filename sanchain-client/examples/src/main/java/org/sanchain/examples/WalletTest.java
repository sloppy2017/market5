package org.sanchain.examples;

import org.junit.Test;
import org.sanchain.core.Wallet;

/**
 * Created by nike on 17/9/30.
 */
public class WalletTest {

    @Test
    public void testWallet(){
            Wallet wallet = new Wallet();
            System.out.println(wallet.account().address);
            System.out.println(wallet.seed());
        /**
         * sKWMVTAtba2L1utpG83depUNWc2kdX7iic
         rnGaCUYkQuJewXvY9tsVCjbPaJ5en
         */

    }

    @Test
    public void testWalletFromSeed(){
        Wallet wallet = Wallet.fromSeedString("rnGaCUYkQuJewXvY9tsVCjbPaJ5en");
        System.out.println(wallet.seed());
        System.out.println(wallet.account().address);
    }
}
