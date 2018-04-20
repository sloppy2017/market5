package org.sanchain.client.api.util;

import org.sanchain.client.api.exception.APIException;
import org.sanchain.client.api.model.AmountObj;
import org.sanchain.client.api.model.Effect;
import org.sanchain.client.api.model.TxObj;
import org.sanchain.client.ws.CoralWebSocketClient;
import org.sanchain.core.*;
import org.sanchain.core.Currency;
import org.sanchain.core.fields.Field;
import org.sanchain.core.hash.B58;
import org.sanchain.core.hash.Hash256;
import org.sanchain.core.serialized.enums.LedgerEntryType;
import org.sanchain.core.types.known.tx.result.AffectedNode;
import org.sanchain.core.types.known.tx.result.TransactionMeta;
import org.sanchain.core.types.known.tx.signed.SignedTransaction;
import org.sanchain.core.types.known.tx.txns.*;
import org.sanchain.core.uint.UInt32;
import org.sanchain.crypto.ecdsa.IKeyPair;
import org.sanchain.crypto.ecdsa.Seed;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.bouncycastle.util.encoders.Hex;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Utils for basic usage
 * Created by AC on 15/12/4.
 */
public class Utils {
    private static final Logger logger = LogManager.getLogger(Utils.class);
    private static Map<String, String> assetMap = new HashMap<>();

    /**
     * check if key of json is object
     *
     * @param json json object to be checked
     * @param key  key to be checked
     * @return
     */
    public static boolean isJsonObject(JSONObject json, String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        } else if (!json.has(key)) {
            return false;
        } else {
            try {
                json.getJSONObject(key);
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }

    /**
     * check is data is in json format
     *
     * @param data data to be checked
     * @return true if data is json format, false if not
     */
    public static boolean isJson(String data) {
        if (StringUtils.isBlank(data)) {
            return false;
        } else {
            try {
                new JsonParser().parse(data);
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }

    /**
     * negative method for #{@link Utils#isJson(String)}
     *
     * @param data data to be checked
     * @return opposite value of #{@link Utils#isJson(String)}
     */
    public static boolean isNonJson(String data) {
        return !isJson(data);
    }

    //-------------------------------------------COMPLETE LEDGERS-------------------------------------------------------

    /**
     * used for checking ledger tx
     *
     * @param ledgerIndex ledger index number
     * @return
     * @throws org.sanchain.client.api.exception.APIException
     */
    public static String ledger(int ledgerIndex) throws APIException {
        return ledger(ledgerIndex, false, true, true, false, false);
    }

    /**
     * load ledger info
     *
     * @param ledgerIndex  ledger index number
     * @param full         if get full data
     * @param expand       if expand
     * @param transactions if contains txns
     * @param accounts     if contains accounts
     * @param dividend     if contains dividend txns
     * @return ledger data
     * @throws APIException
     */
    public static String ledger(
            int ledgerIndex, boolean full, boolean expand,
            boolean transactions, boolean accounts, boolean dividend) throws APIException {
        Map<String, Object> params = new HashMap<>();
        params.put("id", 2);
        params.put("command", "ledger");
        params.put("ledger_index", ledgerIndex);
        params.put("full", full);
        params.put("expand", expand);
        params.put("transactions", transactions);
        params.put("accounts", accounts);
        params.put("dividend", dividend);
        String data = new Gson().toJson(params);
        return CoralWebSocketClient.request(CoralWebSocketClient.WebsocketServerType.FULL, data);
    }

    /**
     * @param completeLedgers
     * @param currentIndex
     * @return -2 if complete ledgers not got;
     * -1 if next index > max value of complete ledgers;
     * next ledger index if other conditions;
     */
    public static int nextLedgerIndex(String completeLedgers, int currentIndex) {
        int nextIndex = currentIndex + 1;
        if (completeLedgers == null || completeLedgers.equals("empty")) {
            logger.error("complete ledgers error, try next time..");
            return -2;
        } else {
            String[] cls = completeLedgers.split(",");
            int endMax = 0;
            for (String current : cls) {
                int end;
                if (current.contains("-")) {
                    end = NumberUtils.toInt(current.split("-")[1]);
                } else {
                    end = NumberUtils.toInt(current);
                }
                if (end > endMax) {
                    endMax = end;
                }
            }
            if (nextIndex > endMax) {
                logger.error("invalid ledger index:" + nextIndex + ", it's bigger than the max index.");
                return -1;
            } else {
                return nextIndex;
            }
        }
    }

    public static void main(String args[]) {
        System.out.println(nextLedgerIndex("4157499-4175493,4175495-4175496,4175499-4175500,4175502-4175503,4175505-4175506,4175508-4175509,4175511-4175512,4175514-4175517,4175519-4175520,4175522-4175523,4175525-4175526,4175528-4175532,4175534-4175535,4175537-4175538,4175540-4175541,4175543-4175544,4175546-4175547,4175549-4175550,4175552-4175553,4175555-4175556,4175558-4175559,4175561-4175562,4175564-4175565,4175567-4175568,4175570-4175571,4175573-4175574,4175576-4175577,4175579-4175580,4175582-4175583,4175585-4175586,4175588-4175589,4175591-4175592,4175594,4175597,4175600,4175602-4175603,4175605,4175607,4175609,4175611-4175612,4175614,4175616,4175621-4175622,4175624-4175625,4175630-4175631,4175633-4175634,4175636-4175639,4175641-4175642,4175644,4175647,4175649-4175650,4175653-4359706", 4359665));
    }

    /**
     * Get completed ledgers from rippled full server
     *
     * @return complete_ledgers str
     * @throws APIException if error occurs, then throw #{@link APIException}
     */
    public static String completeLedgers() throws APIException {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("id", 0);
            params.put("command", "server_info");
            String data = new Gson().toJson(params);
            String res = CoralWebSocketClient.request(CoralWebSocketClient.WebsocketServerType.FULL, data);
            if (res != null) {
                JSONObject jsonObject = new JSONObject(res);
                if ("success".equals(jsonObject.getString("status"))) {
                    return jsonObject.getJSONObject("result").getJSONObject("info").getString("complete_ledgers");
                }
            }
        } catch (Exception ex) {
            if (ex instanceof APIException)
                throw ex;
            else {
                throw new APIException(APIException.ErrorCode.INTERNAL_ERROR, ex.getMessage());
            }
        }
        return null;
    }

    /**
     * get ledger closed value of rippled
     *
     * @return
     */
    public static int ledgerClosed() {
        Map<String, Object> params = new HashMap<>();
        params.put("id", 1);
        params.put("command", "ledger_closed");

        String data = new Gson().toJson(params);
        try {
            String res = CoralWebSocketClient.request(CoralWebSocketClient.WebsocketServerType.FULL, data);
            JSONObject resJson = new JSONObject(res);
            if (resJson.has("status") && resJson.getString("status").equalsIgnoreCase("success")) {
                JSONObject jsonObjectResult = resJson.getJSONObject("result");
                return jsonObjectResult.getInt("ledger_index");
            }
        } catch (Exception e) {
            System.out.println("getLedgerClosed error:" + e.getMessage());
            logger.info("getLedgerClosed exception");
            logger.info("getLedgerClosed error:" + e.getMessage());
        }

        return 0;

    }

    /**
     * check ledger is included in complete ledgers
     *
     * @param lastLedgerIndex
     * @return true if current index is included, false if excluded
     * @throws InterruptedException
     */
    public static boolean checkCompleteLedgers(int lastLedgerIndex) throws InterruptedException {
        String completeLedgerStr = null;
        while (StringUtils.isBlank(completeLedgerStr)) {
            try {
                completeLedgerStr = completeLedgers();
            } catch (Exception ex) {
            }
            Thread.sleep(3000);
        }

        String[] cls = completeLedgerStr.split(",");
        boolean res1 = false;
        boolean res2 = false;
        for (String current : cls) {
            int start, end;
            if (current.contains("-")) {
                start = NumberUtils.toInt(current.split("-")[0]);
                end = NumberUtils.toInt(current.split("-")[1]);
            } else {
                start = NumberUtils.toInt(current);
                end = NumberUtils.toInt(current);
            }
            if (lastLedgerIndex >= start && lastLedgerIndex <= end) {
                res1 = true;
            }
            if (lastLedgerIndex - 3 >= start && lastLedgerIndex - 3 <= end) {
                res2 = true;
            }
        }
        return res1 && res2;
    }


    //------------------------------------------------TRANSACTION-------------------------------------------------------


    /**
     * send tx to san network
     *
     * @param txBlob
     * @return
     * @throws APIException
     */
    public static String sendTx(String txBlob) throws APIException {
        String json = null;
        int i = 0;
        while (StringUtils.isBlank(json) && i < 10) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", 0);
            data.put("command", "submit");
            data.put("tx_blob", txBlob);
            String postData = new Gson().toJson(data);
            json = CoralWebSocketClient.request(postData);
            i++;
            logger.info("make tx result: " + json);
            return json;
        }
        throw new APIException(APIException.ErrorCode.REMOTE_SERVER_BUSY, "Send tx always returns NULL.");
    }

    /**
     * make payment tx
     *
     * @param seed          user seed
     * @param recipient     recipient address
     * @param amount        send amount
     * @param sequence      sender's sequence
     * @param isResolved    if recipient is already activated
     * @param paths         if has paths, can be null
     * @param sendMax       along with paths, (1+1‰)*amount
     * @param dt            if has some destination tag, must be Integer
     * @param lastLedgerSeq last ledger sequence, use for reliable tx
     * @return obj of #{@link org.sanchain.core.types.known.tx.signed.SignedTransaction}
     */
    public static SignedTransaction payment(
            String seed,
            String recipient,
            Amount amount,
            int sequence,
            boolean isResolved,
            JSONArray paths,
            Amount sendMax,
            String dt,
            Integer lastLedgerSeq) {
        IKeyPair kp = Seed.getKeyPair(seed);
        Payment txn = new Payment();
        AccountID destination = AccountID.fromAddress(recipient);
        txn.destination(destination);
        txn.amount(amount);
        if (lastLedgerSeq != null && lastLedgerSeq > 0) {
            txn.lastLedgerSequence(new UInt32(lastLedgerSeq));
        }
        if (NumberUtils.isNumber(dt))
            txn.destinationTag(new UInt32(dt));
        txn.account(AccountID.fromSeedBytes(B58.getInstance().decodeFamilySeed(seed)));
        if (paths != null) {
            if (sendMax != null)
                txn.sendMax(sendMax);
            txn.paths(PathSet.translate.fromJSONArray(paths));
        }
        SignedTransaction sign = new SignedTransaction(txn);
        long fee = 1000;
        if (amount.currencyString().equals("SAN")) {
            fee = (long) (amount.multiply(new BigDecimal("1000")).doubleValue());
        }

        isResolved = true;
        try {
            getAccountInfo(recipient);
        }catch (Exception e){
            isResolved = false;
        }

        fee = Math.max(1000, fee);
        //fee = Math.min(1000000,fee);

        if (!isResolved) {
            fee = 10000 + fee;
        }

        if(fee > 10000000000l){
            logger.info("invalid payment:fee is too large:amount="+amount.toString());
            return null;
        }

        sign.prepare(kp, Amount.fromString(String.valueOf(fee)), new UInt32(sequence), null);
        return sign;
    }


    public static SignedTransaction invoicePayment(
            String seed,
            String recipient,
            Amount amount,
            int sequence,
            String dt,
            String invoice,
            Integer lastLedgerSeq) {
        IKeyPair kp = Seed.getKeyPair(seed);
        Payment txn = new Payment();
        AccountID destination = AccountID.fromAddress(recipient);
        txn.destination(destination);
        txn.amount(amount);
        if(invoice != null){
            txn.invoiceID(Hash256.fromHex(invoice));
        }
        if (lastLedgerSeq != null && lastLedgerSeq > 0) {
            txn.lastLedgerSequence(new UInt32(lastLedgerSeq));
        }
        if (NumberUtils.isNumber(dt))
            txn.destinationTag(new UInt32(dt));
        txn.account(AccountID.fromSeedBytes(B58.getInstance().decodeFamilySeed(seed)));
        SignedTransaction sign = new SignedTransaction(txn);
        long fee = 1000;
        if (amount.currencyString().equals("SAN")) {
            fee = (long) (amount.multiply(new BigDecimal("1000")).doubleValue());
        }

        boolean isResolved = true;
        try {
            getAccountInfo(recipient);
        }catch (Exception e){
            isResolved = false;
        }

        fee = Math.max(1000, fee);

        if (!isResolved) {
            fee = 10000 + fee;
        }

        if(fee > 1000000){
            logger.info("invoicePayment:fee is too large:amount="+amount.toString());
            return null;
        }

        sign.prepare(kp, Amount.fromString(String.valueOf(fee)), new UInt32(sequence), null);
        return sign;
    }

    /**
     * Add referee
     *
     * @param seed           user's seed
     * @param refereeAddress referee's address
     * @param sequence       user's sequence
     * @return
     * @throws APIException
     */
    public static SignedTransaction addReferee(String seed, String refereeAddress, int sequence) {
        IKeyPair kp = Seed.getKeyPair(seed);
        AddReferee txn = new AddReferee();
        AccountID destination = AccountID.fromAddress(refereeAddress);
        txn.destination(destination);
        txn.account(AccountID.fromSeedBytes(B58.getInstance().decodeFamilySeed(seed)));
        SignedTransaction sign = new SignedTransaction(txn);
        long fee = 1000;
        sign.prepare(kp, Amount.fromString(String.valueOf(fee)), new UInt32(sequence), null);
        return sign;
    }

    /**
     * TrustSet tx
     *
     * @param seed     user's seed
     * @param issure   currency's issuer
     * @param currency currency code
     * @param sequence user's sequence
     * @param isRemove if distincted tx
     * @return
     * @throws APIException
     */
    public static SignedTransaction trustSet(String seed, String issure, String currency, int sequence, boolean isRemove) {
        IKeyPair kp = Seed.getKeyPair(seed);
        TrustSet txn = new TrustSet();
        int trustAmount = 1000000000;
        if (isRemove) {
            trustAmount = 0;
        }

        Currency cur = new Currency(Hex.decode(currency));

        Amount limitAmount = new Amount(new BigDecimal(trustAmount), cur, AccountID.fromAddress(issure));
        txn.limitAmount(limitAmount);
        txn.account(AccountID.fromSeedBytes(B58.getInstance().decodeFamilySeed(seed)));
        if (!isRemove)
            txn.flags(new UInt32(131072));//set noRipple
        else
            txn.flags(new UInt32(262144));//clear noRipple
        SignedTransaction sign = new SignedTransaction(txn);
        long fee = 1000;
        sign.prepare(kp, Amount.fromString(String.valueOf(fee)), new UInt32(sequence), null);
        return sign;
    }



    //------------------------------------------------TX PARSERS--------------------------------------------------------

    /**
     * get txn amount in json
     *
     * @param txnHash
     * @return
     */
    public static JSONObject getTxnAmount(String txnHash) {
        String txn;
        try {
            txn = getTxn(txnHash);
            if (logger.isDebugEnabled())
                logger.debug("getTxnAmount result=" + txn);
            if (Utils.isJson(txn)) {
                JSONObject jsonObject = new JSONObject(txn);
                if (checkTxnSuccess(jsonObject)) {
                    if (Utils.isJsonObject(jsonObject.getJSONObject("result"), "Amount")) {
                        JSONObject jsonObjectAmount = jsonObject.getJSONObject("result").getJSONObject("Amount");
                        return jsonObjectAmount;
                    } else {
                        JSONObject jsonObjectAmount = new JSONObject();
                        jsonObjectAmount.put("currency", "SAN");
                        jsonObjectAmount.put("value", new BigDecimal(jsonObject.getJSONObject("result").getString("Amount"))
                                .divide(new BigDecimal(1000000), BigDecimal.ROUND_HALF_UP).toPlainString());
                        jsonObject.put("issuer", AccountID.SAN_ISSUER.address);
                        return jsonObjectAmount;
                    }
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (APIException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean checkTxnSuccess(JSONObject txn) throws APIException {
        try {
            if (txn != null) {
                if (txn.has("result")) {
                    if (txn.getJSONObject("result").has("validated") &&
                            txn.getJSONObject("result").getBoolean("validated") && txn.getJSONObject("result").getJSONObject("meta").has("TransactionResult") &&
                            txn.getJSONObject("result").getJSONObject("meta").getString("TransactionResult").equalsIgnoreCase("tesSUCCESS")) {  //
                        return true;
                    }
//                    else if(!txn.getJSONObject("result").has("meta")&&txn.has("status") && "success".equalsIgnoreCase(txn.getString("status"))){
//                        throw new APIException(APIException.ErrorCode.TXN_STATUS_UNKNOWN, "unknown status, retry next time.");
//                    }
                }
            }
        } catch (Exception e) {
            logger.error("checkTxnSuccess:txn=" + txn.toString() + "," + e.getMessage(), e);
            if(e instanceof APIException){
                throw (APIException) e;
            }else
                throw new APIException(APIException.ErrorCode.UNKNOWN_ERROR, e.getMessage());
        }
        return false;
    }

    /**
     * check if tx is success
     *
     * @param txnHash
     * @return
     * @throws APIException
     */
    public static boolean checkTxnSuccess(String txnHash) throws APIException {
        try {
            String txn = getTxn(txnHash);
            System.out.println("txn check result=" + txn);
            logger.info("txn check:txnHash=" + txnHash + ",result=" + txn);
            if (isJson(txn)) {
                JSONObject jsonObject = new JSONObject(txn);
                return checkTxnSuccess(jsonObject);
            } else {
                //wait and recheck..until receive result.
                Thread.sleep(1000);
                return checkTxnSuccess(txnHash);
            }
        } catch (Exception e) {
            logger.error("checkTxnSuccess:txnhash=" + txnHash + "," + e.getMessage(), e);
            if (e instanceof APIException) {
                if ("notImpl".equals(e.getMessage()) || "txnNotFound".equals(e.getMessage())) {
                    return false;
                }else{
                    throw (APIException) e;
                }
            }
            throw new APIException(APIException.ErrorCode.UNKNOWN_ERROR, e.getMessage());
        }
    }

    /**
     * get tx data by tx_hash
     *
     * @param txnHash hash value of tx
     * @return tx data
     * @throws java.net.URISyntaxException
     * @throws InterruptedException
     * @throws APIException
     */
    public static String getTxn(String txnHash) throws URISyntaxException, InterruptedException, APIException {
        Map<String, Object> params = new HashMap<>();
        params.put("id", 1);
        params.put("command", "tx");
        params.put("transaction", txnHash);

        String data = new Gson().toJson(params);
        try {
            return CoralWebSocketClient.request(CoralWebSocketClient.WebsocketServerType.FULL, data);
        } catch (Exception e) {
            logger.error("getTxn:txnhash=" + txnHash + "," + e.getMessage(), e);
            if (e instanceof APIException) {
                throw e;
            }
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     *
     * @return effects Map #{@link java.util.Map}
     */
    public static Map<String, Object> generateEffectListFromTxMetaByType(String address, TxObj item, TransactionMeta meta, String type, Map<String, String> assets) {
        if (MapUtils.isEmpty(assetMap)) {
            assetMap = assets;
        }
        return generateEffectListFromTxMetaByType(address, item, meta, type);
    }

    private static Map<String, Object> generateEffectListFromTxMetaByType(String address, TxObj item, TransactionMeta meta, String type) {

        List<Effect> effects = new ArrayList<>();
        List<Effect> showEffects = new ArrayList<>();
        for (AffectedNode node : meta.affectedNodes()) {
            LedgerEntryType nodeType = node.ledgerEntryType();
            if (nodeType != LedgerEntryType.AssetState && nodeType != LedgerEntryType.AccountRoot && nodeType != LedgerEntryType.Offer && nodeType != LedgerEntryType.RippleState) {
                //not meta info for balance changes and offer changes, continue
                continue;
            }
            STObject field;
            STObject obj;
            STObject preFields = null;
            if (node.isModifiedNode()) {
                obj = (STObject) node.get(Field.ModifiedNode);
                field = (STObject) obj.get(Field.FinalFields);
                preFields = (STObject) obj.get(Field.PreviousFields);
            } else if (node.isCreatedNode()) {
                obj = (STObject) node.get(Field.CreatedNode);
                field = (STObject) obj.get(Field.NewFields);
            } else {
                obj = (STObject) node.get(Field.DeletedNode);
                field = (STObject) obj.get(Field.FinalFields);
                preFields = (STObject) obj.get(Field.PreviousFields);
            }
            if (field == null) {
                continue;
            }
            switch (type) {
                case "failed":
                case "unknown":
                case "account_set":
                case "addreferee":
                case "connecting":
                case "set_regular_key":
                case "cancel_regular_key":
                    if (nodeType == LedgerEntryType.AccountRoot) {
                        parserAccountRoot(field, node, preFields, address, effects, item);
                    }
                    continue;
                case "rippling":
                case "offer_rippling":
                    //RippleState means balance of non-native currency balance changes
                    if (nodeType == LedgerEntryType.RippleState) {
                        parserRippleState(field, node, preFields, address, effects, item);
                    }
                    continue;
                case "sent":
                case "active_add":
                case "active":
                case "active_acc":
                case "received":
                case "active_show":
                    if (nodeType == LedgerEntryType.AccountRoot) {
                        parserAccountRoot(field, node, preFields, address, effects, item);
                    }

                    if (nodeType == LedgerEntryType.RippleState) {
                        parserRippleState(field, node, preFields, address, effects, item);
                    }
                    if (nodeType == LedgerEntryType.AssetState) {
//                        parserAssetState(field, node, preFields, address, effects);
                    }
                    if (nodeType == LedgerEntryType.Offer) {
                        Amount takerGets = (Amount) field.get(Field.TakerGets);
                        Amount takerPays = (Amount) field.get(Field.TakerPays);
                        if (preFields == null) {
                            continue;
                        }
                        Amount preTakerGets = (Amount) preFields.get(Field.TakerGets);
                        Amount preTakerPays = (Amount) preFields.get(Field.TakerPays);
                        //show effects
                        AccountID account = (AccountID) field.get(Field.Account);
                        if (address.equals(item.getSender()) || address.equals(account)) {
                            Effect showEffect = new Effect();
                            showEffect.setTakerGets(new AmountObj(preTakerGets.subtract(takerGets).valueText(), getAssetName(preTakerGets), preTakerGets.issuerString()));
                            showEffect.setTakerPays(new AmountObj(preTakerPays.subtract(takerPays).valueText(), getAssetName(preTakerPays), preTakerPays.issuerString()));
                            showEffect.setType("bought");
                            showEffects.add(showEffect);
                        }
                    }
                    if (nodeType == LedgerEntryType.Asset) {

                    }
                    continue;
                case "referee":
                case "connected":
                case "offer_cancelled":
                    if (nodeType == LedgerEntryType.Offer) {
                        parserOffer(field, node, preFields, address, item, showEffects);
                    } else if (node.ledgerEntryType() == LedgerEntryType.AccountRoot && node.isModifiedNode()) {
                        parserAccountRoot(field, node, preFields, address, effects, item);
                    }
                    continue;
                case "offercreate":
                    if (nodeType == LedgerEntryType.Offer) {
                        parserOffer(field, node, preFields, address, item, showEffects);
                    } else if (nodeType == LedgerEntryType.RippleState) {
                        parserRippleState(field, node, preFields, address, effects, item);
                    } else if (nodeType == LedgerEntryType.AccountRoot && node.isModifiedNode()) {
                        parserAccountRoot(field, node, preFields, address, effects, item);
                    } else if (nodeType == LedgerEntryType.AssetState) {
//                        parserAssetState(field, node, preFields, address, effects);
                    }
                    continue;
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("effects", effects);
        result.put("show_effects", showEffects);
        result.put("item", item);
        return result;
    }

    private static void parserOffer(STObject field, AffectedNode node, STObject preFields, String address, TxObj item, List<Effect> showEffects) {

        Amount takerGets;
        Amount takerPays;
        Amount preTakerGets;
        Amount preTakerPays;
        AccountID account = (AccountID) field.get(Field.Account);
        //account in delete node and fieldsPrev amount is not zero, offer ok.
        UInt32 flag = field.get(UInt32.Flags);
        if (flag != null) {
            if (item.getOfferType() == null) {
                if (account.address.equals(address)) {
                    item.setOfferType(flag.intValue() == 131072 ? "sell" : "buy");
                }
            }
        }
        if (node.isDeletedNode()) {
            if (preFields != null) {
                takerGets = (Amount) field.get(Field.TakerGets);
                takerPays = (Amount) field.get(Field.TakerPays);

                preTakerGets = (Amount) preFields.get(Field.TakerGets);
                preTakerPays = (Amount) preFields.get(Field.TakerPays);
                if (preTakerPays == null) {
                    preTakerPays = takerPays;
                }
                if (preTakerGets == null) {
                    preTakerGets = takerGets;
                }
                if (account != null && account.address.equals(address)) {
                    if (item.getTakerGets() == null) {
                        item.setTakerPays(new AmountObj(preTakerPays.valueText(), getAssetName(preTakerPays), preTakerPays.issuerString()));
                        item.setTakerGets(new AmountObj(preTakerGets.valueText(), getAssetName(preTakerGets), preTakerGets.issuerString()));
                        item.setOfferStatus("offer_funded");
                    } else {
                        item.setTakerPays(new AmountObj(preTakerPays.add(new BigDecimal(item.getTakerPays().getAmount())).valueText(), getAssetName(preTakerPays), preTakerPays.issuerString()));
                        item.setTakerGets(new AmountObj(preTakerGets.add(new BigDecimal(item.getTakerGets().getAmount())).valueText(), getAssetName(preTakerGets), preTakerGets.issuerString()));
                        item.setOfferStatus("offer_funded");
                    }
                    Effect effect = new Effect();
                    effect.setTakerGets(new AmountObj(preTakerGets.subtract(takerGets).valueText(), getAssetName(takerGets), takerGets.issuerString()));
                    effect.setTakerPays(new AmountObj(preTakerPays.subtract(takerPays).valueText(), getAssetName(takerPays), takerPays.issuerString()));
                    effect.setType("offer_filled");
                    showEffects.add(effect);
                    if (!takerGets.isZero() || !takerPays.isZero()) {
                        effect = new Effect();
                        effect.setTakerGets(new AmountObj(takerGets.valueText(), getAssetName(takerGets), takerGets.issuerString()));
                        effect.setTakerPays(new AmountObj(takerPays.valueText(), getAssetName(takerPays), takerPays.issuerString()));
                        effect.setType("offer_cancelled");
                        showEffects.add(effect);
                    }
                } else if (address.equals(item.getSender())) {
                    //show effects on offer met
                    //sender create an offer, some offers has been filled
//                    takerGets = (Amount) field.get(Field.TakerGets);
//                    takerPays = (Amount) field.get(Field.TakerPays);
//                    preTakerGets = (Amount) preFields.get(Field.TakerGets);
//                    preTakerPays = (Amount) preFields.get(Field.TakerPays);
                    Effect effect = new Effect();
                    effect.setType("offer_filled");
                    if (preTakerPays != null && preTakerGets != null) {
                        if (address.equals(account.address)) {
                            effect.setTakerGets(new AmountObj(preTakerGets.subtract(takerGets).valueText(), getAssetName(takerGets), takerGets.issuerString()));
                            effect.setTakerPays(new AmountObj(preTakerPays.subtract(takerPays).valueText(), getAssetName(takerPays), takerPays.issuerString()));
                        } else {
                            logger.info("preTakerPays:" + preTakerPays + ", takerPays:" + takerPays);
                            effect.setTakerGets(new AmountObj(preTakerPays.subtract(takerPays).valueText(), getAssetName(takerPays), takerPays.issuerString()));
                            effect.setTakerPays(new AmountObj(preTakerGets.subtract(takerGets).valueText(), getAssetName(takerGets), takerGets.issuerString()));
                        }
                    } else {
                        if (address.equals(account.address)) {
                            effect.setTakerGets(new AmountObj(takerGets.valueText(), getAssetName(takerGets), takerGets.issuerString()));
                            effect.setTakerPays(new AmountObj(takerPays.valueText(), getAssetName(takerPays), takerPays.issuerString()));
                        } else {
                            effect.setTakerGets(new AmountObj(takerPays.valueText(), getAssetName(takerPays), takerPays.issuerString()));
                            effect.setTakerPays(new AmountObj(takerGets.valueText(), getAssetName(takerGets), takerGets.issuerString()));
                        }
                    }
                    showEffects.add(effect);
                }
            } else if (field != null) {
                takerGets = (Amount) field.get(Field.TakerGets);
                takerPays = (Amount) field.get(Field.TakerPays);
                if (item.getType().equals("offer_cancelled")) {
                    item.setTakerPays(new AmountObj(takerPays.valueText(), getAssetName(takerPays), takerPays.issuerString()));
                    item.setTakerGets(new AmountObj(takerGets.valueText(), getAssetName(takerGets), takerGets.issuerString()));
                }
                if (account != null && account.address.equals(address)) {

                    Effect effect = new Effect();
                    effect.setType("offer_cancelled");
                    effect.setTakerGets(new AmountObj(takerGets.valueText(), getAssetName(takerGets), takerGets.issuerString()));
                    effect.setTakerPays(new AmountObj(takerPays.valueText(), getAssetName(takerPays), takerPays.issuerString()));
                    showEffects.add(effect);
                }
            }
        } else if (node.isModifiedNode()) {
            if (account != null && account.address.equals(address)) {
                item.setOfferStatus("offer_partially_funded");
                //offer not filled.
                takerGets = (Amount) field.get(Field.TakerGets);
                takerPays = (Amount) field.get(Field.TakerPays);
                if (preFields != null) {
                    preTakerGets = (Amount) preFields.get(Field.TakerGets);
                    preTakerPays = (Amount) preFields.get(Field.TakerPays);
                    if (preTakerGets == null) {
                        preTakerGets = takerGets;
                    }
                    if (preTakerPays == null) {
                        preTakerPays = takerPays;
                    }
                    if (item.getTakerGets() == null) {
                        item.setTakerPays(new AmountObj(preTakerPays.valueText(), getAssetName(takerPays), takerPays.issuerString()));
                        item.setTakerGets(new AmountObj(preTakerGets.valueText(), getAssetName(takerGets), takerGets.issuerString()));
                    } else {
                        item.setTakerPays(new AmountObj(preTakerPays.add(new BigDecimal(item.getTakerPays().getAmount())).valueText(), getAssetName(takerPays), takerPays.issuerString()));
                        item.setTakerGets(new AmountObj(preTakerGets.add(new BigDecimal(item.getTakerGets().getAmount())).valueText(), getAssetName(takerGets), takerGets.issuerString()));
                    }
                    item.setPartiallyPays(new AmountObj(preTakerPays.value().subtract(takerPays.value()).toPlainString(), getAssetName(takerPays), takerPays.issuerString()));
                    item.setPartiallyGets(new AmountObj(preTakerGets.value().subtract(takerGets.value()).toPlainString(), getAssetName(takerGets), takerGets.issuerString()));

                    Effect effect = new Effect();
                    effect.setType("offer_filled");
                    effect.setTakerGets(new AmountObj(preTakerGets.subtract(takerGets).valueText(), getAssetName(takerGets), takerGets.issuerString()));
                    effect.setTakerPays(new AmountObj(preTakerPays.subtract(takerPays).valueText(), getAssetName(takerPays), takerPays.issuerString()));
                    showEffects.add(effect);
                }
                if (takerGets.isZero()) {
                    item.setOfferStatus("offer_funded");
                    return;
                }
                Effect effect = new Effect();
                effect.setType("offer_remained");
                effect.setTakerGets(new AmountObj(takerGets.valueText(), getAssetName(takerGets), takerGets.issuerString()));
                effect.setTakerPays(new AmountObj(takerPays.valueText(), getAssetName(takerPays), takerPays.issuerString()));
                showEffects.add(effect);

            } else if (address.equals(item.getSender())) {
                if (preFields != null) {
                    preTakerGets = (Amount) preFields.get(Field.TakerGets);
                    preTakerPays = (Amount) preFields.get(Field.TakerPays);
                    takerGets = (Amount) field.get(Field.TakerGets);
                    takerPays = (Amount) field.get(Field.TakerPays);

                    Effect effect = new Effect();
                    effect.setType("offer_filled");
                    if (preTakerGets == null) {
                        preTakerGets = takerGets;
                    }
                    if (preTakerPays == null) {
                        preTakerPays = takerPays;
                    }
                    if (takerGets.currencyString().equals(item.getTakerGets().getCurrency())) {
                        effect.setTakerGets(new AmountObj(preTakerGets.subtract(takerGets).valueText(), getAssetName(takerGets), takerGets.issuerString()));
                        effect.setTakerPays(new AmountObj(preTakerPays.subtract(takerPays).valueText(), getAssetName(takerPays), takerPays.issuerString()));
                    } else {
                        effect.setTakerPays(new AmountObj(preTakerGets.subtract(takerGets).valueText(), getAssetName(takerGets), takerGets.issuerString()));
                        effect.setTakerGets(new AmountObj(preTakerPays.subtract(takerPays).valueText(), getAssetName(takerPays), takerPays.issuerString()));
                    }
                    showEffects.add(effect);
                }
            }

        } else if (node.isCreatedNode()) {
            if (field != null) {
                if (account != null && account.address.equals(address)) {
                    takerGets = (Amount) field.get(Field.TakerGets);
                    takerPays = (Amount) field.get(Field.TakerPays);
                    //offer not filled.
                    if (item.getTakerGets() == null) {
                        item.setOfferStatus("offer_create");
                        item.setTakerPays(new AmountObj(takerPays.valueText(), getAssetName(takerPays), takerPays.issuerString()));
                        item.setTakerGets(new AmountObj(takerGets.valueText(), getAssetName(takerGets), takerGets.issuerString()));
                    } else {
                        if (takerGets.value().compareTo(new BigDecimal(item.getTakerGets().getAmount())) == -1) {
                            item.setOfferStatus("offer_partially_funded");
                            Effect effect = new Effect();
                            effect.setType("offer_remained");
                            effect.setTakerGets(new AmountObj(takerGets.valueText(), getAssetName(takerGets), takerGets.issuerString()));
                            effect.setTakerPays(new AmountObj(takerPays.valueText(), getAssetName(takerPays), takerPays.issuerString()));
                            showEffects.add(effect);
                            item.setPartiallyGets(new AmountObj(new BigDecimal(String.valueOf(item.getTakerGets().getAmount())).subtract(takerGets.value()).toPlainString(), getAssetName(takerGets), takerGets.issuerString()));
                            item.setPartiallyPays(new AmountObj(new BigDecimal(String.valueOf(item.getTakerPays().getAmount())).subtract(takerPays.value()).toPlainString(), getAssetName(takerPays), takerPays.issuerString()));
                        } else {
                            item.setOfferStatus("offer_create");
                        }
                    }
                }
            }
        }
    }

    /**
     * other currency payment, contains highLimit and lowLimit
     * it stands for tx amount's currency's balance changes
     */
    private static void parserRippleState(STObject field, AffectedNode node, STObject preFields, String address, List<Effect> effects, TxObj item) {
        if (item.getType() != null && item.getType().startsWith("active")) {
            return;
        }
        Amount highLimit = (Amount) field.get(Field.HighLimit);
        Amount lowLimit = (Amount) field.get(Field.LowLimit);
        Amount balance = (Amount) field.get(Field.Balance);
        Amount balanceChange = null;
        Amount preBalance = null;
        Amount reserve = (Amount) field.get(Field.Reserve);
        if (node.isCreatedNode()) {
            balanceChange = balance;
            if (reserve != null) {
                balanceChange = balanceChange.add(reserve);
                balance = balance.add(reserve);
            }
        } else if (preFields != null) {
            preBalance = (Amount) preFields.get(Field.Balance);
            Amount preReserve = (Amount) preFields.get(Field.Reserve);
            if (preBalance != null || preReserve != null) {
                if (preBalance != null)
                    balanceChange = balance.subtract(preBalance);
                else {
                    balanceChange = balance.subtract(balance);
                }
                if (reserve != null) {
                    balance = balance.add(reserve);
                    if (preReserve != null) {
                        balanceChange = balanceChange.add(reserve.subtract(preReserve));
                        if (reserve.isZero() && preReserve.isZero()) {
                            logger.info("tx-malformed", "tx-reserve", address, item.getHash());
                        }
                    }
                } else {
                    if (preReserve != null) {
                        balanceChange = balanceChange.subtract(preReserve);
                    }
                }
            }
        }
        if (highLimit == null || lowLimit == null || balanceChange == null) {
            return;
        }
        if (highLimit.issuerString().equals(address) || lowLimit.issuerString().equals(address)) {
            String issuer = lowLimit.issuerString();
            String account = highLimit.issuerString();
            if (highLimit.value().intValue() == 0 && (balance.doubleValue() > 0 || (preBalance != null && preBalance.doubleValue() > 0))) {
                //highlimit.limit=0, and (balance > 0 or prebalance > 0) shows the issuer is highlimit.issuer
                issuer = highLimit.issuerString();
                account = lowLimit.issuerString();
            }
            Effect effect = new Effect();
            //Account balance change
            if (address.equals(account)) {
                if (highLimit.issuerString().equals(address)) {
                    effect.setAmount(new AmountObj(balanceChange.negate().valueText(), getAssetName(balance.currencyString(), issuer), issuer, account));
                } else if (lowLimit.issuerString().equals(address)) {
                    //paths tx
                    effect.setAmount(new AmountObj(balanceChange.valueText(), getAssetName(balance.currencyString(), issuer), issuer, account));
                }
                effect.setBalance(new AmountObj(balance.abs().valueText(), getAssetName(balance.currencyString(), issuer), issuer, account));
                if (balance.currency().equals(org.sanchain.core.Currency.ASSET)) {
                    effect.setType("asset");
                } else {
                    effect.setType("amount");
                }
                effects.add(effect);
            } else {
                //gateway account, rippling.
                if (lowLimit.issuerString().equals(issuer)) {
                    effect.setAmount(new AmountObj(balanceChange.negate().valueText(), getAssetName(balance.currencyString(), issuer), account));
                    effect.setBalance(new AmountObj(balance.negate().valueText(), getAssetName(balance.currencyString(), issuer), account));
                } else {
                    effect.setAmount(new AmountObj(balanceChange.valueText(), getAssetName(balance.currencyString(), issuer), account));
                    effect.setBalance(new AmountObj(balance.valueText(), getAssetName(balance.currencyString(), issuer), account));
                }
                if (balance.currency().equals(org.sanchain.core.Currency.ASSET)) {
                    effect.setType("asset");
                } else {
                    effect.setType("amount");
                }
                effects.add(effect);
            }
        }
    }

    public static void parserAccountRoot(STObject field, AffectedNode node, STObject preFields, String address, List<Effect> effects, TxObj item) {
        AccountID account = (AccountID) field.get(Field.Account);
        if (account == null || !address.equals(account.address)) {
            return;
        }
        //XRPbalance changes
        Amount balance = (Amount) field.get(Field.Balance);
        BigDecimal balanceChange = new BigDecimal(0);
        if (node.isCreatedNode()) {
            //new fields
            if (balance != null)
                balanceChange = balance.value();
        } else if (preFields != null) {
            //has prefields
            Amount preBalance = (Amount) preFields.get(Field.Balance);
            if (preBalance != null)
                balanceChange = balance.subtract(preBalance).value();

            if (item.getType().equals("set_regular_key")) {
                AccountID regularKey = (AccountID) preFields.get(Field.RegularKey);
                if (regularKey != null) {
                    item.setRecipient(regularKey.address);
                    item.setType("cancel_regular_key");
                }
            }
        }
        if (balanceChange.abs().doubleValue() > 0) {
            //XRP changes, then should know if only fee changes
            //if sender is current user, then add fee effects
            if (item.getSender().equals(address)) {
                if (balanceChange.abs().compareTo(new BigDecimal(item.getFee().getAmount())) == 1) {
                    //not  only fee changes
                    Effect effect = new Effect();
                    balanceChange = balanceChange.add(new BigDecimal(item.getFee().getAmount()));
                    effect.setAmount(new AmountObj(balanceChange.toPlainString(), "SAN", "-"));
                    effect.setBalance(new AmountObj(balance.add(new BigDecimal(item.getFee().getAmount())).valueText(), "SAN", "-"));
                    effect.setType("amount");
                    effects.add(effect);
                }
                Effect effect = new Effect();
                effect.setAmount(new AmountObj(new BigDecimal(item.getFee().getAmount()).negate().toPlainString(), "SAN", "-"));
                effect.setBalance(new AmountObj(balance.valueText(), "SAN", "-"));
                effect.setType("fee");
                effects.add(effect);

            } else {
                //only XRP balance change...
                Effect effect = new Effect();
                effect.setAmount(new AmountObj(balanceChange.toPlainString(), "SAN", "-"));
                effect.setBalance(new AmountObj(balance.valueText(), "SAN", "-"));
                effect.setType("amount");
                effects.add(effect);
            }

        }
    }

    private static Map<String, List<AmountObj>> analyzeFeeTakers(TransactionMeta metaObj) {
        Map<String, List<AmountObj>> shares = new HashMap<>();
        if (metaObj.has(Field.FeeShareTakers)) {
            STArray takers = (STArray) metaObj.get(Field.FeeShareTakers);
            for (int j = 0; j < takers.size(); j++) {
                STObject taker = (STObject) takers.get(j).get(Field.FeeShareTaker);
                Amount amount = (Amount) taker.get(Field.Amount);
                AccountID account = (AccountID) taker.get(Field.Account);
                if (shares.containsKey(account.address)) {
                    shares.get(account.address).add(new AmountObj(amount.valueText(), amount.currencyString(), amount.issuerString()));
                } else {
                    ArrayList<AmountObj> amounts = new ArrayList<>();
                    amounts.add(new AmountObj(amount.valueText(), amount.currencyString(), amount.issuerString()));
                    shares.put(account.address, amounts);
                }
            }
        }
        return shares;
    }

    public static List<Effect> dealFeeShareEffects(TransactionMeta meta, TxObj item, List<Effect> effects, String address) {
        Map<String, List<AmountObj>> shares = analyzeFeeTakers(meta);
        if (meta.has(Field.FeeShareTakers)) {
            if (item.getType().equals("rippling") || item.getType().equals("offercreate")) {
                List<Effect> newEff = new ArrayList<>();
                for (Effect effect : effects) {
                    // shares contains account
                    if (shares.containsKey(address)) {
                        List<AmountObj> list = shares.get(address);
                        for (int j = 0; j < list.size(); j++) {
                            AmountObj amount = list.get(j);
                            if (new BigDecimal(effect.getAmount().getAmount()).subtract(new BigDecimal(amount.getAmount())).abs().compareTo(new BigDecimal("0.000000001")) == -1  //same amount
                                    && effect.getAmount().getIssuer().equals(amount.getIssuer()) //same issuer
                                    && !effect.getAmount().getIssuer().equals(address)) { //not gateway
                                item.setType("fee_share");
                                newEff.add(effect);
                            }
                        }
                    }
                }
                if (item.getType().equals("fee_share")) {
                    effects = newEff;
                }
            }
        }
        return effects;
    }

    private static String getAssetName(Amount amount) {
        return getAssetName(amount.currencyString(), amount.issuerString());
    }

    /**
     * 获取资产代号
     *
     * @param currency
     * @param issuer
     * @return
     */
    private static String getAssetName(String currency, String issuer) {
        String res = currency;
        if (currency.equals("ASSET")) {
            res = assetMap.containsKey(issuer) ? assetMap.get(issuer) : "ASSET";
        }
        logger.info("RetCur:" + res);
        return res;
    }

    //----------------------------------------------USER SEQUENCE-------------------------------------------------------

    /**
     * GET address current sequence number
     *
     * @return seqence number
     */
    public static int getSequence(String address) throws APIException {
        try {
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("id", 0);
            requestData.put("command", "account_info");
            requestData.put("account", address);
            String data = new Gson().toJson(requestData);
            String accountInfo = CoralWebSocketClient.request(data);
            JSONObject json = new JSONObject(accountInfo);
            String status = json.getString("status");
            if (!status.equalsIgnoreCase("success")) {  //result not success
                throw new APIException(APIException.ErrorCode.REMOTE_SERVER_BUSY, "GET sequence error, " + accountInfo);
            }
            JSONObject jsonAccountData = json.getJSONObject("result").getJSONObject("account_data");
            int sequence = jsonAccountData.getInt("Sequence");
            return sequence;
        } catch (Exception e) {
            if (e instanceof APIException) {
                throw e;
            } else {
                logger.error("GET " + address + "'s sequence error.", e);
                throw new APIException(APIException.ErrorCode.INTERNAL_ERROR, "GET sequence error.");
            }
        }
    }
    //---------------------------------------------ACCOUNT INFO---------------------------------------------------------

    /**
     * load account info from rippled
     *
     * @param address
     * @return
     * @throws APIException
     */
    public static String getAccountInfo(String address) throws APIException {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("id", 0);
        requestData.put("command", "account_info");
        requestData.put("account", address);
        requestData.put("strict", true);
        requestData.put("ledger_index", "validated");
        String data = new Gson().toJson(requestData);
        String accountInfo = CoralWebSocketClient.request(data);
        return accountInfo;
    }

    /**
     * load account info from rippled
     *
     * @param address
     * @return
     * @throws APIException
     */
    public static String getAccountInfo(String address, Integer ledgerIndex) throws APIException {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("id", 0);
        requestData.put("command", "account_info");
        requestData.put("account", address);
        requestData.put("strict", true);
        requestData.put("ledger_index", ledgerIndex);
        String data = new Gson().toJson(requestData);
        String accountInfo = CoralWebSocketClient.request(data);
        return accountInfo;
    }


    /**
     * check if address is activated
     *
     * @param address
     * @return 0--unactivated 1--activated  2--unknown
     */
    public static int checkCoralAccount(String address) {
        if (StringUtils.isBlank(address)) {
            return 0;
        }
        try {
            String res = getAccountInfo(address);
            logger.info("checkCoralAccount:address=" + address + ",res=" + res);
            return 1;
        } catch (APIException e) {
            logger.info("checkCoralAccount res:address=" + address + "Exception:" + e.getMessage());
            if (e.code.compareTo(APIException.ErrorCode.ADDRESS_NOT_FOUND) == 0) {
                return 0;
            }
            return 2;
        } catch (Exception e) {
            logger.error("checkCoralAccount Exception:address=" + address + ",msg=" + e.getMessage(), e);
            return 2;
        }
    }

    //-----------------------------------------OFFERS-------------------------------------------------------------------

    /**
     * load offers
     *
     * @param baseCurrency    base currency
     * @param baseIssuer      gateway address of base currency
     * @param counterCurrency counter currency
     * @param counterIssuer   gateway address of counter currency
     * @param limit           pagesize
     * @return value of #{@link JSONArray}
     * @throws APIException
     */
    public static JSONArray bookOffers(
            String baseCurrency,
            String baseIssuer,
            String counterCurrency,
            String counterIssuer,
            Integer limit) throws APIException {
        if (limit == null || limit <= 0) {
            limit = 20;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("id", 0);
        data.put("command", "book_offers");
        data.put("limit", limit);
        Map<String, Object> mapTakerGets = new HashMap<>();
        if (baseCurrency.equals("ASSET")) {
            mapTakerGets.put("currency", org.sanchain.core.Currency.fromString(baseCurrency).toHex());
        } else
            mapTakerGets.put("currency", baseCurrency);
        if (baseIssuer != null) {
            mapTakerGets.put("issuer", baseIssuer);
        }
        Map<String, Object> mapTakerPays = new HashMap<>();
        mapTakerPays.put("currency", counterCurrency);
        if (counterIssuer != null) {
            mapTakerPays.put("issuer", counterIssuer);
        }
        data.put("taker_gets", mapTakerGets);
        data.put("taker_pays", mapTakerPays);

        String postData = new Gson().toJson(data);
        String jsonStrAsk = CoralWebSocketClient.request(postData);
        if (jsonStrAsk != null) {
            JSONObject json = new JSONObject(jsonStrAsk);  //check success
            if (json.getString("status").equalsIgnoreCase("success")) {
                JSONObject jsonResult = json.getJSONObject("result");
                JSONArray jsonArray = jsonResult.getJSONArray("offers");
                return jsonArray;
            }
        }
        return new JSONArray();
    }

    public static String accountOffers(String address, int limit, String marker) throws APIException {
        Map<String, Object> data = new HashMap<>();
        data.put("id", 0);
        data.put("command", "account_offers");
        data.put("account", address);
        data.put("ledger", "current");
        if (limit >= 10 && limit <= 400) {
            data.put("limit", limit);
        }

        if (marker != null) {
            data.put("marker", marker);
        }
        String postData = new Gson().toJson(data);
        String jsonResult = CoralWebSocketClient.request(postData);
        JSONObject jsonObject = new JSONObject(jsonResult);
        if (jsonObject.getString("status").equalsIgnoreCase("success")) {
            JSONObject result = new JSONObject();
            JSONArray jsonArray = jsonObject.getJSONObject("result").getJSONArray("offers");
            if (jsonObject.getJSONObject("result").has("marker")) {
                String markerReturn = jsonObject.getJSONObject("result").getString("marker");
                result.put("marker", markerReturn);
            }

            result.put("offers", jsonArray);
            return result.toString();
        } else {
            throw new APIException(APIException.ErrorCode.UNKNOWN_ERROR, "unknow error");
        }
    }

    //-------------------------------------------ACCOUNT LINES----------------------------------------------------------


    public static String getAccountLines(String address, String peer, int limit) {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("id", 0);
        requestData.put("command", "account_lines");
        requestData.put("account", address);
        requestData.put("ledger", "current");
        if(peer != null){
            requestData.put("peer", peer);
        }
        if(limit <=0 || limit > 200){
            limit = 50;
        }
        requestData.put("limit", limit);
        String data = new Gson().toJson(requestData);
        System.out.println("getAccountLines="+data);
        String accountLines;
        try {
            accountLines = CoralWebSocketClient.request(data);
            return accountLines;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


    public static BigDecimal getAccountBalance(String address, String currency, String gateway) {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("id", 0);
        requestData.put("command", "account_lines");
        requestData.put("account", address);
        requestData.put("ledger", "current");
        requestData.put("peer", gateway);
        String data = new Gson().toJson(requestData);
        String lines = null;
        try {
            lines = CoralWebSocketClient.request(data);
        } catch (Exception ex) {
        }
        if (StringUtils.isNoneBlank(lines)) {
            JSONArray lineArray = new JSONObject(lines).getJSONObject("result").getJSONArray("lines");
            for (int i = 0; i < lineArray.length(); i++) {
                JSONObject line = lineArray.getJSONObject(i);
                if (line.getString("currency").equals(currency)) {
                    return new BigDecimal(line.getString("balance"));
                }
            }
        }
        return new BigDecimal(0);
    }
}
