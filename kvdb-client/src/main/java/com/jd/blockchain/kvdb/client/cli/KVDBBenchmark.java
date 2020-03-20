package com.jd.blockchain.kvdb.client.cli;

import com.jd.blockchain.kvdb.protocol.Message;
import com.jd.blockchain.kvdb.protocol.KVDBMessage;
import com.jd.blockchain.kvdb.client.ClientConfig;
import com.jd.blockchain.kvdb.client.KVDBClient;
import com.jd.blockchain.utils.ArgumentSet;
import com.jd.blockchain.utils.Bytes;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

public class KVDBBenchmark {

    private static final String HOST = "-h";
    private static final String PORT = "-p";
    private static final String CLIENTS = "-c";
    private static final String REQUESTS = "-n";
    private static final String BATCH = "-b";
    private static final String KEEPALIVE = "-k";
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 6380;
    private static final int DEFAULT_CLIENT = 20;
    private static final int DEFAULT_REQUESTS = 100000;
    private static final boolean DEFAULT_BATCH = false;
    private static final boolean DEFAULT_KEEP_ALIVE = true;

    private String host;
    private int port;
    private int clients;
    private int requests;
    private boolean batch;
    private boolean keepAlive;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getClients() {
        return clients;
    }

    public void setClients(int clients) {
        this.clients = clients;
    }

    public int getRequests() {
        return requests;
    }

    public void setRequests(int requests) {
        this.requests = requests;
    }

    public boolean isBatch() {
        return batch;
    }

    public void setBatch(boolean batch) {
        this.batch = batch;
    }

    public boolean getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public KVDBBenchmark(String[] args) {
        ArgumentSet arguments = ArgumentSet.resolve(args, ArgumentSet.setting().prefix(HOST, PORT, CLIENTS, REQUESTS, KEEPALIVE, BATCH));
        ArgumentSet.ArgEntry hostArg = arguments.getArg(HOST);
        if (null != hostArg) {
            this.host = hostArg.getValue();
        } else {
            this.host = DEFAULT_HOST;
        }
        ArgumentSet.ArgEntry portArg = arguments.getArg(PORT);
        if (null != hostArg) {
            this.port = Integer.valueOf(portArg.getValue());
        } else {
            this.port = DEFAULT_PORT;
        }
        ArgumentSet.ArgEntry clientsArg = arguments.getArg(CLIENTS);
        if (null != hostArg) {
            this.clients = Integer.valueOf(clientsArg.getValue());
        } else {
            this.clients = DEFAULT_CLIENT;
        }
        ArgumentSet.ArgEntry requestsArg = arguments.getArg(REQUESTS);
        if (null != hostArg) {
            this.requests = Integer.valueOf(requestsArg.getValue());
        } else {
            this.requests = DEFAULT_REQUESTS;
        }
        ArgumentSet.ArgEntry keepArg = arguments.getArg(KEEPALIVE);
        if (null != hostArg) {
            this.keepAlive = Integer.valueOf(keepArg.getValue()) == 1 ? true : false;
        } else {
            this.keepAlive = DEFAULT_KEEP_ALIVE;
        }
        ArgumentSet.ArgEntry batchArg = arguments.getArg(BATCH);
        if (null != hostArg) {
            this.batch = Integer.valueOf(batchArg.getValue()) == 1 ? true : false;
        } else {
            this.batch = DEFAULT_BATCH;
        }
    }

    public static void main(String[] args) {
        KVDBBenchmark bm = new KVDBBenchmark(args);
        ClientConfig config = new ClientConfig(bm.getHost(), bm.getPort(), bm.getKeepAlive());

        ArrayBlockingQueue queue = new ArrayBlockingQueue(bm.getRequests());
        for (int i = 0; i < bm.getRequests(); i++) {
            queue.add(KVDBMessage.put(Bytes.fromInt(2 * i), Bytes.fromInt(2 * i + 1)));
        }
        CountDownLatch startCdl = new CountDownLatch(1);
        CountDownLatch endCdl = new CountDownLatch(bm.getClients());
        for (int i = 0; i < bm.getClients(); i++) {
            final int index = i;
            new Thread(() -> {
                KVDBClient client = new KVDBClient(config);
                client.start();
                Message[] messages = new Message[bm.getRequests() / bm.getClients()];
                for (int j = 0; j < messages.length; j++) {
                    messages[j] = KVDBMessage.put(Bytes.fromString(String.valueOf(messages.length * index + j)), Bytes.fromInt(1));
                }
                if (bm.batch) {
                    client.send(KVDBMessage.batchBegin(), Integer.MIN_VALUE);
                }
                try {
                    startCdl.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (Message msg : messages) {
                    client.send(msg, Integer.MAX_VALUE);
                }
                if (bm.batch) {
                    client.send(KVDBMessage.batchCommit(), Integer.MIN_VALUE);
                }
                endCdl.countDown();
                client.stop();
            }).start();
        }

        long startTime = System.currentTimeMillis();
        startCdl.countDown();
        try {
            endCdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println(String.format("requests:%d, clients:%d, times:%dms, tps:%f",
                bm.getRequests(), bm.getClients(), endTime - startTime, bm.getRequests() / ((endTime - startTime) / 1000d)));

    }

}
