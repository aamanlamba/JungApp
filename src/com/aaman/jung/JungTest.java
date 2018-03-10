package com.aaman.jung;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.renderers.Renderer.Vertex;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;

import org.apache.commons.collections4.Transformer;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.ogm.model.Result;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import static org.neo4j.driver.v1.Values.parameters;


public class JungTest implements AutoCloseable {

	private final Driver driver;
    private final ObjectMapper objectMapper;
    private final GraphDatabaseFactory dbFactory;
	
	
	public JungTest(String uri,String user, String password) {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
        objectMapper = new ObjectMapper();
        dbFactory = new GraphDatabaseFactory();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

	}
	
	@Override
	public void close() throws Exception
	{
	        driver.close();
	 }
	
	  public void printGreeting( final String message )
	  {
	        try ( Session session = driver.session() )
	        {
	            String greeting = session.writeTransaction( new TransactionWork<String>()
	            {
	                @Override
	                public String execute( Transaction tx )
	                {
	                    StatementResult result = tx.run( "MATCH p=(person)-[r:ACTED_IN]->(movie) "
	                    		+ "							RETURN person.name LIMIT 1",
	                            parameters( "message", message ) );
	                    return result.single().toString();
	                }
	            } );
	            System.out.println( greeting );
	        }
	    }
	  
	  private List<Map<String, Object>> read(String cql) {
		    List<Map<String, Object>> rows = new ArrayList<>();
		    try (Session session = driver.session()){
		         StatementResult result = session.run(cql);
		        		while (result.hasNext()) {
		        	  Record record = result.next();
		        	  String label = record.get(0).get("title").toString();
		        	  Map<String,Object> row = new HashMap<>();
		        	  row.put(label, record);
		        	  rows.add(row);		        	 
		        } 
		    }
		  return rows;
		  
		}
	
	   

  public static void main(String[] args) throws Exception {
	  final String cql = "Match (m:Movie) return m";
	  final String cql2  = "MATCH (person)-[r:ACTED_IN]->(movie) RETURN person,movie limit 15";
	  try ( JungTest greeter = new JungTest( "bolt://localhost:7687", "", "" ) )
      {
          greeter.printGreeting( "hello, world" );
      }
	  try ( JungTest grapher = new JungTest( "bolt://localhost:7687", "", "" ) )
      {
		  DirectedSparseGraph<NodeInfo,String> g = new DirectedSparseGraph<NodeInfo, String>();
		  List<Map<String, Object>> nodes = grapher.read(cql);
		  
		   try (Session session = grapher.driver.session()){
		         StatementResult result = session.run(cql2);
		        	while (result.hasNext()) {
		        	  Record record = result.next();
		        	  String targetNode = record.get(1).get("title").toString();
		        	  String sourceNode = record.get(0).get("name").toString();
		        	  
		        	  String tagline = record.get(1).get("tagline").toString();
		        	  String released = record.get(1).get("released").toString();
		        	  int born = record.get(0).get("born").asInt();
		        	  String rel = sourceNode + "-ACTED_IN-"+ targetNode;
		        	  
		        	 MovieVertex mv = new MovieVertex(targetNode,tagline,released,"Movie");
		        	 PersonVertex pv = new PersonVertex(sourceNode,born,"Person");
		        	  g.addVertex(pv);
		        	  
		        	  g.addVertex(mv);
		        	  g.addEdge(rel, pv, mv);
		        	  
		        } 
		    }		  
		/*  for (Map<String, Object> map : nodes) {
			    for (Map.Entry<String, Object> entry : map.entrySet()) {
			        String key = entry.getKey();
			        g.addVertex(key);
			       // Object value = entry.getValue();
			    }
			}*/
		  //   g.addEdge("Edge1", "Vertex1", "Vertex2");
		  // g.addEdge("Edge2", "Vertex1", "Vertex3");
	    //g.addEdge("Edge3", "Vertex3", "Vertex1");
		  grapher.renderGraph(g);
      }
}

private void getNodesEdges(String cql2) {
	// TODO Auto-generated method stub
   List<Map<String, Object>> rows = new ArrayList<>();
    try (Session session = driver.session()){
         StatementResult result = session.run(cql2);
         int i = 0;
        	while (result.hasNext()) {
        	  Record record = result.next();
        	  String sourceNode = record.get(0).get("name").toString();
        	  String targetNode = record.get(0).get("title").toString();
        	  String rel = "ACTED_IN";
        	  Map<String,Object> row = new HashMap<>();
        	  row.put(sourceNode, record);
        	  rows.add(row);		        	 
        } 
    }
    

}

private void renderGraph(DirectedSparseGraph<NodeInfo, String> g) throws JsonProcessingException {
	  //SpringLayout<String, String> layout = new SpringLayout<String,String>(g);
	//  layout.setForceMultiplier(0.75);
	  //layout.setRepulsionRange(100);
	  //layout.lock(true);
	//  KKLayout<String, String> layout = new KKLayout<String,String>(g);
	//  layout.setMaxIterations(100);
	 // layout.setAttractionMultiplier(0.75); // default 0.75
	  //layout.setRepulsionMultiplier(0.5); // default 0.75
	ISOMLayout<NodeInfo,String> layout = new ISOMLayout<NodeInfo,String>(g);
	Dimension viewerDim = new Dimension(800,800);
	Rectangle viewerRect = new Rectangle(viewerDim);
    VisualizationViewer<NodeInfo,String> vv =
      new VisualizationViewer<NodeInfo,String>(layout, viewerDim);
    GraphElementAccessor<NodeInfo, String> pickSupport = 
            vv.getPickSupport();
        Collection<NodeInfo> vertices = 
            pickSupport.getVertices(layout, viewerRect);
        
        
        //iterate and print vertices
        for (NodeInfo vertex: vertices) {
        		System.out.println(vertex.toString()+"-->"+vertex.getType());
        		System.out.println(layout.getX(vertex)+"--"+layout.getY(vertex));
        		//print JSON version of vertex
        		System.out.println(objectMapper.writeValueAsString(vertex));
        }
        //print vertices collection as JSON array
        String verticesJSON = objectMapper.writeValueAsString(vertices);
        System.out.println(verticesJSON);
        //TODO: print coordinates and vertices as JSON array
        
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());

      /*vv.getRenderContext().setEdgeLabelTransformer(new Transformer<NodeInfo,String>()  {

		@Override
		public String transform(NodeInfo arg0) {
			// TODO Auto-generated method stub
			String label = arg0.name;
			return label;
		}
      });*/
      
      // The following code adds capability for mouse picking of vertices/edges. Vertices can even be moved!
      final DefaultModalGraphMouse<String,Number> graphMouse = new DefaultModalGraphMouse<String,Number>();
      vv.setGraphMouse(graphMouse);
      graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
   	      
    JFrame frame = new JFrame();
    frame.getContentPane().add(vv);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }
	
}