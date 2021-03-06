package com.jd.blockchain.kvdb.server.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * kvdb-server配置
 */
public class KVDBConfig {

    // 默认数据库保存路径
    private static final String DEFAULT_DB_PATH = "../dbs";
    // 默认地址
    private static final String DEFAULT_HOST = "0.0.0.0";
    // 默认端口
    private static final int DEFAULT_PORT = 7078;
    // 管理工具服务默认端口
    private static final int DEFAULT_MANAGER_PORT = 7060;
    // 默认分片数
    private static final int DEFAULT_DB_PARTITIONS = 1;
    // 默认开启WAL
    private static final boolean DEFAULT_WAL_DISABLE = false;
    // 默认WAL刷盘机制
    private static final int DEFAULT_WAL_FLUSH = 1;

    // 服务器地址
    private String host;
    // 服务器端口
    private int port;
    // 管理工具服务端口
    private int managerPort;
    // 全局数据库保存目录
    private String dbsRootdir;
    // 全局数据实例分片数
    private int dbsPartitions;
    // 是否禁用WAL
    private boolean walDisable;
    // 刷盘机制：<=-1跟随系统，0实时刷盘，>0定时刷盘
    private int walFlush;

    public KVDBConfig(String configFile) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(configFile));
        this.host = properties.getProperty("server.host", DEFAULT_HOST);
        this.port = Integer.parseInt(properties.getProperty("server.port", String.valueOf(DEFAULT_PORT)));
        this.managerPort = Integer.parseInt(properties.getProperty("manager.port", String.valueOf(DEFAULT_MANAGER_PORT)));
        this.dbsRootdir = properties.getProperty("dbs.rootdir", DEFAULT_DB_PATH);
        this.dbsPartitions = Integer.parseInt(properties.getProperty("dbs.partitions", String.valueOf(DEFAULT_DB_PARTITIONS)));
        this.walDisable = Boolean.valueOf(properties.getProperty("wal.disable", String.valueOf(DEFAULT_WAL_DISABLE)));
        this.walFlush = Integer.parseInt(properties.getProperty("wal.flush", String.valueOf(DEFAULT_WAL_FLUSH)));
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getManagerPort() {
        return managerPort;
    }

    public String getDbsRootdir() {
        return dbsRootdir;
    }

    public int getDbsPartitions() {
        return dbsPartitions;
    }

    public boolean isWalDisable() {
        return walDisable;
    }

    public int getWalFlush() {
        return walFlush;
    }
}
