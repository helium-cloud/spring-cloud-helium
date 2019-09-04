package org.helium.util;

/**
 * 3元组
 * Created by Coral on 8/26/15.
 */
public class Combo3<E1, E2, E3> {
	private E1 v1;
	private E2 v2;
	private E3 v3;

	public Combo3(E1 v1, E2 v2, E3 v3) {
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
	}

	public E1 getV1() {
		return v1;
	}

	public Combo3 setV1(E1 v1) {
		this.v1 = v1;
		return this;
	}

	public E2 getV2() {
		return v2;
	}

	public Combo3 setV2(E2 v2) {
		this.v2 = v2;
		return this;
	}

	public E3 getV3() {
		return v3;
	}

	public Combo3 setV3(E3 v3) {
		this.v3 = v3;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Combo3<?, ?, ?> tuple3 = (Combo3<?, ?, ?>) o;

		if (v1 != null ? !v1.equals(tuple3.v1) : tuple3.v1 != null) return false;
		if (v2 != null ? !v2.equals(tuple3.v2) : tuple3.v2 != null) return false;
		return !(v3 != null ? !v3.equals(tuple3.v3) : tuple3.v3 != null);

	}

	@Override
	public int hashCode() {
		int result = v1 != null ? v1.hashCode() : 0;
		result = 31 * result + (v2 != null ? v2.hashCode() : 0);
		result = 31 * result + (v3 != null ? v3.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Tuple3{" +
				"v1=" + v1 +
				", v2=" + v2 +
				", v3=" + v3 +
				'}';
	}
}
