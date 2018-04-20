package org.sanchain.client.api.exception;


/**
 * Created by A
 * since 14/12/19.
 */
public class APIException extends Exception{

    public static enum ErrorCode{
        INTERNAL_ERROR,
        UNKNOWN_ERROR,
        ADDRESS_NOT_FOUND,
        ADDRESS_FORMAT_MALFORMED,
        REMOTE_ERROR,
        MALFORMED_REQUEST_DATA,
        REMOTE_SERVER_BUSY,
        FUNTION_FORBIDDEN,
        SERVER_UNAVALIABLE,
        PERMISSION_DENIED,
        BANK_ADDRESS_NOT_FOUND,
        FORBIDDEN,
        NODE_NOT_FOUND
    }

    public ErrorCode code;
    public String message;

    public APIException(ErrorCode code, String message){
        super(message);
        this.code = code;
        this.message = message;
    }

    public APIException(String message){
        super(message);
        this.message = message;
    }
}
