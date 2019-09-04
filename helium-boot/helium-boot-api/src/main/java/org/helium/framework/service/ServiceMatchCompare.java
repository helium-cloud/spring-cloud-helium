package org.helium.framework.service;

import java.util.Comparator;

public class ServiceMatchCompare implements Comparator<ServiceMatchResult> {

	@Override
	public int compare(ServiceMatchResult o1, ServiceMatchResult o2) {
		return o1.getPriority() - o2.getPriority();
	}
}