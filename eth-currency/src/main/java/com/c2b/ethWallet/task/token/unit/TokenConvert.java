package com.c2b.ethWallet.task.token.unit;

import java.math.BigDecimal;

/**
 * Ethereum unit conversion functions.
 */
public final class TokenConvert {
    private TokenConvert() { }

    public static BigDecimal fromWei(String number,Unit unit) {
        return fromWei(new BigDecimal(number),unit);
    }

    public static BigDecimal fromWei(BigDecimal number,Unit unit) {
        return number.divide(unit.getWeiFactor());
    }

    public static BigDecimal toWei(String number,Unit unit) {
        return toWei(new BigDecimal(number),unit);
    }

    public static BigDecimal toWei(BigDecimal number,Unit unit) {
        return number.multiply(unit.getWeiFactor());
    }

    public enum Unit {

        UBC("ubc",10),
        ATC("atc",10),
    	DOC("doc",8),
    	CDT("cdt",18),
    	OMG("omg",18),
    	SNT("snt",18)
    	;

        private String name;
        private BigDecimal weiFactor;

        Unit(String name, int factor) {
            this.name = name;
            this.weiFactor = BigDecimal.TEN.pow(factor);
        }

        public BigDecimal getWeiFactor() {
            return weiFactor;
        }

        @Override
        public String toString() {
            return name;
        }
        

        public static Unit fromString(String name) {
            if (name != null) {
                for (Unit unit : Unit.values()) {
                    if (name.equalsIgnoreCase(unit.name)) {
                        return unit;
                    }
                }
            }
            return Unit.valueOf(name);
        }
    }
    
    public static void main(String[] args) {
		System.out.println(Unit.fromString("DOC"));
		
	}
}

