package org.helium.util;

/**
 * 4元组
 * Created by Coral on 8/26/15.
 */
public class Tuple4<E1, E2, E3, E4> {
	private E1 v1;
	private E2 v2;
	private E3 v3;
	private E4 v4;

	public Tuple4(E1 v1, E2 v2, E3 v3, E4 v4) {
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
		this.v4 = v4;
	}

	public E1 getV1() {
		return v1;
	}

	public Tuple4 setV1(E1 v1) {
		this.v1 = v1;
		return this;
	}

	public E2 getV2() {
		return v2;
	}

	public Tuple4 setV2(E2 v2) {
		this.v2 = v2;
		return this;
	}

	public E3 getV3() {
		return v3;
	}

	public Tuple4 setV3(E3 v3) {
		this.v3 = v3;
		return this;
	}

	public E4 getV4() {
		return v4;
	}

	public Tuple4 setV4(E4 v4) {
		this.v4 = v4;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Tuple4<?, ?, ?, ?> tuple4 = (Tuple4<?, ?, ?, ?>) o;

		if (v1 != null ? !v1.equals(tuple4.v1) : tuple4.v1 != null) return false;
		if (v2 != null ? !v2.equals(tuple4.v2) : tuple4.v2 != null) return false;
		if (v3 != null ? !v3.equals(tuple4.v3) : tuple4.v3 != null) return false;
		return !(v4 != null ? !v4.equals(tuple4.v4) : tuple4.v4 != null);

	}

	@Override
	public int hashCode() {
		int result = v1 != null ? v1.hashCode() : 0;
		result = 31 * result + (v2 != null ? v2.hashCode() : 0);
		result = 31 * result + (v3 != null ? v3.hashCode() : 0);
		result = 31 * result + (v4 != null ? v4.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Tuple4{" +
				"v1=" + v1 +
				", v2=" + v2 +
				", v3=" + v3 +
				", v4=" + v4 +
				'}';
	}
}
