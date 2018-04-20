package com.c2b.config.mybatis;

/**
 * Created by ZhenWeiLai on 2016/11/22.
 */
public class DataSourceContextHolder {
    private static final ThreadLocal<String> dataSourceLocal = new ThreadLocal<>();

    public static ThreadLocal<String> getDataSourceLocal() {
        return dataSourceLocal;
    }

    /**
     * 主库 只有一个
     */
    public static void write() {
        dataSourceLocal.set(TargetDataSource.WRITE.getCode());
    }

    public static String getTargetDataSource() {
        return dataSourceLocal.get();
    }

}
