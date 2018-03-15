package com.aaman.jung;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import javax.swing.JFrame;

import org.apache.batik.dom.GenericDOMImplementation;

import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsio.svg.SVGGraphics2D;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class JungGraph {

	
    private final ObjectMapper objectMapper;

	
	public JungGraph() {
	     objectMapper = new ObjectMapper();
	     objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	}


	public  String generateJSONGraph(final String cql,final String uri,final String user,final String password)
			throws IOException, SQLException {
		
		return renderJSONGraph(loadJungGraph(cql, uri, user, password));
	}

	public  String generateSVGGraph(final String cql,final String uri,final String user,final String password)
			throws IOException, SQLException {
			
		return renderSVGGraph(loadJungGraph(cql, uri, user, password));
			
	}

	/**
	 * Function to load a JUNG graph object with values from Neo4J
	 * @param cql
	 * @param uri
	 * @param user
	 * @param password
	 * 
	 * @throws SQLException
	 */
	private DirectedSparseGraph<NodeInfo,String> loadJungGraph(final String cql, 
			final String uri, final String user, final String password) throws SQLException {
		
		DirectedSparseGraph<NodeInfo,String> graph = new DirectedSparseGraph<>();
		try(Connection con= DriverManager.getConnection(uri, user, password)){
			try (Statement stmt = con.createStatement()){
				 try (ResultSet rs = stmt.executeQuery(cql)) {
			            while (rs.next()) {
			        	    		Map<String, Object> person = (Map<String, Object>) rs.getObject("person");
			        	       		Map<String, Object> movie = (Map<String, Object>) rs.getObject("movie");
			   			                

				              	String targetNode = movie.get("title").toString();
					        	  	String sourceNode = person.get("name").toString();
					        	  	String tagline="";

					        	  	String released = movie.get("released").toString();
					        	  	int born =  ((Long)person.get("born")).intValue();
					        	  	String rel = sourceNode + "-ACTED_IN-"+ targetNode;
					        	  
					        	  	MovieVertex mv = new MovieVertex(targetNode,tagline,released,"Movie");
					        	  	PersonVertex pv = new PersonVertex(sourceNode,born,"Person");
					        	  	
					        	  	graph.addVertex(pv);
					        	  
					        	  	graph.addVertex(mv);
					        	  	graph.addEdge(rel, pv, mv);				         
					     }
			        }
			  }
		}
		return graph;
	}


	private String renderJSONGraph(DirectedSparseGraph<NodeInfo, String> g) throws IOException {
	
		String JSONGraph="";
		ISOMLayout<NodeInfo,String> layout = new ISOMLayout<>(g);
		Dimension viewerDim = new Dimension(800,800);
		Rectangle viewerRect = new Rectangle(viewerDim);
	    VisualizationViewer<NodeInfo,String> vv =
	      new VisualizationViewer<>(layout, viewerDim);
	    GraphElementAccessor<NodeInfo, String> pickSupport = 
	            vv.getPickSupport();
	        Collection<NodeInfo> vertices = 
	            pickSupport.getVertices(layout, viewerRect);
	        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
	        for (NodeInfo vertex: vertices) {
        			System.out.println(vertex.toString()+"-->"+vertex.getType());
        			System.out.println(layout.getX(vertex)+"--"+layout.getY(vertex));
        			//print JSON version of vertex
            		JSONGraph += objectMapper.writeValueAsString(vertex);
	        }
        		
	        //print vertices collection as JSON array
	        String verticesJSON = objectMapper.writeValueAsString(vertices);
	        JSONGraph += verticesJSON;
	        return JSONGraph;
	}
	
	
	private String renderSVGGraph(DirectedSparseGraph<NodeInfo, String> g) throws IOException {
		String svgResult="";
		ISOMLayout<NodeInfo,String> layout = new ISOMLayout<>(g);
		Dimension viewerDim = new Dimension(800,800);
	
	    VisualizationViewer<NodeInfo,String> vv =
	      new VisualizationViewer<>(layout, viewerDim);
	       
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
    

	    // Get a DOMImplementation.
	    DOMImplementation domImpl =
	      GenericDOMImplementation.getDOMImplementation();
	
	    // Create an instance of org.w3c.dom.Document.
	    String svgNS = "http://www.w3.org/2000/svg";
	    Document document = domImpl.createDocument(svgNS, "svg", null);
	
	    // Create an instance of the SVG Generator.
	    org.apache.batik.svggen.SVGGraphics2D svgGenerator = new org.apache.batik.svggen.SVGGraphics2D(document);
	
	    svgGenerator.setSVGCanvasSize(viewerDim);
	    vv.print(svgGenerator);
	    // The following code adds capability for mouse picking of vertices/edges. Vertices can even be moved!
	    final DefaultModalGraphMouse<String,Number> graphMouse = new DefaultModalGraphMouse<>();
	    vv.setGraphMouse(graphMouse);
	    graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
	 	      
		  JFrame frame = new JFrame();
		  frame.getContentPane().add(vv);
		  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		  frame.pack();
		  frame.setVisible(true);
		  
		  // create svg from Visualization
		  Properties p = new Properties(); 
		  p.setProperty("PageSize","A5"); 
		  String svgURI = "/Users/aamanlamba/Downloads/Output.svg";
		  File svgOutput = new File(svgURI);
		  if(svgOutput.exists())
		  		svgOutput.delete();
		  VectorGraphics vg = new SVGGraphics2D(svgOutput,
		  			viewerDim);
		  vg.setProperties(p); 
		  vg.startExport(); 
		  vv.print(vg); 
		  vg.endExport();
		  //ugly way of getting the svg into a string - from the file
		  FileInputStream fis = new FileInputStream(svgOutput);
		  try( BufferedReader br =
		          new BufferedReader( new InputStreamReader(fis, "UTF-8" )))
		  {
		     StringBuilder sb = new StringBuilder();
		     String line;
		     while(( line = br.readLine()) != null ) {
		        sb.append( line );
		        sb.append( '\n' );
		     	}
	     		svgResult= sb.toString();
	  		}
	
	    		return svgResult;
		}

}
