package org.helium.framework.logging;

import javafx.scene.Parent;
import org.slf4j.Marker;

import java.util.Iterator;
import java.util.List;

/**
 * Marker的几个用法
 * 1. Marker应该是Singleton
 * 2.
 * Created by Coral on 8/31/15.
 */
public class SimpleMarker implements Marker {
	private String name;

	public SimpleMarker(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void add(Marker reference) {
	}

	@Override
	public boolean remove(Marker reference) {
		return false;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public boolean hasReferences() {
		return false;
	}

	@Override
	public Iterator<Marker> iterator() {
		return null;
	}

	@Override
	public boolean contains(Marker other) {
		return false;
	}

	@Override
	public boolean contains(String name) {
		return false;
	}
}
