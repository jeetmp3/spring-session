package grails.plugin.springsession;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

public class FakeHttpSession extends HashMap<String, Object> implements HttpSession {
	@Override
	public long getCreationTime() {
		return 0;
	}

	@Override
	public String getId() {
		return "fake";
	}

	@Override
	public long getLastAccessedTime() {
		return 0;
	}

	@Override
	public ServletContext getServletContext() {
		return null;
	}

	@Override
	public void setMaxInactiveInterval(int interval) {

	}

	@Override
	public int getMaxInactiveInterval() {
		return 0;
	}

	@Override
	public HttpSessionContext getSessionContext() {
		return null;
	}

	@Override
	public Object getAttribute(String name) {
		return get(name);
	}

	@Override
	public Object getValue(String name) {
		return get(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return new Vector(keySet()).elements();
	}

	@Override
	public String[] getValueNames() {
		return (String[]) keySet().toArray();
	}

	@Override
	public void setAttribute(String name, Object value) {
		put(name, value);
	}

	@Override
	public void putValue(String name, Object value) {
		put(name, value);
	}

	@Override
	public void removeAttribute(String name) {
		remove(name);
	}

	@Override
	public void removeValue(String name) {
		remove(name);
	}

	@Override
	public void invalidate() {
		clear();
	}

	@Override
	public boolean isNew() {
		return false;
	}
}
