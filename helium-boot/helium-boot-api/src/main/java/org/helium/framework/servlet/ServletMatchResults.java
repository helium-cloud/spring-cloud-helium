package org.helium.framework.servlet;

import org.helium.util.IntegerReference;
import org.helium.util.Tuple;
import org.helium.util.Tuple2;
import org.helium.util.Tuple3;
import org.helium.framework.BeanContext;
import org.helium.framework.BeanIdentity;
import org.helium.framework.route.BeanEndpoint;


import java.beans.BeanDescriptor;
import java.util.*;
import java.util.function.Function;

/**
 * 不负责计算结果, 所有的结果都是参与计算的
 * Created by Coral on 8/8/15.
 */
public class ServletMatchResults {
	private static final Random RAND = new Random();
	private List<ServletMatchResult> results;

	public ServletMatchResults() {
	}

	public ServletMatchResults(ServletMatchResult result) {
		this();
		addResult(result);
	}

	/**
	 * 是否至少命中了一个结果
	 * @return
	 */
	public boolean hasResult() {
		return results != null;
	}

	/**
	 * 返回计算后的结果
	 * @param
	 * @return
	 */
	public BeanEndpoint getServletEndpoint() {
		if (results == null) {
			return null;
		}
		int totalWeight = 0;
		BeanContext defaultBean = null;
		List<Tuple<Integer, BeanEndpoint>> list = new ArrayList<>();
		for (ServletMatchResult mr: results) {
			if (defaultBean == null) {
				defaultBean = mr.getRouter().getBeanContext();
			}
			int w = mr.getRouter().getWeight();
			if (w > 0) {
				BeanEndpoint e = mr.getRouter().routeBean();
				totalWeight += w;
				list.add(new Tuple<>(w, e));
			}
		}
		if (list.size() == 0) {
			return new BeanEndpoint(defaultBean, null);
		}
		int n = 0;
		int rand = RAND.nextInt(totalWeight);
		for (int i = 0; i < list.size(); i++) {
			n += list.get(i).getV1();
			if (rand < n) {
				return list.get(i).getV2();
			}
		}
		throw new RuntimeException("getServletEndpoint with a BUG!!");
	}

	/**
	 * for Rcs-Service-Adapter
	 * @return
	 */
	public BeanEndpoint[] getServletEndpoints() {
		if (results == null) {
			return null;
		}
		Map<BeanIdentity, Tuple3<Integer, List<Tuple2<Integer, BeanEndpoint>>, BeanContext>> map = new HashMap<>();
		for (ServletMatchResult mr: results) {
			Tuple3<Integer, List<Tuple2<Integer, BeanEndpoint>>, BeanContext> t3;
			BeanContext bc = mr.getRouter().getBeanContext();
			t3 = map.get(bc.getId());
			if (t3 == null) {
				t3 = new Tuple3<>(0, new ArrayList<>(), bc);
				map.put(bc.getId(), t3);
			}

			int w = mr.getRouter().getWeight();
			if (w > 0) {
				BeanEndpoint e = mr.getRouter().routeBean();
				e.setPriority(mr.getPriority());
				t3.setV1(t3.getV1() + w);
				t3.getV2().add(new Tuple2<>(w, e));
			}
		}
		if (map.size() == 0) {
			return new BeanEndpoint[0];
		}
		BeanEndpoint[] r = new BeanEndpoint[map.size()];
		IntegerReference j = new IntegerReference(0);
		map.forEach((k, t3) -> {
			int n = 0;
			if (t3.getV1() > 0) {
				int rand = RAND.nextInt(t3.getV1());
				BeanEndpoint e = null;
				for (int i = 0; i < t3.getV2().size(); i++) {
					n += t3.getV2().get(i).getV1();
					if (rand < n) {
						e = t3.getV2().get(i).getV2();
					}
				}
				r[j.value++] = e != null ? e : new BeanEndpoint(t3.getV3(), null);
			} else {
				r[j.value++] = new BeanEndpoint(t3.getV3(), null);
			}
		});
		return r;
	}

	/**
	 * 获取全部结果
	 * @return
	 */
	public List<ServletMatchResult> getResults() {
		return results;
	}

	/**
	 * 增加一个结果
	 * @param r
	 */
	public void addResult(ServletMatchResult r) {
		if (results == null) {
			results = new ArrayList<>();
		}
		results.add(r);
	}

	public void addResults(ServletMatchResults lv) {
		if (lv.results != null) {
			lv.results.forEach(a -> addResult(a));
		}
	}

	public void applyFilter(ServletMatchResult.Filter filter) {
		if (results != null) {
			results.removeIf(r -> !filter.applyFirst(r));
			results.removeIf(r -> !filter.applyLast(r));
		}
	}

	public void applyFilterLast(ServletMatchResult.Filter filter) {
		if (results != null) {
			results.removeIf(r -> !filter.applyLast(r));
		}
	}

	public void applyExperimentFilter() {
		if (results == null) {
			return;
		}
		//
		// 判断是否存在灰度发布的节点
		boolean hasExperiment = false;
		for (ServletMatchResult mr: results) {
			if (mr.isExperiment()) {
				hasExperiment = true;
			}
		}

		if (hasExperiment) {
			//
			// 移除所有的非灰度节点, 剩余的灰度节点放在一起随机
			results.removeIf(mr -> !mr.isExperiment());
		}
	}
}
