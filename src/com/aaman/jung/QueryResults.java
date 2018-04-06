/**
 * 
 */
package com.aaman.jung;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * @author aamanlamba
 *
 */
public class QueryResults {
	
	public List<Node> nodes;
	public List<Relationship> rels;
	
	QueryResults(){
		this.nodes = new ArrayList<>();
		this.rels = new ArrayList<>();
	}
	
}
