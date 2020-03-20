package com.jd.blockchain.kvdb.server.handler;

import com.jd.blockchain.kvdb.protocol.Message;
import com.jd.blockchain.kvdb.protocol.KVDBMessage;
import com.jd.blockchain.kvdb.server.Request;

public class UnknowExecutor implements Executor {

    @Override
    public Message execute(Request request) {

        return KVDBMessage.error(request.getId(), "un support command: " + request.getCommand().getName());
    }
}
