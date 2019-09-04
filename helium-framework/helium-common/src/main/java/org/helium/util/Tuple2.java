package org.helium.util;

/**
 * Created by Coral on 8/26/15.
 */
public class Tuple2<E1, E2> {
	private E1 v1;
	private E2 v2;

	public Tuple2(E1 v1, E2 v2) {
		this.v1 = v1;
		this.v2 = v2;
	}

	public E1 getV1() {
		return v1;
	}

	public Tuple2 setV1(E1 v1) {
		this.v1 = v1;
		return this;
	}

	public E2 getV2() {
		return v2;
	}

	public Tuple2 setV2(E2 v2) {
		this.v2 = v2;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;

		if (v1 != null ? !v1.equals(tuple2.v1) : tuple2.v1 != null) return false;
		return !(v2 != null ? !v2.equals(tuple2.v2) : tuple2.v2 != null);

	}

	@Override
	public int hashCode() {
		int result = v1 != null ? v1.hashCode() : 0;
		result = 31 * result + (v2 != null ? v2.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Tuple2{" +
				"v1=" + v1 +
				", v2=" + v2 +
				'}';
	}
}
