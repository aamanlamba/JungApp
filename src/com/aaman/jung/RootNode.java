package com.aaman.jung;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class RootNode {
	protected String name="";
	protected String id="";
	protected String type="";
	protected RootNode() {
		name="";
		id="";
		type="";
	}
	protected RootNode(String name,String id,String type) {
		this.name=name;
		this.id=id;
		this.type = type;
	}
	protected String getName() {
		return name;
	}
	protected void setName(String name) {
		this.name = name;
	}
	protected String getId() {
		return id;
	}
	protected void setId(String id) {
		this.id = id;
	}
	protected String getType() {
		return type;
	}
	protected void setType(String type) {
		this.type = type;
	}
	
	  @Override
	    public boolean equals(Object o) {

	        if (o == this) return true;
	        if (!(o instanceof RootNode)) {
	            return false;
	        }

	        RootNode rn = (RootNode) o;

	        return new EqualsBuilder()
	                .append(id, rn.id)
	                .isEquals();
	    }
	  
	  @Override
	    public int hashCode() {
	        return new HashCodeBuilder(17, 37)
	                .append(id)
	                .toHashCode();
	    }
}
