package org.helium.logging.spi;

import java.util.List;
import java.util.function.Function;

/**
 * Created by Coral on 9/10/15.
 */
class LinkChain<E> {
	private E value;
	private LinkChain<E> next;

	public LinkChain(E value) {
		this.value = value;
	}

	public void addNext(E value) {
		LinkChain header = this;
		while (header.next != null) {
			header = header.next;
		}
		header.next = new LinkChain(value);
	}

	public LinkChain<E> getNext() {
		return next;
	}

	public void foreach(Function<E, Boolean> func) {
		for (LinkChain<E> p = this; p != null; p = p.next) {
			if (!func.apply(value)) {
				break;
			}
		}
	}

	public static <E> LinkChain<E> fromList(List<E> list) {
		if (list == null || list.size() == 0) {
			return null;
		}

		LinkChain header = new LinkChain(list.get(0));
		LinkChain p = header;
		for (int i = 1; i < list.size(); i++) {
			p.next = new LinkChain(list.get(i));
			p = p.next;
		}
		return header;
	}
}
