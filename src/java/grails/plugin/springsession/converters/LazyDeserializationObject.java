package grails.plugin.springsession.converters;

import java.io.Serializable;

public class LazyDeserializationObject implements Serializable {

	private static final long serialVersionUID = 1L;

	private byte[] serialized;
	private transient Object deserializaded;

	public LazyDeserializationObject() {
	}

	public LazyDeserializationObject(byte[] serialized) {
		this.serialized = serialized;
	}

	public LazyDeserializationObject(Object deserializaded) {
		this.deserializaded = deserializaded;
	}

	public LazyDeserializationObject(byte[] serialized, Object deserializaded) {
		this.serialized = serialized;
		this.deserializaded = deserializaded;
	}

	public byte[] getSerialized() {
		return serialized;
	}

	public void setSerialized(byte[] serialized) {
		this.serialized = serialized;
	}

	public Object getDeserializaded() {
		return deserializaded;
	}

	public void setDeserializaded(Object deserializaded) {
		this.deserializaded = deserializaded;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		LazyDeserializationObject that = (LazyDeserializationObject) o;

		return deserializaded != null ? deserializaded.equals(that.deserializaded) : that.deserializaded == null;
	}

	@Override
	public int hashCode() {
		return deserializaded != null ? deserializaded.hashCode() : 0;
	}
}
