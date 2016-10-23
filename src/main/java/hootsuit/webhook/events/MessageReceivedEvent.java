package hootsuit.webhook.events;

import org.springframework.context.ApplicationEvent;

import hootsuit.webhook.model.Message;

public class MessageReceivedEvent extends ApplicationEvent {
	
	private static final long serialVersionUID = 1L;
	
	private Message message;

	public MessageReceivedEvent(Object source, Message message) {
		super(source);
		this.message = message;
	}

	public Message getMessage() {
		return message;
	}
	
}
