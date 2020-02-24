package com.jd.blockchain.kvdb.service.rocksdb;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jd.blockchain.kvdb.service.DBInstance;
import com.jd.blockchain.kvdb.service.KVWriteBatch;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.io.FileUtils;

public class RocksDBCluster extends DBInstance {

	public static final Logger LOGGER = LoggerFactory.getLogger(RocksDBCluster.class);

	private Partitioner partitioner;

	private String rootPath;

	private RocksDBProxy[] dbPartitions;

	private ExecutorService executor;

	private RocksDBCluster(String rootPath, RocksDBProxy[] dbPartitions, ExecutorService executor) {
		this.rootPath = rootPath;
		this.dbPartitions = dbPartitions;
		this.executor = executor;
		this.partitioner = new SimpleMurmur3HashPartitioner(dbPartitions.length);
	}

	public static RocksDBCluster open(String path, int partitions) {
		String rootPath = FileUtils.getFullPath(path);
		RocksDBProxy[] dbPartitions = new RocksDBProxy[partitions];
		for (int i = 0; i < partitions; i++) {
			String partitionPath = rootPath + File.separator + "parti-" + i;
			FileUtils.makeDirectory(partitionPath);
			dbPartitions[i] = RocksDBProxy.open(partitionPath);
		}

		ExecutorService executor = Executors.newFixedThreadPool(partitions);

		return new RocksDBCluster(rootPath, dbPartitions, executor);
	}

	@Override
	public void set(String key, String value) {
		set(BytesUtils.toBytes(key), BytesUtils.toBytes(value));
	}

	@Override
	public void set(byte[] key, byte[] value) {
		int pid = partitioner.partition(key);
		dbPartitions[pid].set(key, value);
	}

	@Override
	public byte[] get(byte[] key) {
		int pid = partitioner.partition(key);
		return dbPartitions[pid].get(key);
	}

	@Override
	public String get(String key) {
		return BytesUtils.toString(get(BytesUtils.toBytes(key)));
	}

	@Override
	public KVWriteBatch beginBatch() {
		KVWritePartition[] taskPartitions = new KVWritePartition[dbPartitions.length];
		for (int i = 0; i < dbPartitions.length; i++) {
			taskPartitions[i] = new KVWritePartition(dbPartitions[i].beginBatch(), executor);
		}
		return new KVWriteClusterBatch(taskPartitions, partitioner);
	}

	@Override
	public synchronized void close() {
		if (dbPartitions != null) {
			try {
				executor.shutdown();
			} catch (Exception e) {
				LOGGER.error("Error occurred while closing rocksdb cluster[" + rootPath + "]", e);
			}

			for (int i = 0; i < dbPartitions.length; i++) {
				dbPartitions[i].close();
			}

			dbPartitions = null;
		}
	}

	@Override
	public synchronized void drop() {
		if (dbPartitions != null) {
			RocksDBProxy[] dbs = dbPartitions;

			close();

			for (int i = 0; i < dbs.length; i++) {
				dbs[i].drop();
			}

			try {
				FileUtils.deleteFile(rootPath);
			} catch (Exception e) {
				LOGGER.error("Error occurred while dropping rocksdb cluster[" + rootPath + "]", e);
			}
		}

	}

}