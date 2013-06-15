package org.young.model;

import java.io.Serializable;

public class Entity implements Serializable {

	protected static final long serialVersionUID = 1L;

	protected String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
