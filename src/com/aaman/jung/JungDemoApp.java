package com.aaman.jung;

public class JungDemoApp {

  public static void main(String[] args) throws Exception {
	  final String cql2  = "MATCH (person)-[r:ACTED_IN]->(movie) RETURN person,movie limit 15";
	  final String uri = "jdbc:neo4j:bolt://localhost";
	  final String user="";
	  final String pwd="";

	  	JungGraph jg = new JungGraph();
	
		 System.out.println(jg.generateJSONGraph(cql2,uri,user,pwd));
		 System.out.println( jg.generateSVGGraph(cql2,uri,user,pwd));
}

	
}