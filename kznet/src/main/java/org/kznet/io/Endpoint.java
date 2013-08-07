package org.kznet.io;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.kznet.message.Message;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

class Endpoint implements Closeable {
	private SocketChannel ch;
	private Kryo kryo;
	private MessageQueue in;
	private MessageQueue out;
	private boolean open = true;
	
	public Endpoint(SocketChannel ch, Kryo kryo, MessageQueue in, MessageQueue out) {
		this.ch = ch;
		this.kryo = kryo;
		this.in = in;
		this.out = out;
	}
	
	public void start() {
		new Thread(new ReceiveTask()).start();
		new Thread(new SendTask()).start();
	}
	
	public void close() throws IOException {
		in.close();
		out.close();
		ch.close();
	}
	
	private Message receive() throws IOException {
		ByteBuffer buf = ByteBuffer.allocate(4);
		ch.read(buf);
		int size = buf.getInt(0);
		
		buf = ByteBuffer.allocate(size);
		ch.read(buf);
		
		Input input = new Input(buf.array());
		return (Message) kryo.readClassAndObject(input);
	}
	
	private void send(Message m) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		Output output = new Output(bout);
		kryo.writeClassAndObject(output, m);
		
		ByteBuffer buf = ByteBuffer.wrap(bout.toByteArray());
		ch.write(buf);
	}
	
	private class ReceiveTask implements Runnable {
		@Override
		public void run() {
			try {
				while(open) {
					out.offer(receive());
				}
			} catch(IOException ioe) {
				if(!open)
					return;
				throw new RuntimeException(ioe);
			} finally {
				try {
					close();
				} catch(IOException ioe) {
					throw new RuntimeException(ioe);
				}
			}
		}
	}
	
	private class SendTask implements Runnable {
		@Override
		public void run() {
			try {
				while(open) {
					Message m = in.take();
					if(m == null)
						close();
					else
						send(m);
				}
			} catch(IOException ioe) {
				if(!open)
					return;
				throw new RuntimeException(ioe);
			} catch(InterruptedException ie) {
				throw new RuntimeException(ie);
			} finally {
				try {
					close();
				} catch(IOException ioe) {
					throw new RuntimeException(ioe);
				}
			}
		}
	}
}
