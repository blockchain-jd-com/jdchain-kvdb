package com.jd.blockchain.kvdb.server.executor;

import com.jd.blockchain.kvdb.protocol.proto.Message;
import com.jd.blockchain.kvdb.protocol.proto.impl.KVDBMessage;
import com.jd.blockchain.kvdb.server.Request;

import utils.io.BytesUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.jd.blockchain.kvdb.protocol.proto.Command.COMMAND_DROP_DATABASE;

@KVDBExecutor(command = COMMAND_DROP_DATABASE)
public class DropDatabaseExecutor implements Executor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DropDatabaseExecutor.class);

    @Override
    public Message execute(Request request) {
        LOGGER.debug("{}-{} execute drop databases: {}", request.getSession().getId(), request.getId(), request.getCommand().getParameters());
        try {
            String database = BytesUtils.toString(request.getCommand().getParameters()[0].toBytes());
            request.getServerContext().dropDatabase(database);

            return KVDBMessage.success(request.getId());
        } catch (Exception e) {
            LOGGER.error("{}-{} execute drop databases", request.getSession().getId(), request.getId(), e);

            return KVDBMessage.error(request.getId(), e.getMessage());
        }
    }
}
