package com.aaman.jung;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JToggleButton;

import com.google.common.base.Function;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.util.RandomLocationTransformer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.picking.PickedInfo;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import edu.uci.ics.jung.visualization.util.Animator;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;

import com.google.common.base.Functions;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.PolarPoint;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.event.GraphEventListener;
import edu.uci.ics.jung.graph.util.Graphs;

import edu.uci.ics.jung.visualization.renderers.GradientVertexRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;


@SuppressWarnings("unused")
public class SimpleJung3 extends javax.swing.JApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6361953436371324015L;

	private Graph<Number,Number> g = null;

	private VisualizationViewer<Number,Number> vv = null;
    VisualizationViewer<String,Integer> vv2;

	private AbstractLayout<Number,Number> layout = null;
 

	Timer timer;
	public static final int EDGE_LENGTH = 100;

	boolean done;

	protected JButton switchLayout;
	   Forest<String,Integer> graph;
    Supplier<DirectedGraph<String,Integer>> graphFactory = 
    	new Supplier<DirectedGraph<String,Integer>>() {

			public DirectedGraph<String, Integer> get() {
				return new DirectedSparseMultigraph<String,Integer>();
			}
		};
			
		Supplier<Tree<String,Integer>> treeFactory =
		new Supplier<Tree<String,Integer>> () {

		public Tree<String, Integer> get() {
			return new DelegateTree<String,Integer>(graphFactory);
		}
	};
	
	Supplier<Integer> edgeFactory = new Supplier<Integer>() {
		int i=0;
		public Integer get() {
			return i++;
		}};
    
    Supplier<String> vertexFactory = new Supplier<String>() {
    	int i=0;
		public String get() {
			return "V"+i++;
		}};

    VisualizationServer.Paintable rings;
    
    String root;
    TreeLayout<String,Integer> treeLayout;
    
    RadialTreeLayout<String,Integer> radialLayout;

 

	LoadingCache<Number, Paint> vertexPaints =
			CacheBuilder.newBuilder().build(
					CacheLoader.from(Functions.<Paint>constant(Color.yellow))); 

	LoadingCache<Number, Paint> edgePaints =
			CacheBuilder.newBuilder().build(
					CacheLoader.from(Functions.<Paint>constant(Color.blue))); 
	public final Color[] similarColors =
		{
				new Color(216, 134, 134),
				new Color(135, 137, 211),
				new Color(134, 206, 189),
				new Color(206, 176, 134),
				new Color(194, 204, 134),
				new Color(145, 214, 134),
				new Color(133, 178, 209),
				new Color(103, 148, 255),
				new Color(60, 220, 220),
				new Color(30, 250, 100)
		};

	public static void main(String[] args) {

		SimpleJung3 and = new SimpleJung3();

		and.init();

		and.start();

		JFrame jf = new JFrame();
		jf.setTitle("Basic pickable FRLayout");

		addMenuToFrame(and, jf);
		
		jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jf.pack();
		jf.setVisible(true);	

	}
	/**
	 * @param and
	 * @param jf
	 */
	private static void addMenuToFrame(SimpleJung3 and, JFrame jf) {
		JMenuBar jmb = new JMenuBar();
		JMenu layouts = new JMenu("Layouts");
		JMenu file = new JMenu("File");
		JMenuItem basicLayout = new JMenuItem("Basic FR Layout");
		JMenuItem clusteredLayout = new JMenuItem("Clustered FR Layout");
		JMenuItem pickableLayout = new JMenuItem("Pickable Layout");
		JMenuItem L2RTreeLayout = new JMenuItem("L2R Tree Layout");
		JMenuItem svgSave = new JMenuItem("Save as SVG");
		JMenuItem exitItem = new JMenuItem("Exit");
		layouts.add(basicLayout);
		layouts.add(clusteredLayout);
		layouts.add(pickableLayout);
		layouts.add(L2RTreeLayout);
		file.add(svgSave);
		file.add(exitItem);
		basicLayout.addActionListener((ActionEvent event) -> {
			jf.getContentPane().removeAll();
			and.basicLayout(jf);
		});
		clusteredLayout.addActionListener((ActionEvent event) -> {
			jf.getContentPane().removeAll();
			and.clusteredLayout(jf);
		});
		pickableLayout.addActionListener((ActionEvent event) -> {
			jf.getContentPane().removeAll();
			and.pickableLayout(jf);
		});
		L2RTreeLayout.addActionListener((ActionEvent event) -> {
			jf.getContentPane().removeAll();
			and.L2RTreeLayout(jf);
		});
		exitItem.addActionListener((ActionEvent event) -> {
			System.exit(0);
		});
		jmb.add(file);
		jmb.add(layouts);
		jf.setJMenuBar(jmb);
		basicLayout.doClick();
	}
	
	private void L2RTreeLayout(JFrame jf) {
  
	        // create a simple graph for the demo
	        graph = new DelegateForest<String,Integer>();

	        createTree();
	        
	        treeLayout = new TreeLayout<String,Integer>(graph);
	        radialLayout = new RadialTreeLayout<String,Integer>(graph);
	        radialLayout.setSize(new Dimension(600,600));
	        vv2 =  new VisualizationViewer<String,Integer>(treeLayout, new Dimension(600,600));
	        vv2.setBackground(Color.white);
	        vv2.getRenderContext().setEdgeShapeTransformer(EdgeShape.quadCurve(graph));
	        vv2.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
	        // add a listener for ToolTips
	        vv2.setVertexToolTipTransformer(new ToStringLabeller());
	        vv2.getRenderContext().setArrowFillPaintTransformer(Functions.<Paint>constant(Color.lightGray));
	        rings = new Rings();
	        
	        setLtoR(vv2);
	    	jf.setTitle("Changeable Spring - FRLayout");
			jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	        Container content = jf.getContentPane();
	        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv2);
	        content.add(panel);
	        
	        final DefaultModalGraphMouse<String, Integer> graphMouse
	        	= new DefaultModalGraphMouse<String, Integer>();

	        vv.setGraphMouse(graphMouse);
	        
	        JComboBox<Mode> modeBox = graphMouse.getModeComboBox();
	        modeBox.addItemListener(graphMouse.getModeListener());
	        graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);

	        final ScalingControl scaler = new CrossoverScalingControl();

	        JButton plus = new JButton("+");
	        plus.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                scaler.scale(vv2, 1.1f, vv2.getCenter());
	            }
	        });
	        JButton minus = new JButton("-");
	        minus.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                scaler.scale(vv2, 1/1.1f, vv2.getCenter());
	            }
	        });
	        
	        JToggleButton radial = new JToggleButton("Radial");
	        radial.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange() == ItemEvent.SELECTED) {
						
						LayoutTransition<String,Integer> lt =
							new LayoutTransition<String,Integer>(vv2, treeLayout, radialLayout);
						Animator animator = new Animator(lt);
						animator.start();
						vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
						vv.addPreRenderPaintable(rings);
					} else {
						LayoutTransition<String,Integer> lt =
							new LayoutTransition<String,Integer>(vv2, radialLayout, treeLayout);
						Animator animator = new Animator(lt);
						animator.start();
						vv2.getRenderContext().getMultiLayerTransformer().setToIdentity();
						setLtoR(vv2);
						vv2.removePreRenderPaintable(rings);
					}

					vv2.repaint();
				}});

	        JPanel scaleGrid = new JPanel(new GridLayout(1,0));
	        scaleGrid.setBorder(BorderFactory.createTitledBorder("Zoom"));

	        JPanel controls = new JPanel();
	        scaleGrid.add(plus);
	        scaleGrid.add(minus);
	        controls.add(radial);
	        controls.add(scaleGrid);
	        controls.add(modeBox);

	        content.add(controls, BorderLayout.SOUTH);
	    	jf.pack();
			jf.setVisible(true);
	}
	 

	    private void setLtoR(VisualizationViewer<String,Integer> vv2) {
	    	Layout<String,Integer> layout = vv2.getModel().getGraphLayout();
	    	Dimension d = layout.getSize();
	    	Point2D center = new Point2D.Double(d.width/2, d.height/2);
	    	vv2.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).rotate(-Math.PI/2, center);
	    }
	    
	    class Rings implements VisualizationServer.Paintable {
	    	
	    	Collection<Double> depths;
	    	
	    	public Rings() {
	    		depths = getDepths();
	    	}
	    	
	    	private Collection<Double> getDepths() {
	    		Set<Double> depths = new HashSet<Double>();
	    		Map<String,PolarPoint> polarLocations = radialLayout.getPolarLocations();
	    		for(String v : graph.getVertices()) {
	    			PolarPoint pp = polarLocations.get(v);
	    			depths.add(pp.getRadius());
	    		}
	    		return depths;
	    	}

			public void paint(Graphics g) {
				g.setColor(Color.lightGray);
			
				Graphics2D g2d = (Graphics2D)g;
				Point2D center = radialLayout.getCenter();

				Ellipse2D ellipse = new Ellipse2D.Double();
				for(double d : depths) {
					ellipse.setFrameFromDiagonal(center.getX()-d, center.getY()-d, 
							center.getX()+d, center.getY()+d);
					Shape shape = vv2.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).transform(ellipse);
					g2d.draw(shape);
				}
			}

			public boolean useTransform() {
				return true;
			}
	    }
	    
	    /**
	     * 
	     */
	    private void createTree() {
	    	graph.addVertex("V0");
	    	graph.addEdge(edgeFactory.get(), "V0", "V1");
	    	graph.addEdge(edgeFactory.get(), "V0", "V2");
	    	graph.addEdge(edgeFactory.get(), "V1", "V4");
	    	graph.addEdge(edgeFactory.get(), "V2", "V3");
	    	graph.addEdge(edgeFactory.get(), "V2", "V5");
	    	graph.addEdge(edgeFactory.get(), "V4", "V6");
	    	graph.addEdge(edgeFactory.get(), "V4", "V7");
	    	graph.addEdge(edgeFactory.get(), "V3", "V8");
	    	graph.addEdge(edgeFactory.get(), "V6", "V9");
	    	graph.addEdge(edgeFactory.get(), "V4", "V10");
	    	
	       	graph.addVertex("A0");
	       	graph.addEdge(edgeFactory.get(), "A0", "A1");
	       	graph.addEdge(edgeFactory.get(), "A0", "A2");
	       	graph.addEdge(edgeFactory.get(), "A0", "A3");
	       	
	       	graph.addVertex("B0");
	    	graph.addEdge(edgeFactory.get(), "B0", "B1");
	    	graph.addEdge(edgeFactory.get(), "B0", "B2");
	    	graph.addEdge(edgeFactory.get(), "B1", "B4");
	    	graph.addEdge(edgeFactory.get(), "B2", "B3");
	    	graph.addEdge(edgeFactory.get(), "B2", "B5");
	    	graph.addEdge(edgeFactory.get(), "B4", "B6");
	    	graph.addEdge(edgeFactory.get(), "B4", "B7");
	    	graph.addEdge(edgeFactory.get(), "B3", "B8");
	    	graph.addEdge(edgeFactory.get(), "B6", "B9");
	       	
	    }
    
	    
	@Override
	public void init() {

		//create a graph with relaxers and multiple layouts
		Graph<Number,Number> ig = Graphs.<Number,Number>synchronizedDirectedGraph(new DirectedSparseMultigraph<Number,Number>());
		ObservableGraph<Number,Number> og = new ObservableGraph<Number,Number>(ig);
		og.addGraphEventListener(new GraphEventListener<Number,Number>() {

			public void handleGraphEvent(GraphEvent<Number, Number> evt) {
				System.err.println("got "+evt);

			}});
		this.g = og;

		this.timer   = new Timer();
		this.layout = new FRLayout2<Number,Number>(g);
		//       ((FRLayout)layout).setMaxIterations(200);
		// create a simple pickable layout
		this.vv = new VisualizationViewer<Number,Number>(layout, new Dimension(600,600));



	}

	private void pickableLayout(JFrame jf) {
		
		JFrame frame = jf;
		frame.setTitle("Changeable Spring - FRLayout");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JRootPane rp = frame.getRootPane();
		rp.putClientProperty("defeatSystemEventQueueCheck", Boolean.TRUE);

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().setBackground(java.awt.Color.lightGray);
		frame.getContentPane().setFont(new Font("Serif", Font.PLAIN, 12));

		vv.getModel().getRelaxer().setSleepTime(500);
		vv.setGraphMouse(new DefaultModalGraphMouse<Number,Number>());
		List<Number> groupVertices = Arrays.asList(83,84,71,72);
		addVertexGroupPainter(vv,groupVertices);
		vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		vv.setForeground(Color.white);
		  vv.getRenderer().setVertexRenderer(
	        		new GradientVertexRenderer<Number,Number>(
	        				Color.white, Color.red, 
	        				Color.white, Color.blue,
	        				vv.getPickedVertexState(),
	        				false));
	        vv.getRenderContext().setEdgeDrawPaintTransformer(Functions.<Paint>constant(Color.lightGray));
	        vv.getRenderContext().setArrowFillPaintTransformer(Functions.<Paint>constant(Color.lightGray));
	        vv.getRenderContext().setArrowDrawPaintTransformer(Functions.<Paint>constant(Color.lightGray));
	        
		final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
		Container content = frame.getContentPane();
		content.add(panel);
		panel.add(vv);
		switchLayout = new JButton("Switch to SpringLayout");
		switchLayout.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				Dimension d = new Dimension(600,600);
				if (switchLayout.getText().indexOf("Spring") > 0) {
					switchLayout.setText("Switch to FRLayout");
					layout = new SpringLayout<Number,Number>(g,
							Functions.<Integer>constant(EDGE_LENGTH));
					layout.setSize(d);
					vv.getModel().setGraphLayout(layout, d);
				} else {
					switchLayout.setText("Switch to SpringLayout");
					layout = new FRLayout<Number,Number>(g, d);
					vv.getModel().setGraphLayout(layout, d);
				}
			}
		});

		panel.add(switchLayout, BorderLayout.SOUTH);

		panel.add(vv);   
		frame.pack();
		frame.setVisible(true);

	}

	private void addVertexGroupPainter(final VisualizationViewer<Number, Number> vv2, final List<Number> groupVertices) {
		vv.addPreRenderPaintable(new VisualizationViewer.Paintable() {

			@Override
			public void paint(Graphics gr) {
				Graphics2D g = (Graphics2D)gr;
				Layout<Number,Number> lay =  vv.getGraphLayout();
				AffineTransform transform = vv.getRenderContext()
												.getMultiLayerTransformer()
												.getTransformer(Layer.LAYOUT)
												.getTransform();
				Rectangle2D boundingBox = 
						computeBoundingBox(groupVertices,lay,transform);
				double d = 20;
				double thickness = 2;
				Stroke oldStroke = g.getStroke();
				g.setStroke(new BasicStroke((float) thickness));
				//Shape rect = new RoundRectangle2D.Double(
				//		boundingBox.getMinX()-d,
				//		boundingBox.getMinY()-d,
				//		boundingBox.getWidth()+d+d,
				//		boundingBox.getHeight()+d+d,d,d);
				g.setColor(new Color(255,100,145));
				
				g.drawRoundRect((int)(boundingBox.getMinX()-d),
						(int)(boundingBox.getMinY()-d),
						(int)(boundingBox.getWidth()+d+d),
						(int)(boundingBox.getHeight()+d+d),(int)d,(int)d);
				//g.fill(rect);
				//g.draw(rect);
			}

			@Override
			public boolean useTransform() {
				return true;
			}
			
		});
		
	}
	protected Rectangle2D computeBoundingBox(List<Number> groupVertices, Layout<Number, Number> lay, AffineTransform transform) {
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		for(Number vertex: groupVertices)
		{
			Point2D location = lay.apply(vertex);
			transform.transform(location, location);
			minX = Math.min(minX,location.getX());
			minY = Math.min(minY, location.getY());
			maxX = Math.max(maxX, location.getX());
			maxY = Math.max(maxY, location.getY());
		}
		return new Rectangle2D.Double(minX,minY,maxX-minX,maxY-minY);
	}
	
	private void clusteredLayout(JFrame jf) {
		//create a clustered layout
		//Create a simple layout frame
		JFrame jf2 = jf;

		jf2.setTitle("Clustered Aggregate FRLayout");
		jf2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		//specify the Fruchterman-Rheingold layout algorithm
		final AggregateLayout<Number,Number> layout2 = 
				new AggregateLayout<Number,Number>(new FRLayout<Number,Number>(this.g));
		VisualizationViewer<Number, Number> vv2 = new VisualizationViewer<Number,Number>(layout2);
		vv2.setBackground( Color.white );
		//Tell the renderer to use our own customized color rendering

		vv2.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);

		vv2.getRenderContext().setVertexFillPaintTransformer(vertexPaints);
		vv2.getRenderContext().setVertexDrawPaintTransformer(new Function<Number,Paint>() {
			public Paint apply(Number v) {
				if(vv2.getPickedVertexState().isPicked(v)) {
					return Color.cyan;
				} else {
					return Color.BLACK;
				}
			}
		});
		vv2.getRenderContext().setEdgeDrawPaintTransformer(edgePaints);

		vv2.getRenderContext().setEdgeStrokeTransformer(new Function<Number,Stroke>() {
			protected final Stroke THIN = new BasicStroke(1);
			protected final Stroke THICK= new BasicStroke(2);
			public Stroke apply(Number e)
			{
				Paint c = edgePaints.getUnchecked(e);
				if (c == Color.LIGHT_GRAY)
					return THIN;
				else 
					return THICK;
			}
		});
		DefaultModalGraphMouse<Number, Number> gm = new DefaultModalGraphMouse<Number, Number>();
		vv2.setGraphMouse(gm);
		Container content = jf2.getContentPane();
		content.add(new GraphZoomScrollPane(vv2));
		JPanel south = new JPanel();
		JPanel grid = new JPanel(new GridLayout(2,1));

		south.add(grid);

		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createTitledBorder("Mouse Mode"));
		p.add(gm.getModeComboBox());
		south.add(p);
		content.add(south, BorderLayout.SOUTH);
		jf2.pack();
		jf2.setVisible(true);

	}

	@Override
	public void start() {
		validate();
		//set timer so applet will change
		timer.schedule(new RemindTask(), 1, 1); //subsequent rate
		vv.repaint();
	}

	Integer v_prev = null;


	public void process() {

		try {

			if (g.getVertexCount() < 100) {
				layout.lock(true);
				//add a vertex
				Integer v1 = new Integer(g.getVertexCount());

				Relaxer relaxer = vv.getModel().getRelaxer();
				relaxer.pause();
				g.addVertex(v1);
				System.err.println("added node " + v1);

				// wire it to some edges
				if (v_prev != null) {
					g.addEdge(g.getEdgeCount(), v_prev, v1);
					// let's connect to a random vertex, too!
					int rand = (int) (Math.random() * g.getVertexCount());
					g.addEdge(g.getEdgeCount(), v1, rand);
				}

				v_prev = v1;

				layout.initialize();
				relaxer.resume();
				layout.lock(false);
			} else {
				done = true;
			}

		} catch (Exception e) {
			System.out.println(e);

		}
	}

	class RemindTask extends TimerTask {

		@Override
		public void run() {
			process();
			if(done) cancel();

		}
	}



	private  void basicLayout(JFrame jf2) {


		JFrame jf = jf2;
		jf.setTitle("Basic pickable FRLayout");


		FRLayout<Number,Number> layout = new FRLayout<>(this.g);
		layout.setMaxIterations(100);
		Function<Number, Point2D> arg0 = new RandomLocationTransformer<Number>(new Dimension(600,600), 0);
		layout.setInitializer(arg0 );
		VisualizationViewer<Number,Number> vv = new VisualizationViewer<Number,Number>(
				layout, new Dimension(600,600));
		vv.setPreferredSize(new Dimension(600,600)); //Sets the viewing area size
		Function<Number,Paint> vpf = 
				new PickableVertexPaintTransformer<Number>((PickedInfo<Number>)vv.getPickedVertexState(), (Paint)Color.GREEN, (Paint)Color.YELLOW);			

		vv.getRenderContext().setVertexFillPaintTransformer(vpf);
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		final DefaultModalGraphMouse<Integer,String> graphMouse = new DefaultModalGraphMouse<>();
		graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
		vv.setGraphMouse(graphMouse);
		vv.addKeyListener(graphMouse.getModeKeyListener());
		// create a frome to hold the graph
		final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
		Container content = getContentPane();
		content.add(panel);



		Box controls = Box.createHorizontalBox();

		ButtonGroup radio = new ButtonGroup();
		JRadioButton lineButton = new JRadioButton("Line");
		lineButton.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					vv.getRenderContext().setEdgeShapeTransformer(EdgeShape.line(g));
					vv.repaint();
				}
			}
		});


		JRadioButton quadButton = new JRadioButton("QuadCurve");
		quadButton.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					vv.getRenderContext().setEdgeShapeTransformer(EdgeShape.quadCurve(g));
					vv.repaint();
				}
			}
		});

		JRadioButton cubicButton = new JRadioButton("CubicCurve");
		cubicButton.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					vv.getRenderContext().setEdgeShapeTransformer(EdgeShape.cubicCurve(g));
					vv.repaint();
				}
			}
		});
		radio.add(lineButton);
		radio.add(quadButton);
		radio.add(cubicButton);

		JPanel edgePanel = new JPanel(new GridLayout(0,1));
		edgePanel.setBorder(BorderFactory.createTitledBorder("EdgeType Type"));
		edgePanel.add(lineButton);
		edgePanel.add(quadButton);
		edgePanel.add(cubicButton);
		controls.add(edgePanel);
		content.add(controls, BorderLayout.SOUTH);

		quadButton.setSelected(true);
		// jf.getContentPane().add(panel);
		jf.getContentPane().add(content);
		jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jf.pack();
		jf.setVisible(true);		
	} 
}
