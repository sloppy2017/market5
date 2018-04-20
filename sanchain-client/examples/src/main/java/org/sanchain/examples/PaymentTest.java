package org.sanchain.examples;

import org.junit.Test;
import org.sanchain.client.api.exception.APIException;
import org.sanchain.client.api.util.Utils;
import org.sanchain.core.Amount;
import org.sanchain.core.types.known.tx.signed.SignedTransaction;

/**
 * Created by nike on 17/9/30.
 */
public class PaymentTest {

    /**
     * sign and payment
     */
    @Test
    public void testPayment(){
        String address = "sMi4zUJZ1zWbVhp7VAK1zo7QPANSnx4t8z";
        String seed = "rrHT5KSWGeoT7zL6np2svZYsBL8jP";

        String destination = "sKWMVTAtba2L1utpG83depUNWc2kdX7iic";

        SignedTransaction sign = null;
        try {

            //todo: check balance

            Integer lastLedgerIndex = Utils.ledgerClosed() + 3;
            // means if currenct ledger > lastLedgerIndex, payment must not be validated, payment failed

            sign = Utils.payment(
                    seed,
                    destination,
                    Amount.fromString("1"),
                    Utils.getSequence(address),
                    true,
                    null, null, null, lastLedgerIndex
            );  //just sign local

            System.out.println(sign.hash.toHex());

            //todo :developer can save hash to db, then broadcast

            String res = Utils.sendTx(sign.tx_blob);  //broadcast
            System.out.println("res="+res);  //broadcast return

            /**
             * {
             "result": {
             "tx_json": {
             "Account": "sMi4zUJZ1zWbVhp7VAK1zo7QPANSnx4t8z",
             "Destination": "sKWMVTAtba2L1utpG83depUNWc2kdX7iic",
             "TransactionType": "Payment",
             "TxnSignature": "304402200B0E94FC74CF652951A10E4EE2887BC5656984F757F18D8500AF1B8E099E35390220035C90C424DE34E454E7463EFC8E855CB3BF7BA400C2F319D6873E10C8BE9EC3",
             "SigningPubKey": "031F42889B2E0F0C67085D3AD4D4ABCFB569774624C7297867E7C50146FB5B7420",
             "Amount": "1",
             "Fee": "11000",
             "Flags": 2147483648,
             "Sequence": 2,
             "LastLedgerSequence": 1625050,
             "hash": "65FE66943E05CAA9C8D4BEF326636DBC8281771B58EDB51423E549EF630917A5"
             },
             "engine_result_code": 0,
             "tx_blob": "12000022800000002400000002201B0018CBDA614000000000000001684000000000002AF87321031F42889B2E0F0C67085D3AD4D4ABCFB569774624C7297867E7C50146FB5B74207446304402200B0E94FC74CF652951A10E4EE2887BC5656984F757F18D8500AF1B8E099E35390220035C90C424DE34E454E7463EFC8E855CB3BF7BA400C2F319D6873E10C8BE9EC38114E4B9403A6066C7218E37212463C13EA7C79DD9E98314CAFF6E8C90145C5619663F666209BC6008FF8287",
             "engine_result": "tesSUCCESS",
             "engine_result_message": "The transaction was applied. Only final in a validated ledger."
             },
             "id": 0,
             "type": "response",
             "status": "success"
             }
             */

        } catch (APIException e) {
            e.printStackTrace();
        }
    }


    /**
     * check if success
     */
    @Test
    public void testPaymentSuccess(){
        String txnHash = "65FE66943E05CAA9C8D4BEF326636DBC8281771B58EDB51423E549EF630917A5";
        try {
            System.out.println(Utils.checkTxnSuccess(txnHash));
        } catch (APIException e) {
            e.printStackTrace();
        }
    }
}
