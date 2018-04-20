package org.sanchain.examples;

import org.junit.Test;
import org.sanchain.client.api.exception.APIException;
import org.sanchain.client.api.util.Utils;

/**
 * Created by nike on 17/9/30.
 */
public class AccountTest {

    @Test
    public void testBalance(){
        String address = "sKWMVTAtba2L1utpG83depUNWc2kdX7iic";
        try {
            System.out.println(Utils.getAccountInfo(address));
        } catch (APIException e) {
            e.printStackTrace();  //if actNotFound, means address not activated, send 11000SAN to it
        }


        address = "sHoB22Dn4CbAsaKduVDv64BvhDYRxzZAUh";
        try {
            System.out.println(Utils.getAccountInfo(address));
        } catch (APIException e) {
            e.printStackTrace();  //if actNotFound, means address not activated, send 11000SAN to it
        }

        /**
         *
         {
         "result": {
         "validated": true,
         "ledger_index": 1624767,
         "ledger_hash": "638932366BB13FB987E1E651D700FDF50F6760A645F4EC393B1B35CBF76E46E5",
         "account_data": {
         "Account": "sHoB22Dn4CbAsaKduVDv64BvhDYRxzZAUh",
         "OwnerCount": 0,
         "PreviousTxnLgrSeq": 1624759,
         "LedgerEntryType": "AccountRoot",
         "index": "B05BD835D0375B1C8DEE1F1E33418327A37172E8954399799F4409202F7B0900",
         "PreviousTxnID": "64BF277EFFDC5E929F09BAAC33EDEEC77322DB0439DE206C4C51B9630B8D8630",
         "Flags": 0,
         "Sequence": 1,
         "Balance": "221578200"   //balance = 221578200 units  1000,000units == 1SAN
         }
         },
         "id": 0,
         "type": "response",
         "status": "success"
         }
         */

    }


}
