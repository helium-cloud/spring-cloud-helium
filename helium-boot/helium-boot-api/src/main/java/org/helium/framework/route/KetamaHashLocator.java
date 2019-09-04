package org.helium.framework.route;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public final class KetamaHashLocator<E> {
	public static final Integer VIRTUAL_NODE_COUNT = 160; // why 160?

	// 添加日志记录
	private List<E> nodes;
	private TreeMap<Long, E> ketamaNodes;
	private int numReps;

	public KetamaHashLocator(List<E> nodes) {
		this(nodes, nodes.size() * VIRTUAL_NODE_COUNT);
	}

	public boolean nodesAreEqual(List<E> nodes) {
		if (this.nodes.size() != nodes.size()) {
			return false;
		} else {
			return nodes.equals(nodes);
		}
	}

	public KetamaHashLocator(List<E> nodes, int nodeCopies) {
		this.nodes = nodes;
		this.ketamaNodes = new TreeMap();
		this.numReps = nodeCopies;

		for (E node : nodes) {
			for (int i = 0; i < numReps / 4; i++) {
				byte[] digest = computeMd5(node.toString() + i);
				for (int h = 0; h < 4; h++) {
					long m = hash(digest, h);
					ketamaNodes.put(m, node);
				}
			}
		}
	}

	public KetamaHashLocator(E[] nodes) {
		this(Arrays.asList(nodes), nodes.length * VIRTUAL_NODE_COUNT);
	}

	public E getPrimary(final String k) {
		byte[] digest = computeMd5(k);
		E rv = getNodeForKey(hash(digest, 0));
		return rv;
	}

	public E getPrimary(final byte[] kBytes) {
		byte[] digest = computeMd5(kBytes);
		E rv = getNodeForKey(hash(digest, 0));
		return rv;
	}

	E getNodeForKey(long hash) {
		final E rv;
		Long key = hash;
		if (!ketamaNodes.containsKey(key)) {
			key = ketamaNodes.ceilingKey(key);
			if (key == null) {
				key = ketamaNodes.firstKey();
			}
		}

		rv = ketamaNodes.get(key);
		return rv;
	}

	private long hash(byte[] digest, int nTime) {
		long rv =
				((long) (digest[3 + nTime * 4] & 0xFF) << 24) |
				((long) (digest[2 + nTime * 4] & 0xFF) << 16) |
				((long) (digest[1 + nTime * 4] & 0xFF) << 8) |
				(digest[0 + nTime * 4] & 0xFF);

		return rv & 0xffffffffL; /* Truncate to 32-bits */
	}

	/**
	 * Get the md5 of the given key.
	 */
	private byte[] computeMd5(String k) {
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("MD5 not supported", e);
		}
		md5.reset();
		byte[] keyBytes = null;
		try {
			keyBytes = k.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unknown string :" + k, e);
		}

		md5.update(keyBytes);
		return md5.digest();
	}

	/**
	 * Get the md5 of the given key.
	 */
	private byte[] computeMd5(byte[] keyBytes) {
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("MD5 not supported", e);
		}
		md5.reset();

		md5.update(keyBytes);
		return md5.digest();
	}
}
