package org.kznet.io;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.kznet.message.Message;

class MessageQueue {
	private Queue<Message> queue = new ArrayDeque<>();
	private boolean open = true;
	
	public void offer(Message m) {
		if(m == null)
			throw new NullPointerException();
		if(!open)
			return;
		synchronized(queue) {
			queue.offer(m);
			queue.notifyAll();
		}
	}
	
	public Message poll() {
		synchronized(queue) {
			return queue.poll();
		}
	}
	
	public Message take() throws InterruptedException {
		synchronized(queue) {
			while(queue.size() == 0 && open)
				queue.wait();
			return queue.poll();
		}
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public void close() {
		synchronized(queue) {
			open = false;
			queue.notifyAll();
		}
	}
}
