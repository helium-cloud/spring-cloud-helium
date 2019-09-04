package org.helium.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 这是一个链表节点, value == null是合理的，表示当前这个节点不存在任何的值
 * 仅存在value == null && next == null的场景
 *
 * Created by Coral on 9/20/15.
 */
public class LinkedNode<E> {
	private E value;
	private LinkedNode<E> next;

	public LinkedNode(E value) {
		if (value == null) {
			throw new IllegalArgumentException("value can't be null");
		}
		this.value = value;
	}

	public E getValue() {
		return value;
	}

	public LinkedNode<E> getNext() {
		return next;
	}

	/**
	 * 在链表结尾添加一个值
	 * @param value
	 */
	public void add(E value) {
		if (value == null) {
			throw new IllegalArgumentException("value can't be null");
		}

		if (this.value == null) {
			this.value = value;
			return;
		}

		LinkedNode header = this;
		while (header.next != null) {
			header = header.next;
		}
		header.next = new LinkedNode(value);
	}

	/**
	 * 移除链表中的一个值
	 * @param value
	 */
	public void remove(E value) {
		if (value == null) {
			throw new IllegalArgumentException("value can't be null");
		}
		removeIf(a -> a.equals(value));
	}

	/**
	 * 移除链表中的所有值
	 */
	public void clear() {
		value = null;
		next = null;
	}

	/**
	 * 移除
	 * @param func
	 */
	public void removeIf(Predicate<E> func) {
		List<E> list = toList();
		list.removeIf(a -> func.test(a));
		clear();
		list.forEach(this::add);
	}

	/**
	 * 转成列表
	 * @return
	 */
	public List<E> toList() {
		List<E> list = new ArrayList();
		forEach(a -> list.add(a));
		return list;
	}

	/**
	 * 遍历链表中的所有值
	 * @param action
	 */
	public void forEach(Consumer<? super E> action) {
		if (value == null) {
			return;
		}

		LinkedNode<E> header = LinkedNode.this;
		while (header.next != null) {
			action.accept(header.value);
			header = header.next;
		}
	}
}
