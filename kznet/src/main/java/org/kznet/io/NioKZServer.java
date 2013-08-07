package org.kznet.io;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.kznet.KZServer;

public class NioKZServer implements KZServer {
	private ServerSocketChannel serverChannel;
	private List<SocketChannel> channels = new CopyOnWriteArrayList<>();
	
	public NioKZServer() throws IOException {
		serverChannel = ServerSocketChannel.open();
	}
	
	@Override
	public boolean isOpen() {
		return serverChannel.isOpen();
	}
	
	@Override
	public synchronized void close() throws IOException {
		if(!isOpen())
			return;
		serverChannel.close();
	}


	
}
