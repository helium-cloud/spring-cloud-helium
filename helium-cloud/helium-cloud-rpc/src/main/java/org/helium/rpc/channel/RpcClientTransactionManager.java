/*
 * FAE, Feinno App Engine
 *
 * Create by Coral 2011-1-19
 *
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;

import org.helium.threading.ExecutorFactory;
import org.helium.util.container.SessionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 * RPC客户端事务管理器,<br>
 * <li>管理客户端事务, 通过SessionPool</li><br>
 * <li>管理客户端访问缓存RpcClientMethodCache</li><br>
 * <p>
 * Created by Coral@feinno.com
 *
 * @see RpcClientTransaction
 * @see RpcClientMethodCache
 * @see RpcClientMethodCache
 */
public final class RpcClientTransactionManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcClientTransactionManager.class);
	public static final RpcClientTransactionManager INSTANCE = new RpcClientTransactionManager();
	public static final Executor CALLBACK_EXECUTOR = ExecutorFactory.newFixedExecutor("callback", 300, 6400);

	private SessionPool<RpcClientTransaction> txs;
	private Thread thread;


	private RpcClientTransactionManager() {
		txs = new SessionPool<RpcClientTransaction>();

		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				checkTransactions();
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	public int addTransaction(RpcClientTransaction tx) {
		return txs.add(tx);
	}

	public RpcClientTransaction getTransaction(int seq) {
		return txs.get(seq);
	}

	public RpcClientTransaction removeTransaction(int seq) {
		return txs.remove(seq);
	}

	public void checkTransactions() {
		while (true) {
			try {
				Thread.sleep(500);
				Function<RpcClientTransaction, Boolean> funcConnectionBroken = tx -> {
					if (tx.getConnection().isClosed()) {
						return true;
					} else {
						return false;
					}
				};

				Function<RpcClientTransaction, Boolean> funcTimeout = tx -> tx.isTimeout();

				for (Entry<Integer, RpcClientTransaction> k : txs.getAllItems(funcConnectionBroken)) {
					RpcResponse response = RpcResponse.createError(RpcReturnCode.CONNECTION_BROKEN, null, k.getValue().getCodecName());
					txs.remove(k.getKey());
					try {
						k.getValue().setResponse(response);
					} catch (Exception e) {
						LOGGER.error("setResponse failed{}", e);
					}
				}

				for (Entry<Integer, RpcClientTransaction> k : txs.getAllItems(funcTimeout)) {
					RpcResponse response = RpcResponse.createError(RpcReturnCode.TRANSACTION_TIMEOUT, null, k.getValue().getCodecName());
					txs.remove(k.getKey());
					try {
						k.getValue().setResponse(response);
					} catch (Exception e) {
						LOGGER.error("setResponse failed{}", e);
					}
				}
			} catch (InterruptedException e) {
				// nothing
			} catch (Exception e) {
				LOGGER.error("check transaction failed:", e);
			}
		}
	}

	;
}