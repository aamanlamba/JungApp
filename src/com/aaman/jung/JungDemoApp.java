package com.aaman.jung;

import com.google.common.base.Supplier;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.Network;
import com.google.common.graph.NetworkBuilder;
import edu.uci.ics.jung.io.PajekNetReader;
//import edu.uci.ics.jung.samples.SimpleGraphDraw;
//import edu.uci.ics.jung.algorithms.layout.*;

import edu.uci.ics.jung.layout.algorithms.FRLayoutAlgorithm;
import edu.uci.ics.jung.layout.algorithms.LayoutAlgorithm;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.swing.*;

public class JungDemoApp {

 /* public static void main(String[] args) throws Exception {
	  final String cql2  = "MATCH (person)-[r:ACTED_IN]->(movie) RETURN person,movie limit 15";
	  final String uri = "jdbc:neo4j:bolt://localhost";
	  final String user="neo4j";
	  final String pwd="Frodo1";

	  	JungGraph jg = new JungGraph();
	
		 System.out.println(jg.generateJSONGraph(cql2,uri,user,pwd));
		 System.out.println( jg.generateSVGGraph(cql2,uri,user,pwd));
  }*/

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static void main(String[] args) throws IOException {
    JFrame jf = new JFrame();
    Network g = getGraph();
    LayoutAlgorithm layoutAlgorithm = new FRLayoutAlgorithm();
    VisualizationViewer vv = new VisualizationViewer(g, layoutAlgorithm, new Dimension(900, 900));
    jf.getContentPane().add(vv);
    jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    jf.pack();
    jf.setVisible(true);
  }

  /**
   * Generates a graph: in this case, reads it from the file (in the classpath):
   * "datasets/simple.net"
   *
   * @return A sample undirected graph
   * @throws IOException if there is an error in reading the file
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static Network getGraph() throws IOException {
    PajekNetReader pnr = new PajekNetReader(Object::new);

    MutableNetwork g = NetworkBuilder.undirected().build();
    Reader reader =
        new InputStreamReader(JungDemoApp.class.getResourceAsStream("/datasets/simple.net"));
    pnr.load(reader, (Supplier) g);
    return g;
  }

	
}