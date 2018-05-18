package com.c2b.coin.common;

/**
 * 消息格式化工具
 *
 * @author tangwei
 */
public class ParamMessageUtil {

    private static final char DELIM_START = '{';
    private static final char DELIM_STOP = '}';
    private static final char ESCAPE_CHAR = '\\';

    /**
     * 将字符串中的"{}"替换为对应的参数
     *
     * @param messagePattern
     * @param arguments
     * @return
     */
    public static String format(final String messagePattern, final Object... arguments) {
        if (messagePattern == null || arguments == null || arguments.length == 0) {
            return messagePattern;
        }

        final StringBuilder result = new StringBuilder();
        int escapeCounter = 0;
        int currentArgument = 0;
        for (int i = 0; i < messagePattern.length(); i++) {
            final char curChar = messagePattern.charAt(i);
            if (curChar == ESCAPE_CHAR) {
                escapeCounter++;
            } else {
                if (curChar == DELIM_START && i < messagePattern.length() - 1
                        && messagePattern.charAt(i + 1) == DELIM_STOP) {
                    // write escaped escape chars
                    final int escapedEscapes = escapeCounter / 2;
                    for (int j = 0; j < escapedEscapes; j++) {
                        result.append(ESCAPE_CHAR);
                    }

                    if (escapeCounter % 2 == 1) {
                        // i.e. escaped
                        // write escaped escape chars
                        result.append(DELIM_START);
                        result.append(DELIM_STOP);
                    } else {
                        // unescaped
                        if (currentArgument < arguments.length) {
                            result.append(arguments[currentArgument]);
                        } else {
                            result.append(DELIM_START).append(DELIM_STOP);
                        }
                        currentArgument++;
                    }
                    i++;
                    escapeCounter = 0;
                    continue;
                }
                // any other char beside ESCAPE or DELIM_START/STOP-combo
                // write unescaped escape chars
                if (escapeCounter > 0) {
                    for (int j = 0; j < escapeCounter; j++) {
                        result.append(ESCAPE_CHAR);
                    }
                    escapeCounter = 0;
                }
                result.append(curChar);
            }
        }
        return result.toString();
    }

}
