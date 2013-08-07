package org.kznet.message;

public class DefaultMessage implements Message {
	private String[] destinations;
	private Object content;
	
	public DefaultMessage(Object content, String... destinations) {
		this.content = content;
		this.destinations = destinations;
	}

	public String[] getDestinations() {
		return destinations;
	}

	public Object getContent() {
		return content;
	}
}
