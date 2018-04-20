package org.sanchain.client.api.model;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.LongUnaryOperator;

/**
 * Created by ac
 * since 15/6/18.
 */
public class HttpServer extends ReentrantLock{
    private String url;
    private AtomicLong failed = new AtomicLong(0);
    private int status = 1;
    public static final int MAX_RETRY_TIMES = 10;
    private final Marker marker = new Marker();

    public HttpServer(String url){
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public AtomicLong getFailed() {
        return failed;
    }

    public void setFailed(AtomicLong failed) {
        this.failed = failed;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        if(status == 1)
            failed.set(0);
    }

    public long updateStatus(){
        return failed.getAndUpdate(marker);
    }

    private class Marker implements LongUnaryOperator {
        @Override
        public long applyAsLong(long operand) {
            if(operand > MAX_RETRY_TIMES){
                status = 0;
            }
            return ++operand;
        }
    }
}
