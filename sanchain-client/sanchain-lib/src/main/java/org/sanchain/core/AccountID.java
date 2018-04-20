package org.sanchain.core;

import org.sanchain.core.fields.TypedFields;
import org.sanchain.core.exception.CoralException;
import org.sanchain.core.fields.Field;
import org.sanchain.core.hash.B58;
import org.sanchain.core.hash.Hash160;
import org.sanchain.core.serialized.BinaryParser;
import org.sanchain.core.serialized.BytesSink;
import org.sanchain.core.serialized.TypeTranslator;
import org.sanchain.core.uint.UInt32;
import org.sanchain.crypto.ecdsa.IKeyPair;
import org.sanchain.crypto.ecdsa.Seed;
import org.ripple.bouncycastle.util.encoders.Hex;

import java.util.HashMap;
import java.util.Map;

public class AccountID extends Hash160 {
    final public String address;

    /**
     * Constructor of AccountID
     * @param bytes   MUST be SHA256_RIPEMD160 format
     */
    public AccountID(byte[] bytes) {
        this(bytes, encodeAddress(bytes));
    }

    public AccountID(byte[] bytes, String address) {
        super(bytes);
        this.address = address;
    }

    @Override
    public int hashCode() {
        return address.hashCode();
    }

    public static AccountID NEUTRAL,  SAN_ISSUER;

    static {
        SAN_ISSUER = fromInteger(0);
        NEUTRAL = fromInteger(1);
    }

    @Override
    public String toString() {
        return address;
    }

    //@Deprecated
    static public AccountID fromSeedBytes(byte[] seed) {
        return fromKeyPair(Seed.getKeyPair(seed));
    }

    public static AccountID fromKeyPair(IKeyPair kp) {
        byte[] bytes = kp.sha256_Ripemd160_Pub();
        return new AccountID(bytes, encodeAddress(bytes));
    }

    private static String encodeAddress(byte[] a) {
        if(a.length != 20)  // added by Fau
            throw new CoralException("encodeAddress() param length MUST be 20(RIPEMD160)!");
        return B58.getInstance().encodeAddress(a);
    }

    static public AccountID fromInteger(Integer n) {
        // The hash160 will extend the address
        return fromBytes(new Hash160(new UInt32(n).toByteArray()).bytes());
    }

    public static AccountID fromBytes(byte[] bytes) {
        return new AccountID(bytes, encodeAddress(bytes));
    }

    static public AccountID fromAddress(String address) throws CoralException {
        byte[] bytes = B58.getInstance().decodeAddress(address);
        return new AccountID(bytes, address);
    }

    static public AccountID fromAddressBytes(byte[] bytes) {
        return fromBytes(bytes);
    }

    public Issue issue(String code) {
        return new Issue(Currency.fromString(code), this);
    }

    @Override
    public Object toJSON() {
        return toString();
    }

    @Override
    public byte[] toBytes() {
        return translate.toBytes(this);
    }

    @Override
    public String toHex() {
        return translate.toHex(this);
    }

    @Override
    public void toBytesSink(BytesSink to) {
        to.add(bytes());
    }

    public boolean lessThan(AccountID from) {
        return compareTo(from) == -1;
    }

    public static class Translator extends TypeTranslator<AccountID> {
        @Override
        public AccountID fromParser(BinaryParser parser, Integer hint) {
            if (hint == null) {
                hint = 20;
            }
            return AccountID.fromAddressBytes(parser.read(hint));
        }

        @Override
        public String toString(AccountID obj) {
            return obj.toString();
        }

        @Override
        public AccountID fromString(String value) {
            return AccountID.fromString(value);
        }
    }

    public static AccountID fromString(String value) throws CoralException {
        if (value.length() == 160 / 4) {
            return fromAddressBytes(Hex.decode(value));
        } else {
            if (value.startsWith("r") && value.length() >= 26) {
                return fromAddress(value);
            }
            // This is potentially dangerous but fromString in
            // generic sense is used by Amount for parsing strings
            return accountForPassPhrase(value);
        }
    }

    static public Map<String, AccountID> accounts = new HashMap<String, AccountID>();

    public static AccountID accountForPassPhrase(String value) {

        if (accounts.get(value) == null) {
            accounts.put(value, accountForPass(value));
        }

        return accounts.get(value);
    }

    private static AccountID accountForPass(String value) {
        return AccountID.fromSeedBytes(Seed.passPhraseToSeedBytes(value));
    }

    static {
        accounts.put("root", accountForPass("masterpassphrase"));
    }

    public boolean isNativeIssuer() {
        return equals(SAN_ISSUER);
    }

    static public Translator translate = new Translator();

    public static TypedFields.AccountIDField accountField(final Field f) {
        return new TypedFields.AccountIDField() {
            @Override
            public Field getField() {
                return f;
            }
        };
    }

    static public TypedFields.AccountIDField Account = accountField(Field.Account);
    static public TypedFields.AccountIDField Owner = accountField(Field.Owner);
    static public TypedFields.AccountIDField Destination = accountField(Field.Destination);
    static public TypedFields.AccountIDField Issuer = accountField(Field.Issuer);
    static public TypedFields.AccountIDField Target = accountField(Field.Target);
    static public TypedFields.AccountIDField RegularKey = accountField(Field.RegularKey);
}
