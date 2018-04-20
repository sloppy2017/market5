package org.sanchain.core.formats;

import org.sanchain.core.fields.Field;
import org.sanchain.core.serialized.enums.LedgerEntryType;

import java.util.EnumMap;

public class LEFormat extends Format {
    static public EnumMap<LedgerEntryType, LEFormat> formats = new EnumMap<LedgerEntryType, LEFormat>(LedgerEntryType.class);

    static public LEFormat fromString(String name) {
        return getLedgerFormat(LedgerEntryType.valueOf(name));
    }

    static public LEFormat fromNumber(Number ord) {
        return getLedgerFormat(LedgerEntryType.fromNumber(ord));
    }

    static public LEFormat fromValue(Object o) {
        if (o instanceof Number) {
            return fromNumber(((Number) o).intValue());
        } else if (o instanceof String){
            return fromString((String) o);
        }
        else {
            return null;
        }
    }

    public static LEFormat getLedgerFormat(LedgerEntryType key) {
        if (key == null) return null;
        return formats.get(key);
    }

    public final LedgerEntryType ledgerEntryType;

    public LEFormat(LedgerEntryType type, Object... args) {
        super(args);
        ledgerEntryType = type;
        addCommonFields();
        formats.put(type, this);
    }

    @Override
    public void addCommonFields() {
        put(Field.LedgerIndex,             Requirement.OPTIONAL);
        put(Field.LedgerEntryType,         Requirement.REQUIRED);
        put(Field.Flags,                   Requirement.REQUIRED);
    }

    public static LEFormat AccountRoot = new LEFormat(
            LedgerEntryType.AccountRoot,
            Field.Account,             Requirement.REQUIRED,
            Field.Sequence,            Requirement.REQUIRED,
            Field.Balance,             Requirement.REQUIRED,
            Field.OwnerCount,          Requirement.REQUIRED,
            Field.PreviousTxnID,       Requirement.REQUIRED,
            Field.PreviousTxnLgrSeq,   Requirement.REQUIRED,
            Field.RegularKey,          Requirement.OPTIONAL,
            Field.EmailHash,           Requirement.OPTIONAL,
            Field.WalletLocator,       Requirement.OPTIONAL,
            Field.WalletSize,          Requirement.OPTIONAL,
            Field.MessageKey,          Requirement.OPTIONAL,
            Field.TransferRate,        Requirement.OPTIONAL,
            Field.Domain,              Requirement.OPTIONAL
    );

    public static LEFormat Contract = new LEFormat(
            LedgerEntryType.Contract,
            Field.Account,             Requirement.REQUIRED,
            Field.Balance,             Requirement.REQUIRED,
            Field.PreviousTxnID,       Requirement.REQUIRED,
            Field.PreviousTxnLgrSeq,   Requirement.REQUIRED,
            Field.Issuer,              Requirement.REQUIRED,
            Field.Owner,               Requirement.REQUIRED,
            Field.Expiration,          Requirement.REQUIRED,
            Field.BondAmount,          Requirement.REQUIRED,
            Field.CreateCode,          Requirement.OPTIONAL,
            Field.FundCode,            Requirement.OPTIONAL,
            Field.RemoveCode,          Requirement.OPTIONAL,
            Field.ExpireCode,          Requirement.OPTIONAL
    );

    public static LEFormat DirectoryNode = new LEFormat(
            LedgerEntryType.DirectoryNode,
            Field.Owner,               Requirement.OPTIONAL,  // for owner directories
            Field.TakerPaysCurrency,   Requirement.OPTIONAL,  // for order book directories
            Field.TakerPaysIssuer,     Requirement.OPTIONAL,  // for order book directories
            Field.TakerGetsCurrency,   Requirement.OPTIONAL,  // for order book directories
            Field.TakerGetsIssuer,     Requirement.OPTIONAL,  // for order book directories
            Field.ExchangeRate,        Requirement.OPTIONAL,  // for order book directories
            Field.Indexes,             Requirement.REQUIRED,
            Field.RootIndex,           Requirement.REQUIRED,
            Field.IndexNext,           Requirement.OPTIONAL,
            Field.IndexPrevious,       Requirement.OPTIONAL
    );

    public static LEFormat GeneratorMap = new LEFormat(
            LedgerEntryType.GeneratorMap,
            Field.Generator,           Requirement.REQUIRED
    );

    public static LEFormat Offer = new LEFormat(
            LedgerEntryType.Offer,
            Field.Account,             Requirement.REQUIRED,
            Field.Sequence,            Requirement.REQUIRED,
            Field.TakerPays,           Requirement.REQUIRED,
            Field.TakerGets,           Requirement.REQUIRED,
            Field.BookDirectory,       Requirement.REQUIRED,
            Field.BookNode,            Requirement.REQUIRED,
            Field.OwnerNode,           Requirement.REQUIRED,
            Field.PreviousTxnID,       Requirement.REQUIRED,
            Field.PreviousTxnLgrSeq,   Requirement.REQUIRED,
            Field.Expiration,          Requirement.OPTIONAL
    );

    public static LEFormat Ticket = new LEFormat(
            LedgerEntryType.Ticket,
            Field.PreviousTxnID,       Requirement.REQUIRED,
            Field.PreviousTxnLgrSeq,   Requirement.REQUIRED,
            Field.Account,             Requirement.REQUIRED,
            Field.Sequence,            Requirement.REQUIRED,
            Field.OwnerNode,           Requirement.REQUIRED,
            Field.Target,              Requirement.OPTIONAL,
            Field.Expiration,          Requirement.OPTIONAL
    );

    public static LEFormat RippleState = new LEFormat(
            LedgerEntryType.RippleState,
            Field.Balance,             Requirement.REQUIRED,
            Field.LowLimit,            Requirement.REQUIRED,
            Field.HighLimit,           Requirement.REQUIRED,
            Field.PreviousTxnID,       Requirement.REQUIRED,
            Field.PreviousTxnLgrSeq,   Requirement.REQUIRED,
            Field.LowNode,             Requirement.OPTIONAL,
            Field.LowQualityIn,        Requirement.OPTIONAL,
            Field.LowQualityOut,       Requirement.OPTIONAL,
            Field.HighNode,            Requirement.OPTIONAL,
            Field.HighQualityIn,       Requirement.OPTIONAL,
            Field.HighQualityOut,      Requirement.OPTIONAL
    );

    public static LEFormat LedgerHashes = new LEFormat(
            LedgerEntryType.LedgerHashes,
            Field.FirstLedgerSequence, Requirement.OPTIONAL, // Remove if we do a ledger restart
            Field.LastLedgerSequence,  Requirement.OPTIONAL,
            Field.Hashes,              Requirement.REQUIRED
    );

    public static LEFormat EnabledAmendments = new LEFormat(
            LedgerEntryType.EnabledAmendments,
            Field.Features, Requirement.REQUIRED
    );

    public static LEFormat FeeSettings = new LEFormat(
            LedgerEntryType.FeeSettings,
            Field.BaseFee,             Requirement.REQUIRED,
            Field.ReferenceFeeUnits,   Requirement.REQUIRED,
            Field.ReserveBase,         Requirement.REQUIRED,
            Field.ReserveIncrement,    Requirement.REQUIRED
    );
}
