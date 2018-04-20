package org.sanchain.examples;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.sanchain.client.api.util.Utils;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nike on 17/9/01.
 */

//

/**
 * developers can use for syncledger or for exchange deposit
 */
public class SyncLedgerTest {
    private static final Logger log = LogManager.getLogger(SyncLedgerTest.class);

    private static Integer startLedger = 1624571;  //start ledger

    @Test
    public void syncLedger(){
        try {
            log.info("start sync start.");

            int ledgerCurrentIndex = Utils.ledgerClosed();
            String completeLedgers = Utils.completeLedgers();
            log.info("complete ledgers:" + completeLedgers);
            Integer startIndex = startLedger;
            startIndex = Utils.nextLedgerIndex(completeLedgers, startIndex);
            while (startIndex <= ledgerCurrentIndex) {
                if(startIndex<0){
                    log.error("wrong start index number.");
                    return;
                }
                log.info("get ledger: " + startIndex);
                String ledger = Utils.ledger(startIndex);
                if(ledger != null){
                    JSONObject ledgerJson = new JSONObject(ledger);
                    if(ledgerJson.has("status") && ledgerJson.getString("status").equals("success")) {
                        JSONObject ledgerResultData = ledgerJson.getJSONObject("result").getJSONObject("ledger");
                        JSONArray txArray = ledgerResultData.getJSONArray("transactions");
                        if (txArray != null && txArray.length() > 0) {
                            Long closeTime = ledgerResultData.getLong("close_time");
                            int txLength = txArray.length();
                            for (int i = 0; i < txLength; i++) {
                                JSONObject tx = txArray.getJSONObject(i);
                                log.info("origin txn data="+tx.toString());
                                JSONObject txData = parseTxData(tx);
                                if (txData != null) {
                                    log.info("scan txn:"+txData.toString());

                                    String account = txData.getString("Account");
                                    String destination = txData.getString("Destination");

                                    //todo exchange developer can check if the destination Address is user's deposit address

                                    txData.put("closeTime", closeTime);
                                    String txType = tx.getString("TransactionType");
                                    if (!txType.equalsIgnoreCase("Payment")) {  //just check if payment
                                        continue;
                                    }
                                    txData.put("TransactionType", txType);
                                    log.info("txn data is :"+txData.toString());
                                }
                            }
                        } else {
                            log.info("ledger: " + startIndex + " is done." + " no tx found.");
                        }
                        startIndex = Utils.nextLedgerIndex(completeLedgers, startIndex);
                    }else{
                        log.info("ledger:" + startIndex + " not done." + " no ledger found. Retry...");
                    }
                }else{
                    log.info("ledger: " + startIndex + " not done." + " timeout to get data. Retry...");
                }
                Thread.sleep(100);
                }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }

    }

    private static JSONObject parseTxData(JSONObject resultData) throws URISyntaxException, InterruptedException {
        String txType = resultData.getString("TransactionType");
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("Account", resultData.get("Account"));
        dataMap.put("Fee", resultData.get("Fee"));
        dataMap.put("hash", resultData.get("hash"));
        String txnResult = resultData.getJSONObject("metaData").getString("TransactionResult");
        dataMap.put("TransactionResult", txnResult);
        log.info("get tx hash:" + dataMap.get("hash") + ", TransactionResult=" + dataMap.get("TransactionResult"));

        if(txnResult == null || !txnResult.equalsIgnoreCase("tesSUCCESS")){
            return null;
        }

        if (txType.equalsIgnoreCase("Payment")) {
            dataMap.put("Amount", resultData.get("Amount"));
            dataMap.put("Destination", resultData.get("Destination"));
            return new JSONObject(dataMap);
        }
        return null;
    }
}
