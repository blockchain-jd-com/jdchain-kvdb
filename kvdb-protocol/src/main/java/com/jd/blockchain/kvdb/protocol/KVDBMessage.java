package com.jd.blockchain.kvdb.protocol;

import com.jd.blockchain.utils.Bytes;

import java.util.UUID;

import static com.jd.blockchain.kvdb.protocol.Command.CommandType.*;

public class KVDBMessage implements Message {

    private String id;

    private MessageContent content;

    public KVDBMessage(MessageContent content) {
        this(UUID.randomUUID().toString(), content);
    }

    public KVDBMessage(String id, MessageContent content) {
        this.id = id;
        this.content = content;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setContent(MessageContent content) {
        this.content = content;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public MessageContent getContent() {
        return content;
    }

    //----------- Messages for request

    public static Message use(Bytes key) {
        return new KVDBMessage(new KVDBCommand(USE.getCommand(), key));
    }

    public static Message createDB(Bytes key) {
        return new KVDBMessage(new KVDBCommand(CREATE_DATABASE.getCommand(), key));
    }

    public static Message info() {
        return new KVDBMessage(new KVDBCommand(INFO.getCommand()));
    }

    public static Message showDBs() {
        return new KVDBMessage(new KVDBCommand(SHOW_DATABASES.getCommand()));
    }

    public static Message put(Bytes... kvs) {
        return new KVDBMessage(new KVDBCommand(PUT.getCommand(), kvs));
    }

    public static Message get(Bytes... keys) {
        return new KVDBMessage(new KVDBCommand(GET.getCommand(), keys));
    }

    public static Message exists(Bytes... keys) {
        return new KVDBMessage(new KVDBCommand(EXISTS.getCommand(), keys));
    }

    public static Message batchBegin(Bytes... kvs) {
        return new KVDBMessage(new KVDBCommand(BATCH_BEGIN.getCommand(), kvs));
    }

    public static Message batchAbort(Bytes... kvs) {
        return new KVDBMessage(new KVDBCommand(BATCH_ABORT.getCommand(), kvs));
    }

    public static Message batchCommit(Bytes... kvs) {
        return new KVDBMessage(new KVDBCommand(BATCH_COMMIT.getCommand(), kvs));
    }

    //----------- Messages for response

    public static Message success(String id, Bytes... value) {
        return new KVDBMessage(id, new KVDBResponse(Constants.SUCCESS, value));
    }

    public static Message error(String id, String description) {
        return new KVDBMessage(id, new KVDBResponse(Constants.ERROR, Bytes.fromString(description)));
    }

    public static Message error(String id, byte[] description) {
        return new KVDBMessage(id, new KVDBResponse(Constants.ERROR, new Bytes(description)));
    }
}
