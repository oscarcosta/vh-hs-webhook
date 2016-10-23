package hootsuit.webhook.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Message {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false)
	private String messageBody;
	
	@Column(nullable = false)
	private String contentType;
	
	@Column(nullable = false)
	private Timestamp timestamp;
	
	@ManyToOne(optional = false)
	private Destination destination;
	
	protected Message() {		
	}
	
	public Message(String messageBody, String contentType, Destination destination) {
		super();
		this.messageBody = messageBody;
		this.contentType = contentType;
		this.timestamp = new Timestamp(System.currentTimeMillis());
		this.destination = destination;
	}

	public Long getId() {
		return id;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public String getContentType() {
		return contentType;
	}
	
	public Timestamp getTimestamp() {
		return timestamp;
	}

	public Destination getDestination() {
		return destination;
	}
	
	public Long getDestinationId() {
		return destination.getId();
	}
	
	public String getDestinationUrl() {
		return destination.getUrl(); 
	}
	
	public Boolean isDestinationOnline() {
		return destination.isOnline();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
    public String toString() {
        return String.format("Message[id=%d, messageBody='%s', contentType='%s']", id, messageBody, contentType);
    }
	
}
