package org.helium.framework.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

public class HashedServiceConsistent<T> implements HashedService<T>{

    private final HashFunction hashFunction = new HashFunction();
    private final int numberOfReplicas;  // 虚拟节点
    private final SortedMap<Long, T> circle = new TreeMap<Long, T>();   // 用来存储虚拟节点hash值 到真实node的映射

    public HashedServiceConsistent(int numberOfReplicas, Collection<T> nodes) {
        this.numberOfReplicas = numberOfReplicas;
        for (T node : nodes) {
            add(node);
        }
    }

    public void add(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
        	if (node instanceof ServiceMatchResult){
        		ServiceMatchResult nodeResult = (ServiceMatchResult) node;
				circle.put(hashFunction.hash(nodeResult.getServerUrl().getUrl() + i), node);
			}

        }
    }

    public void remove(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
			if (node instanceof ServiceMatchResult){
				ServiceMatchResult nodeResult = (ServiceMatchResult) node;
				circle.remove(hashFunction.hash(nodeResult.getServerUrl().getUrl() + i), node);
			}
        }
    }

    /**
     * 获得一个最近的顺时针节点
     *
     * @param key 为给定键取Hash，取得顺时针方向上最近的一个虚拟节点对应的实际节点
     * @return
     */
    @Override
    public T get(Object key) {
        if (circle.isEmpty()) {
            return null;
        }
        long hash = hashFunction.hash((String) key);
        if (!circle.containsKey(hash)) {
            SortedMap<Long, T> tailMap = circle.tailMap(hash); ////返回此映射的部分视图，其键大于等于 hash
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(hash);
    }

    public long getSize() {
        return circle.size();
    }


    public static class HashFunction {
        private MessageDigest md5 = null;

        public long hash(String key) {
            if (md5 == null) {
                try {
                    md5 = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException e) {
                    throw new IllegalStateException("no md5 algorythm found");
                }
            }

            md5.reset();
            md5.update(key.getBytes());
            byte[] bKey = md5.digest();
            long res = ((long) (bKey[3] & 0xFF) << 24) | ((long) (bKey[2] & 0xFF) << 16) | ((long) (bKey[1] & 0xFF) << 8)
                    | (long) (bKey[0] & 0xFF);
            return res & 0xffffffffL;
        }

    }
}