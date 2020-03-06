package org.helium.framework.utils;

import org.helium.util.CollectionUtils;

import java.util.Random;

/**
 * Created by Coral on 8/8/15.
 */
public class RandomPicker<E> {
	private int totalWeight;
	private Item<E>[] items;

	public void add(int weight, E value) {
		synchronized (this) {
			items = CollectionUtils.appendArray(items, new Item(weight, value));
			calcTotalWeight();
		}
	}

	public void remove(E value) {
		synchronized (this) {
			items = CollectionUtils.removeArrayIf(items, v -> v.equals(value));
			calcTotalWeight();
		}
	}

	public int getWeight() {
		return totalWeight;
	}

	public E getItem() {
		int r = RAND.nextInt(totalWeight);
		int c = 0;
		for (int i = 0; i < items.length; i++) {
			c += items[i].weight;
			if (r < c) {
				return items[i].value;
			}
		}
		throw new RuntimeException("RandomPicker has a bug: " + String.format("totalWeight=%d, r=%d", totalWeight, c));
	}

	private void calcTotalWeight() {
		int sum = 0;
		for (int i = 0; i < items.length; i++) {
			sum += items.length;
		}
		totalWeight = sum;
	}

	private static class Item<E> {
		int weight;
		E value;
		Item(int weight, E value) {
			this.weight = weight;
			this.value = value;
		}
	}

	private static final Random RAND = new Random();
}
