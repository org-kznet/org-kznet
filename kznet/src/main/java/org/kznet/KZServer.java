package org.kznet;

import java.io.Closeable;

public interface KZServer extends Closeable {
	public boolean isOpen();
}
