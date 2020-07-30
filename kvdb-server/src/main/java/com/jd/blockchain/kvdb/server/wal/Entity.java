package com.jd.blockchain.kvdb.server.wal;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.kvdb.protocol.Constants;

@DataContract(code = Constants.WAL_ENTITY)
public interface Entity {

    @DataField(order = 0, primitiveType = PrimitiveType.TEXT)
    String getDB();

    @DataField(order = 1, primitiveType = PrimitiveType.INT64)
    Long getLsn();

    @DataField(order = 2, refEnum = true)
    WalCommand getCommand();

    @DataField(order = 3, list = true, refContract = true)
    KV[] getKVs();

    void setLsn(Long lsn);
}
