package jadx.core.utils;

import java.util.BitSet;

public class EmptyBitSet extends BitSet {

	public EmptyBitSet() {
		super(0);
	}

	@Override
	public int cardinality() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public int nextSetBit(int fromIndex) {
		return -1;
	}

	@Override
	public int length() {
		return 0;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public void set(int bitIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(int bitIndex, boolean value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(int fromIndex, int toIndex, boolean value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean get(int bitIndex) {
		return false;
	}

	@Override
	public BitSet get(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void and(BitSet set) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void or(BitSet set) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void xor(BitSet set) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void andNot(BitSet set) {
		throw new UnsupportedOperationException();
	}
}
