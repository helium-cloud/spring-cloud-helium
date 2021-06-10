
package org.helium.common.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * UnsafeByteArrayInputStream.
 */
public class UnsafeByteArrayInputStream extends InputStream {
    protected byte[] mData;

    protected int mPosition, mLimit, mMark = 0;

    public UnsafeByteArrayInputStream(byte[] buf) {
        this(buf, 0, buf.length);
    }

    public UnsafeByteArrayInputStream(byte[] buf, int offset) {
        this(buf, offset, buf.length - offset);
    }

    public UnsafeByteArrayInputStream(byte[] buf, int offset, int length) {
        mData = buf;
        mPosition = mMark = offset;
        mLimit = Math.min(offset + length, buf.length);
    }

    @Override
    public int read() {
        return (mPosition < mLimit) ? (mData[mPosition++] & 0xff) : -1;
    }

    @Override
    public int read(byte[] b, int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
        if (mPosition >= mLimit) {
            return -1;
        }
        if (mPosition + len > mLimit) {
            len = mLimit - mPosition;
        }
        if (len <= 0) {
            return 0;
        }
        System.arraycopy(mData, mPosition, b, off, len);
        mPosition += len;
        return len;
    }

    @Override
    public long skip(long len) {
        if (mPosition + len > mLimit) {
            len = mLimit - mPosition;
        }
        if (len <= 0) {
            return 0;
        }
        mPosition += len;
        return len;
    }

    @Override
    public int available() {
        return mLimit - mPosition;
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void mark(int readAheadLimit) {
        mMark = mPosition;
    }

    @Override
    public void reset() {
        mPosition = mMark;
    }

    @Override
    public void close() throws IOException {
    }

    public int position() {
        return mPosition;
    }

    public void position(int newPosition) {
        mPosition = newPosition;
    }

    public int size() {
        return mData == null ? 0 : mData.length;
    }
}
