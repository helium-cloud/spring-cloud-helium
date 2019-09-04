package org.helium.http.client;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A {@link Map}-backed {@link Set}.
 *
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 *
 * @version $Rev: 2080 $, $Date: 2010-01-26 18:04:19 +0900 (Tue, 26 Jan 2010) $
 */
final class MapBackedSet<E> extends AbstractSet<E> implements Serializable {

	private static final long serialVersionUID = -6761513279741915432L;

	private final Map<E, Boolean> map;

	/**
	 * Creates a new instance which wraps the specified {@code map}.
	 */
	MapBackedSet(Map<E, Boolean> map) {
		this.map = map;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	@Override
	public boolean add(E o) {
		return map.put(o, Boolean.TRUE) == null;
	}

	@Override
	public boolean remove(Object o) {
		return map.remove(o) != null;
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}
}

