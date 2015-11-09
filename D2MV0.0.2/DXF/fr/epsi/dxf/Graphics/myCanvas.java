package fr.epsi.dxf.Graphics;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;

import fr.epsi.dxf.DXF_Loader;
import fr.epsi.dxf.myUnivers;
import fr.epsi.dxf.Entities.myArc;
import fr.epsi.dxf.Entities.myBlockReference;
import fr.epsi.dxf.Entities.myCircle;
import fr.epsi.dxf.Entities.myDimension;
import fr.epsi.dxf.Entities.myEllipse;
import fr.epsi.dxf.Entities.myEntity;
import fr.epsi.dxf.Entities.myInsert;
import fr.epsi.dxf.Entities.myLine;
import fr.epsi.dxf.Entities.myPoint;
import fr.epsi.dxf.Entities.myPolyline;
import fr.epsi.dxf.Entities.mySolid;
import fr.epsi.dxf.Entities.myText;
import fr.epsi.dxf.Entities.myTrace;
import fr.epsi.dxf.Entities.myVertex;
import fr.epsi.dxf.Graphics.sun.print.ProxyPrintGraphics;
import fr.epsi.dxf.Header.myBlock;
import fr.epsi.dxf.Header.myLayer;
import fr.epsi.dxf.Header.myLineType;
import fr.epsi.dxf.Header.myNameGenerator;
import fr.epsi.dxf.Header.myTable;

public class myCanvas extends Canvas implements MouseListener, MouseMotionListener, ComponentListener, KeyListener{

	private static final long serialVersionUID = 1L;
	public BufferedImage bi;
	public Graphics2D big;
	public boolean firstTime = true;
	public Rectangle area;
	public static DXF_Loader _dxf;
	public Dimension dim;
	public boolean drawingLine=false;
	public Point lastClick;
	public Point currPoint;
	public Point lastDrag;
	public Point arcStart;
	public Point arcStop;
	public Point arcCenter;
	public Point arcRadius;
	public Point origPoint;
	public Color currColor;
	public boolean selecting=false;
	public boolean moving=false;
	public boolean zooming=false;
	public boolean drawingCircle=false;
	public boolean drawingPolyLineStart=false;
	public boolean drawingPolyLineEnd=false;
	public boolean drawingLwPolyLineStart=false;
	public boolean drawingLwPolyLineEnd=false;
	public boolean drawingArc=false;
	public boolean drawingArcAngleStart=false;
	public boolean drawingArcAngleEnd=false;
	public boolean drawingEllipse=false;
	public boolean drawingTrace=false;
	public boolean drawingTxt=false;
	public boolean drawingSolid=false;
	public boolean xSelecting=false;
	public Vector<myVertex> tmpVectVertex = new Vector<myVertex>();
	public int[] tmpPolyX;
	public int[] tmpPolyY;
	public int[] tmpLwPolyX;
	public int[] tmpLwPolyY;
	public Rectangle zoomRect;
	public static myEntity clickOn;
	public Vector<myEntity> vectClickOn=new Vector<myEntity>();
	private int bump=1;
	public Vector<myEntity> clipBoard=new Vector<myEntity>();	
	public Stroke currentStroke = myTable.defaultStroke;
	public int quadCount=0;
	public Point2D.Double[] quadPt=new Point2D.Double[3];
	private myText editText;
	private boolean firstTxt=false;
	private myEntity changingEnt;
	private myCircle editCircle=null;
	private myArc editArc=null;
	public static double selEntityVar[] = new double[3];

	public myCanvas(DXF_Loader d){
		super();
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		_dxf=d;
		addMouseMotionListener(this);
		addMouseListener(this);
		addComponentListener(this);
		addKeyListener(this);

		this.setBackground(myUnivers._bgColor);

		try {
			_dxf.newDXF();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	}

	public void paint(Graphics g){
		update(g);
	}

	public void update(Graphics g){

		Graphics2D g2 = null;
		Object aliasing = RenderingHints.VALUE_ANTIALIAS_OFF;

		if (myUnivers.antialiasing)
			aliasing = RenderingHints.VALUE_ANTIALIAS_ON;			
		else
			aliasing = RenderingHints.VALUE_ANTIALIAS_OFF;

		if((g instanceof ProxyPrintGraphics)){
			g2 = (Graphics2D)((ProxyPrintGraphics)g).getGraphics();
		}else{
			g2=((Graphics2D) g);
		}

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,aliasing);

		if(firstTime){
			dim = getSize();
			myCoord.Max = dim.width;

			_dxf._u._header.setLIM(myCoord.javaToDXF_X(getWidth()), myCoord.javaToDXF_X(getHeight()));

			area = new Rectangle(dim);
			bi = (BufferedImage)createImage(dim.width,dim.height);
			big = bi.createGraphics();
			firstTime = false;
			//big.setStroke(myTable.defaultStroke);	BIG BUG LAGG		
		}
		big.clearRect(0, 0, area.width, area.height);

		if (this.moving)
			bump=3;
		else bump=1;
		for (int i=0;i<_dxf._u._myTables.size();i++) {
			for (int j=0;j<_dxf._u._myTables.elementAt(i)._myLayers.size();j++) {
				if (this.moving) {
					if (((myLayer)((myTable)_dxf._u._myTables.elementAt(i))._myLayers.elementAt(j))._myEnt.size() < 50)
						bump=1;
					else 
						bump=3;
				}
				for (int k=0;k<((myLayer)((myTable)_dxf._u._myTables.elementAt(i))._myLayers.elementAt(j))._myEnt.size();k+=bump) {
					if(((myLayer)((myTable)_dxf._u._myTables.elementAt(i))._myLayers.elementAt(j)).isVisible && ((myLayer)((myTable)_dxf._u._myTables.elementAt(i))._myLayers.elementAt(j))._flag != 1) {

						if(((myLayer)((myTable)_dxf._u._myTables.elementAt(i))._myLayers.elementAt(j))._myEnt.elementAt(k).isVisible){                    
							((myLayer)((myTable)_dxf._u._myTables.elementAt(i))._myLayers.elementAt(j))._myEnt.elementAt(k).draw(big);
						}
					}
				}
			}
		}

		Color curr = _dxf._jcc.getColor();
		if (curr.equals(myUnivers._bgColor)) {
			if (myUnivers._bgColor.equals(Color.WHITE))
				big.setColor(Color.BLACK);
			else if (myUnivers._bgColor.equals(Color.BLACK))		
				big.setColor(Color.WHITE);
		} else
			big.setColor(curr);


		// affichage de la ligne pdt un glisser de la souris (outil ligne)
		if (this.drawingLine) {
			int x1=(int)(this.lastClick.getX());
			int y1=(int)(this.lastClick.getY());
			int x2=(int)(this.currPoint.getX());
			int y2=(int)(this.currPoint.getY());
			big.drawLine(x1 , y1 , x2 , y2 );
		} else if (drawingPolyLineStart || drawingPolyLineEnd) {
			this.tmpPolyX=new int[tmpVectVertex.size()];
			this.tmpPolyY=new int[tmpVectVertex.size()];
			for (int i=0;i<tmpPolyX.length;i++) {
				tmpPolyX[i]=(int) myCoord.dxfToJava_X(tmpVectVertex.elementAt(i).X());
				tmpPolyY[i]=(int) myCoord.dxfToJava_Y(tmpVectVertex.elementAt(i).Y()); 
			}
			big.drawPolyline(this.tmpPolyX, tmpPolyY, tmpPolyX.length);
			big.drawLine(tmpPolyX[tmpPolyX.length-1], tmpPolyY[tmpPolyY.length-1], (int)currPoint.getX(), (int)currPoint.getY());
			this.tmpPolyX=null;
			this.tmpPolyY=null;
		} else if (drawingLwPolyLineStart || drawingLwPolyLineEnd) {
			this.tmpLwPolyX=new int[tmpVectVertex.size()];
			this.tmpLwPolyY=new int[tmpVectVertex.size()];
			for (int i=0;i<tmpPolyX.length;i++) {
				tmpPolyX[i]=(int) myCoord.dxfToJava_X(tmpVectVertex.elementAt(i).X());
				tmpPolyY[i]=(int) myCoord.dxfToJava_Y(tmpVectVertex.elementAt(i).Y()); 
			}
			big.drawPolyline(this.tmpPolyX, tmpPolyY, tmpPolyX.length);
			big.drawLine(tmpPolyX[tmpPolyX.length-1], tmpPolyY[tmpPolyY.length-1], (int)currPoint.getX(), (int)currPoint.getY());
			this.tmpLwPolyX=null;
			this.tmpLwPolyY=null;
		} else if (this.drawingCircle) {
			big.drawOval((int)lastClick.getX()-(int)lastClick.distance(currPoint), (int)lastClick.getY()-(int)lastClick.distance(currPoint), (int)lastClick.distance(currPoint)*2, (int)lastClick.distance(currPoint)*2);
		} else if (this.drawingArc) {
			big.drawOval((int)lastClick.getX()-(int)lastClick.distance(currPoint), (int)lastClick.getY()-(int)lastClick.distance(currPoint), (int)lastClick.distance(currPoint)*2, (int)lastClick.distance(currPoint)*2);
		} else if (this.drawingArcAngleStart) {
			big.drawOval((int)arcCenter.getX()-(int)arcCenter.distance(arcRadius), (int)arcCenter.getY()-(int)arcCenter.distance(arcRadius), (int)arcCenter.distance(arcRadius)*2, (int)arcCenter.distance(arcRadius)*2);
			big.draw(new Line2D.Double(arcCenter.getX(), arcCenter.getY(), currPoint.getX(), currPoint.y));
		} else if (this.drawingArcAngleEnd) {
			big.drawOval((int)arcCenter.getX()-(int)arcCenter.distance(arcRadius), (int)arcCenter.getY()-(int)arcCenter.distance(arcRadius), (int)arcCenter.distance(arcRadius)*2, (int)arcCenter.distance(arcRadius)*2);
			big.draw(new Line2D.Double(arcCenter.getX(), arcCenter.getY(), currPoint.getX(), currPoint.y));
		} else if (this.zooming) {
			int x1=0,y1=0,x2=0,y2=0,min=0,caseDraw=0;
			big.setColor(Color.LIGHT_GRAY);
			big.setStroke(myTable.zoomStroke);

			if ( (lastClick.getX()<currPoint.getX()) && (lastClick.getY()<currPoint.getY()) )
				caseDraw=myCoord.SE;
			else if ( (lastClick.getX()<currPoint.getX()) && (lastClick.getY()>currPoint.getY()) )
				caseDraw=myCoord.NE;
			else if ( (lastClick.getX()>currPoint.getX()) && (lastClick.getY()>currPoint.getY()) )
				caseDraw=myCoord.NO;
			else if ( (lastClick.getX()>currPoint.getX()) && (lastClick.getY()<currPoint.getY()) )
				caseDraw=myCoord.SO;

			switch (caseDraw) {
			case myCoord.NE : 
				x1=(int)this.lastClick.getX();
				y1=(int)this.currPoint.getY();
				x2=(int)(this.currPoint.getX()-this.lastClick.getX());
				y2=(int)(this.lastClick.getY()-this.currPoint.getY());
				break;

			case myCoord.NO : 
				x1=(int)this.currPoint.getX();
				y1=(int)this.currPoint.getY();
				x2=(int)(this.lastClick.getX()-this.currPoint.getX());
				y2=(int)(this.lastClick.getY()-this.currPoint.getY());
				break;

			case myCoord.SE : 
				x1=(int)this.lastClick.getX();
				y1=(int)this.lastClick.getY();
				x2=(int)(this.currPoint.getX()-this.lastClick.getX());
				y2=(int)(this.currPoint.getY()-this.lastClick.getY());
				break;

			case myCoord.SO : 
				x1=(int)this.currPoint.getX();
				y1=(int)this.lastClick.getY();
				x2=(int)(this.lastClick.getX()-this.currPoint.getX());
				y2=(int)(this.currPoint.getY()-this.lastClick.getY());
				break;

			default : break;
			}
			if (x2<=y2)
				min=x2;
			else 
				min=y2;
			zoomRect.setLocation(x1, y1);
			zoomRect.setSize(min, min);
			big.draw(zoomRect);
			big.setStroke(currentStroke);
		} else if (this.xSelecting) {
			int x1=0,y1=0,x2=0,y2=0,min=0,caseDraw=myCoord.SE;
			big.setColor(Color.LIGHT_GRAY);

			if ( (lastClick.getX()<currPoint.getX()) && (lastClick.getY()<currPoint.getY()) )
				caseDraw=myCoord.SE;
			else if ( (lastClick.getX()<currPoint.getX()) && (lastClick.getY()>currPoint.getY()) )
				caseDraw=myCoord.NE;
			else if ( (lastClick.getX()>currPoint.getX()) && (lastClick.getY()>currPoint.getY()) )
				caseDraw=myCoord.NO;
			else if ( (lastClick.getX()>currPoint.getX()) && (lastClick.getY()<currPoint.getY()) )
				caseDraw=myCoord.SO;

			switch (caseDraw) {
				case myCoord.NE : 
					x1=(int)this.lastClick.getX();
					y1=(int)this.currPoint.getY();
					x2=(int)(this.currPoint.getX()-this.lastClick.getX());
					y2=(int)(this.lastClick.getY()-this.currPoint.getY());
					break;
	
				case myCoord.NO : 
					x1=(int)this.currPoint.getX();
					y1=(int)this.currPoint.getY();
					x2=(int)(this.lastClick.getX()-this.currPoint.getX());
					y2=(int)(this.lastClick.getY()-this.currPoint.getY());
					break;
	
				case myCoord.SE : 
					x1=(int)this.lastClick.getX();
					y1=(int)this.lastClick.getY();
					x2=(int)(this.currPoint.getX()-this.lastClick.getX());
					y2=(int)(this.currPoint.getY()-this.lastClick.getY());
					break;
	
				case myCoord.SO : 
					x1=(int)this.currPoint.getX();
					y1=(int)this.lastClick.getY();
					x2=(int)(this.lastClick.getX()-this.currPoint.getX());
					y2=(int)(this.currPoint.getY()-this.lastClick.getY());
					break;
			}
			if (x2<=y2)
				min=x2;
			else 
				min=y2;
			zoomRect.setLocation(x1, y1);
			zoomRect.setSize(min, min);
			big.draw(zoomRect);
		} else if (this.drawingEllipse) {
			big.drawOval((int)lastClick.getX(), (int)lastClick.getY(), (int)(currPoint.getX()-lastClick.getX()),(int)(currPoint.getY()-lastClick.getY()));
		} else if (drawingTrace) {
			for (int i=0;i<quadCount-1;i++) {
				big.drawLine(
						(int)myCoord.dxfToJava_X(quadPt[i].x),
						(int)myCoord.dxfToJava_Y(quadPt[i].y),
						(int)myCoord.dxfToJava_X(quadPt[i+1].x),
						(int)myCoord.dxfToJava_Y(quadPt[i+1].y)
				);
			}
			big.drawLine(
					(int)(this.lastClick.getX()),
					(int)(this.lastClick.getY()),
					(int)(this.currPoint.getX()),
					(int)(this.currPoint.getY())
			);
		}
		repaint();
		
		//		 GLOBAL MAP
		{
			big.setColor(Color.YELLOW);
			//big.setStroke(mapStroke);
			double x1= area.width-area.width*0.1;
			double y1= area.height-area.height*0.1;
			double w1= area.width*0.1;
			double h1= area.height*0.1;

			big.clearRect(
					(int) x1,
					(int) y1, 
					(int) w1, 
					(int) h1
			);
			big.drawRect(
					(int) x1,
					(int) y1, 
					(int) w1, 
					(int) h1
			);

			big.setColor(Color.RED);

			double x2 =  x1+0.1*_dxf._u._header._LIMMIN.X();;
			double y2 = area.height + (_dxf._u._header._LIMMIN.Y()-_dxf._u._header._LIMMAX.Y())*0.1;
			double w2 = w1 - (_dxf._u._header._EXTMAX.X()-_dxf._u._header._LIMMAX.X())*0.1;
			double h2 = (_dxf._u._header._LIMMIN.X()-_dxf._u._header._LIMMAX.X())*0.1;

			big.fillRect(
					(int) x2,
					(int) y2, 
					(int) w2, 
					(int) h2
			);			
		}	
		// FIN MAP
		big.setBackground(myUnivers._bgColor);
		g2.drawImage(bi, 0, 0, this); //double buffering		
	}

	public void mousePressed(MouseEvent e){
		this.lastClick=e.getPoint();
		this.currPoint = e.getPoint();
		if (_dxf._typeOutil.getSelectedIndex()==myToolBar.toolLine) {
			this.drawingLine=true;
		} else if (_dxf._typeOutil.getSelectedIndex()==myToolBar.toolCircle) {
			this.drawingCircle=true;
		} else if (_dxf._typeOutil.getSelectedIndex()==myToolBar.toolDeplace) {
			this.moving=true;
		} else if (_dxf._typeOutil.getSelectedIndex()==myToolBar.toolSel) {
			this.selecting=true;
			if ((e.getModifiersEx()==1024) || (e.getModifiersEx()==1088) ) {  //rien ou shift
				for (int i=0;i<vectClickOn.size();i++) 
					vectClickOn.elementAt(i).setSelected(false);
				vectClickOn=null;
				vectClickOn=new Vector<myEntity>();
			}
			getSelectedObject(e.getX(),e.getY());
			if (clickOn!=null) {
				boolean sel=true;
				if (e.getModifiersEx()==1088) { //shift
					if (clickOn instanceof myLine) {
						Point2D.Double pA = new Point2D.Double();
						pA.x=myCoord.dxfToJava_X(((myLine)clickOn)._a.X());
						pA.y=myCoord.dxfToJava_Y(((myLine)clickOn)._a.Y());
						Point2D.Double pB = new Point2D.Double();
						pB.x=myCoord.dxfToJava_X(((myLine)clickOn)._b.X());
						pB.y=myCoord.dxfToJava_Y(((myLine)clickOn)._b.Y());
						double A=e.getPoint().distance(pA);
						double B=e.getPoint().distance(pB);
						double limits=pA.distance(pB);
						double maxLim=10;
						try {
							limits/=3;
						} catch (Exception ex) { // Dividebyzero
							limits=1;
						}
						if (limits > maxLim)
							limits=maxLim;
						if ((A<B)&& (A<limits))  {
							clickOn.setChanging(true);
							this.changingEnt=clickOn;
							clickOn=((myLine)clickOn)._a;
							sel=false;
						} else if ((B<=A)&& (B<limits))  {
							clickOn.setChanging(true);
							this.changingEnt=clickOn;
							clickOn=((myLine)clickOn)._b;
							sel=false;
						}
					} else if (clickOn instanceof myCircle) {
						this.editCircle=((myCircle)clickOn);
						this.changingEnt=clickOn;
						this.changingEnt.setChanging(true);
						this.editCircle.setChanging(true);
						sel=false;
					} else if (clickOn instanceof myPolyline) {
						int len=((myPolyline)clickOn)._myVertex.size();
						Point2D.Double pX[] = new Point2D.Double[len];
						double min=Double.MAX_VALUE,dist=0 ;
						int find=-1;
						for (int i=0;i<len;i++) {
							pX[i]=new Point2D.Double();
							pX[i].x=myCoord.dxfToJava_X(((myPolyline)clickOn)._myVertex.elementAt(i).X());
							pX[i].y=myCoord.dxfToJava_Y(((myPolyline)clickOn)._myVertex.elementAt(i).Y());
							dist=pX[i].distance(e.getPoint());
							if (dist<min) {
								min=dist;
								find=i;
							}
						}
						double maxLim=10;
						if (find != -1) {
							double d1=Double.MAX_VALUE, d2=Double.MAX_VALUE;
							int xA=0,xB=0;
							if (find==0) {
								xA=1;
								d1=pX[0].distance(pX[xA]);
								xB=len-1;
								d2=pX[0].distance(pX[xB]);
							} else if (find==(len-1)) {
								xA=0;
								d1=pX[len-1].distance(pX[0]);
								xB=find-1;
								d2=pX[len-1].distance(pX[xB]);
							} else {
								xA=find+1;
								d1=pX[find].distance(pX[find+1]);
								xB=find-1;
								d2=pX[find].distance(pX[xB]);
							}
							double limits=1;
							if ((d1!=0) && (d2!=0)) {
								if (d1<d2) {
									limits=d1/3;
									dist=pX[xA].distance(e.getPoint());
								} else {
									limits=d2/3;
									dist=pX[xB].distance(e.getPoint());
								}
								if (limits > maxLim)
									limits=maxLim;
							}
							if ((e.getPoint().distance(pX[find])<limits)) {
								this.changingEnt=((myPolyline)clickOn);
								this.changingEnt.setChanging(true);
								clickOn=((myPolyline)clickOn)._myVertex.elementAt(find);
								sel=false;
							}
						} 
						
					} else if (clickOn instanceof myArc) {
						this.editArc=((myArc)clickOn);
						this.changingEnt=clickOn;
						this.changingEnt.setChanging(true);
						this.editArc.setChanging(true);
						sel=false;
					} else if (clickOn instanceof myText) {

						this.changingEnt=((myText)clickOn);
						this.changingEnt.setChanging(true);
						sel=false;
						editText=((myText)clickOn);
						drawingTxt=true;
						firstTxt=false;
						
					}/* else if (clickOn instanceof mySolid) {
						((mySolid)clickOn).translate(x,y);
					} else if (clickOn instanceof myBlockReference) {
						((myBlockReference)clickOn).translate(x,y);
					} */
				}
				if (!sel) {
					vectClickOn=null;
					vectClickOn=new Vector<myEntity>();
					vectClickOn.add(clickOn);
				} else {
					if (!clickOn.selected)
						vectClickOn.add(clickOn);
					else
						vectClickOn.remove(clickOn);
					clickOn.setSelected(!clickOn.selected);
				}				
			}
			_dxf.sel.setText(_dxf.defSelTxtA+vectClickOn.size()+_dxf.txtB);
			_dxf.clipB.setText(_dxf.defClipTxtA+clipBoard.size()+_dxf.txtB);
			
		} else if (_dxf._typeOutil.getSelectedIndex()==myToolBar.toolSelX) {
			xSelecting=true;
			zoomRect=new Rectangle();
		} else if (_dxf._typeOutil.getSelectedIndex()==myToolBar.toolZoom) {
			this.zooming=true;
			currentStroke = big.getStroke();
			zoomRect=new Rectangle();
		} else if (_dxf._typeOutil.getSelectedIndex()==myToolBar.toolArc) {
			if (!this.drawingArc && !drawingArcAngleStart && !drawingArcAngleEnd) {
				this.drawingArc=true;	
			} else if (!this.drawingArc && drawingArcAngleStart && !drawingArcAngleEnd) {
				arcStart=e.getPoint();
				drawingArcAngleStart=false;
				drawingArcAngleEnd=true;
			}
		} else if (_dxf._typeOutil.getSelectedIndex()==myToolBar.toolEllipse) {
			this.drawingEllipse=true;
		}		
		_dxf.tree.updateSelection();
		repaint();
	}


	public void getSelectedObject(double x, double y) {
		double min=Double.MAX_VALUE,calcMin=0;
		myEntity testObj = null;
		myEntity closestObj = null;
		clickOn=null;
		Point2D.Double clickPoint=new Point2D.Double();
		clickPoint.setLocation(x,y);	
		for (int i=0;i<_dxf._u._myTables.size();i++) {
			for (int j=0;j<_dxf._u._myTables.elementAt(i)._myLayers.size();j++) {
				for (int k=0;k<_dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.size();k++) {
					testObj = _dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.elementAt(k);
					if (testObj instanceof myPoint) { 
						Rectangle2D.Double p = ((myPoint)testObj).getSelectedEntity();
						if ( (p!=null) &&  (p.intersects(x, y, 5, 5))) {
							clickOn=testObj;return;
						}				    
						calcMin=((myPoint)testObj)._point.distance(clickPoint);
						if (calcMin  < min) {
							min=calcMin;
							closestObj=testObj;
						}				
					} else if (testObj instanceof myText) {
						Rectangle2D.Double r = (Rectangle2D.Double)((myText)testObj).getSelectedEntity();
						if ((r!=null) && (r.intersects(x, y, 5, 10))) {
							clickOn=testObj;return;
						}				    
						calcMin=((myText)testObj)._point._point.distance(clickPoint);
						if (calcMin  < min) {
							min=calcMin;
							closestObj=testObj;
						}							
					} else if (testObj instanceof mySolid) {
						GeneralPath p = ((mySolid)testObj).getSelectedEntity();
						if ((p!=null) && (p.intersects(x, y, 5, 5))) {
							clickOn=testObj;return;
						}				    
						calcMin=((mySolid)testObj)._p1._point.distance(clickPoint);
						if (calcMin  < min) {
							min=calcMin;
							closestObj=testObj;
						}				    
						calcMin=((mySolid)testObj)._p2._point.distance(clickPoint);
						if (calcMin  < min) {
							min=calcMin;
							closestObj=testObj;
						}				    
						calcMin=((mySolid)testObj)._p3._point.distance(clickPoint);
						if (calcMin  < min) {
							min=calcMin;
							closestObj=testObj;
						}		
						if (((mySolid)testObj)._p4 != null) {
							calcMin=((mySolid)testObj)._p4._point.distance(clickPoint);
							if (calcMin  < min) {
								min=calcMin;
								closestObj=testObj;
							}	
						}
					}  else if (testObj instanceof myLine) {
						Line2D.Double l = ((myLine)testObj).getSelectedEntity();
						if ((l!=null) && (l.intersects(x, y, 5, 5))) {
							clickOn=testObj;return;
						}				    
						calcMin=l.getP1().distance(clickPoint);
						if (calcMin  < min) {
							min=calcMin;
							closestObj=testObj;
						}											    
						calcMin=l.getP2().distance(clickPoint);
						if (calcMin  < min) {
							min=calcMin;
							closestObj=testObj;
						}					
					} else if (testObj instanceof myCircle) {
						Ellipse2D.Double c = ((myCircle)testObj).getSelectedEntity();
						Ellipse2D.Double in_c =  myCircle.getSmallerGraphicEntity(c);
						if ((c!=null) && (c.intersects(x, y, 5, 5)) && (!in_c.intersects(x, y, 5, 5))) {
							clickOn=testObj;return;
						}				    
						calcMin=new Point2D.Double(c.getCenterX(),c.getCenterY()).distance(clickPoint);
						if (calcMin  < min) {
							min=calcMin;
							closestObj=testObj;
						}						
					} else if (testObj instanceof myPolyline) {
						GeneralPath p = ((myPolyline)testObj).getSelectedEntity();
						if ((p!=null) && (p.intersects(x, y, 5, 5))) {
							clickOn=testObj;return;
						}				    
						for (int a=0;a<((myPolyline)testObj)._myVertex.size();a++) {
							calcMin=new Point2D.Double(((myPolyline)testObj)._myVertex.elementAt(a).X(),((myPolyline)testObj)._myVertex.elementAt(a).Y()).distance(clickPoint);
							if (calcMin  < min) {
								min=calcMin;
								closestObj=testObj;
							}						
						}
					} else if (testObj instanceof myArc) {
						Arc2D.Double a = ((myArc)testObj).getSelectedEntity();
						if ((a!=null) && (a.intersects(x, y, 5, 5))) {
							clickOn=testObj;return;
						}							    
						calcMin=new Point2D.Double(a.getCenterX(),a.getCenterY()).distance(clickPoint);
						if (calcMin  < min) {
							min=calcMin;
							closestObj=testObj;
						}					
					} else if (testObj instanceof myBlockReference) {
						selEntityVar[0]=x;
						selEntityVar[1]=y;
						selEntityVar[2]=min;
						if (((myBlockReference)testObj).getSelectedEntity(selEntityVar)) {
							//clickOn=testObj;
							return;
						}
					}
				}
			}
		}
		if (_dxf.proximitySelection) {
			if (clickOn==null)
				clickOn=closestObj;
		}
	}

	public void mouseDragged(MouseEvent e){
		currPoint = e.getPoint();
		_dxf.coordXY.setText("   X:  "+myCoord.javaToDXF_X(currPoint.x)+"  Y:  "+myCoord.javaToDXF_Y(currPoint.y));
		if (this.selecting && (vectClickOn.size()>0)) { //!=null)) {
			
			if (this.editCircle!=null) {
				Point2D.Double p = new Point2D.Double();
				p.x=myCoord.dxfToJava_X(editCircle._point.X());
				p.y=myCoord.dxfToJava_Y(editCircle._point.Y());
				this.editCircle._radius=p.distance(e.getPoint());
			} else if (this.editArc!=null) {
				Point2D.Double p = new Point2D.Double();
				p.x=myCoord.dxfToJava_X(editArc._point.X());
				p.y=myCoord.dxfToJava_Y(editArc._point.Y());
				this.editArc._radius=p.distance(e.getPoint());
			} else {
				if (lastDrag==null)
					lastDrag=currPoint;
				double x=lastDrag.getX()-currPoint.getX();
				double y=lastDrag.getY()-currPoint.getY();
				for (int i=0;i<vectClickOn.size();i++) {
					clickOn=vectClickOn.elementAt(i);
					if (clickOn!=null)/* && (clickOn.selected)*/ {
						if (clickOn instanceof myLine) {
							((myLine)clickOn).translate(x,y);
						} else if (clickOn instanceof myCircle) {
							((myCircle)clickOn).translate(x,y);
						} else if (clickOn instanceof myPolyline) {
							((myPolyline)clickOn).translate(x,y);
						} else if (clickOn instanceof myArc) {
							((myArc)clickOn).translate(x,y);
						} else if (clickOn instanceof myPoint) {
							((myPoint)clickOn).translate(x,y);
						} else if (clickOn instanceof myText) {
							((myText)clickOn).translate(x,y);
						} else if (clickOn instanceof mySolid) {
							((mySolid)clickOn).translate(x,y);
						} else if (clickOn instanceof myBlockReference) {
							((myBlockReference)clickOn).translate(x,y);
						} 
					}
				}
				lastDrag=currPoint;
			}
		} else if (this.moving) {
			if (lastDrag==null)
				lastDrag=lastClick;
			if (e.getModifiersEx()==InputEvent.CTRL_DOWN_MASK) { 
				this.moving=false;
			} else {
				myCoord.decalageX+=(currPoint.getX()-this.lastDrag.getX());
				myCoord.decalageY+=(currPoint.getY()-this.lastDrag.getY());
			}
			lastDrag=currPoint;
		} /*else if (this.xSelecting) {

		} */
	}

	public void mouseReleased(MouseEvent e){

		if (this.drawingLine) {
			if (e.getModifiersEx()==InputEvent.CTRL_DOWN_MASK) { 
				this.drawingLine=false;
			} else {
				myLine l = new myLine(
						new myPoint(
								new Point2D.Double(
										myCoord.javaToDXF_X(lastClick.getX()),
										myCoord.javaToDXF_Y(lastClick.getY())
								)
						),
						new myPoint(
								new Point2D.Double(
										myCoord.javaToDXF_X(e.getX()),
										myCoord.javaToDXF_Y(e.getY())
								)
						),
						DXF_Color.getColor(_dxf._jcc.getColor()),
						_dxf._u.currLayer,
						(myLineType) _dxf._comboLineType.getSelectedItem(),
						_dxf._u.currThickness, 0
				);
				_dxf._u.currLayer._myEnt.addElement(l);
				_dxf.tree.addEntity(l);
				this.drawingLine=false;
			}
		} else if (this.drawingCircle) {
			if (e.getModifiersEx()==InputEvent.CTRL_DOWN_MASK) { 
				this.drawingCircle=false;
			} else {
				myCircle c = new myCircle(
						new myPoint(
								new Point2D.Double(
										myCoord.javaToDXF_X(lastClick.getX()),
										myCoord.javaToDXF_Y(lastClick.getY())	
								)								
						),
						lastClick.distance(e.getPoint())/myCoord.Ratio,
						(myLineType) _dxf._comboLineType.getSelectedItem(),
						DXF_Color.getColor(_dxf._jcc.getColor()),
						_dxf._u.currLayer,0,
						_dxf._u.currThickness
				);
				_dxf._u.currLayer._myEnt.addElement(c);
				_dxf.tree.addEntity(c);
				this.drawingCircle=false;
			}
		} else if (this.drawingArc) { 
			if (e.getModifiersEx()==InputEvent.CTRL_DOWN_MASK) { 
				this.drawingArc=false;
				this.drawingArcAngleStart=false;
				this.drawingArcAngleEnd=false;
			} else {
				this.arcCenter=lastClick;
				this.arcRadius=e.getPoint();
				this.drawingArc=false;
				this.drawingArcAngleStart=true;
			}
		} else if (this.drawingArcAngleEnd) { 
			if (e.getModifiersEx()==InputEvent.CTRL_DOWN_MASK) { 
				this.drawingArc=false;
				this.drawingArcAngleStart=false;
				this.drawingArcAngleEnd=false;
			} else {
				double teta1 = Math.acos((arcStart.getX()-arcCenter.getX())/arcCenter.distance(arcStart));
				double teta2 = Math.acos((e.getPoint().getX()-arcCenter.getX())/arcCenter.distance(e.getPoint()));

				if ((arcStart.getY() < arcCenter.getY()) && (e.getPoint().getY() > arcCenter.getY())) {//1h -> 8h 
					teta2 = (Math.PI*2) - teta2;
				} else if ((arcStart.getY() > arcCenter.getY()) && (e.getPoint().getY() > arcCenter.getY())) {//8h -> 5h
					if (arcCenter.getX()<e.getX()) {
						teta1 = (Math.PI*2) - teta1;
						teta2 = (Math.PI*2) - teta2;
					} else {
						teta1 = (Math.PI*2) - teta1;
						teta2 = (Math.PI*2) + teta1;
					}
				} else if ((arcStart.getY() < arcCenter.getY()) && (e.getPoint().getY() < arcCenter.getY())) {//8h -> 1h
					if (arcStart.getX() < e.getX()) {
						teta1 = (Math.PI*2) + teta1;
						teta2 = (Math.PI*2) + teta2;
					} else {  }
				} else if ((arcStart.getY() > arcCenter.getY()) && (e.getPoint().getY() < arcCenter.getY())) {//8h -> 1h
					double tmp=teta1;
					teta1 = (Math.PI*2) + teta2;
					teta2 = (Math.PI*2) - tmp;
				} 

				myArc a = new myArc(
						Math.toDegrees(teta1%(Math.PI*2)),
						Math.toDegrees(teta2),
						new myPoint(
								new Point2D.Double(
										myCoord.javaToDXF_X(arcCenter.getX()),
										myCoord.javaToDXF_Y(arcCenter.getY())
								)
						),
						arcCenter.distance(arcRadius)/myCoord.Ratio,
						(myLineType) _dxf._comboLineType.getSelectedItem(),
						DXF_Color.getColor(_dxf._jcc.getColor()),
						_dxf._u.currLayer,0, _dxf._u.currThickness
				);
				_dxf._u.currLayer._myEnt.addElement(a);
				_dxf.tree.addEntity(a);
				this.drawingArcAngleEnd=false;
			}
		} else if (this.moving) {
			if ((e.getModifiersEx()!=InputEvent.CTRL_DOWN_MASK) && (lastDrag!=null)) { 
				myCoord.decalageX+=(e.getX()-this.lastDrag.getX());
				myCoord.decalageY+=(e.getY()-this.lastDrag.getY());
				myHistory.saveHistory(true);
				myCanvas.clickOn=null;
				this.lastDrag=null;
				this.currPoint=null;
			}
			this.moving=false;
		} else if (this.zooming) {
			if (e.getModifiersEx()==InputEvent.CTRL_DOWN_MASK) { 
				this.zooming=false;
				big.setStroke(currentStroke);
				zoomRect=null;
			} else  {
				myCoord.Max=myCoord.javaToDXF_X(zoomRect.getWidth());
				//myCoord.decalageX*=-_dxf._u.lastView.getCenterX()-zoomRect.getCenterX();
				//myCoord.decalageY+=(this.getHeight()-zoomRect.getMaxY());

				myCoord.resetRatio();
				myCoord.decalageX-=myCoord.dxfToJava_X(_dxf._u.lastView.getCenterX()-zoomRect.getCenterX());
				myCoord.decalageY+=myCoord.dxfToJava_Y(_dxf._u.lastView.getCenterY()-zoomRect.getCenterY());
				//TODO --> 1
				
				
				this.zooming=false;
				big.setStroke(currentStroke);
			}
		} else if (this.selecting)  {//!=null)) {
			if (vectClickOn.size()>0) {
				if ((e.getModifiersEx()==0) || (e.getModifiersEx()==64) ) {  //Bouton 1 - sans Ctl
					for (int i=0;i<vectClickOn.size();i++) 
						vectClickOn.elementAt(i).setSelected(false);
					vectClickOn.removeAllElements();
						
				}
	
				if (this.editCircle!=null) {				
					this.editCircle=null;
				}
				if (this.editArc!=null) {				
					this.editArc=null;
				}
				if (editText==null) {
					if (this.changingEnt!=null) {
						this.changingEnt.changing=false;
						this.changingEnt=null;
					}
					clickOn=null;
					lastDrag=null;
					this.selecting=false;
				}
			}
			
			_dxf.sel.setText(_dxf.defSelTxtA+vectClickOn.size()+_dxf.txtB);
			_dxf.clipB.setText(_dxf.defClipTxtA+clipBoard.size()+_dxf.txtB);
			
		} else if (this.drawingEllipse) {
			double width=e.getX()-lastClick.getX();
			double height=e.getY()-lastClick.getY();
			double centerX = e.getX()+width/2;
			myEllipse el = new myEllipse(
					new myPoint(
							new Point2D.Double(centerX,e.getY()+height/2)
					),
					new myPoint(
							new Point2D.Double(centerX,e.getY())
					),
					myCoord.dxfToJava_X(width)/myCoord.dxfToJava_Y(height),
					0,
					360,
					DXF_Color.getColor(_dxf._jcc.getColor()),
					_dxf._u.currLayer,
					0,
					(myLineType) _dxf._comboLineType.getSelectedItem()
			);
			_dxf._u.currLayer._myEnt.addElement(el);
			_dxf.tree.addEntity(el);
			this.drawingEllipse=false;
		} else if (this.xSelecting) { //try catch --> stackOverFlow je crois
			myEntity testObj;
			for (int i=0;i<_dxf._u._myTables.size();i++) {
				for (int j=0;j<_dxf._u._myTables.elementAt(i)._myLayers.size();j++) {
					for (int k=0;k<_dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.size();k++) {
						testObj = _dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.elementAt(k);
						if (testObj instanceof myPoint) { 
							Rectangle2D.Double p = ((myPoint)testObj).getSelectedEntity();
							if ( (p!=null) &&  (zoomRect.contains(p)) ) {
								((myPoint)testObj).setSelected(true);
								vectClickOn.add(testObj);
							}				    
						} else if (testObj instanceof myText) {
							Rectangle2D.Double r = (Rectangle2D.Double)((myText)testObj).getSelectedEntity();
							if ((r!=null) && (zoomRect.contains(r))) {
								((myText)testObj).setSelected(true);
								vectClickOn.add(testObj);
							}							
						} else if (testObj instanceof mySolid) {
							GeneralPath p = ((mySolid)testObj).getSelectedEntity();
							if ((p!=null) && (zoomRect.contains(p.getBounds2D()))) {
								((mySolid)testObj).setSelected(true);
								vectClickOn.add(testObj);
							}	
						}  else if (testObj instanceof myLine) {
							Line2D.Double l = ((myLine)testObj).getSelectedEntity();
							if ((l!=null) && ((zoomRect.contains(l.x1, l.y1)) && (zoomRect.contains(l.x2, l.y2)))) {
								((myLine)testObj).setSelected(true);
								vectClickOn.add(testObj);
							}					
						} else if (testObj instanceof myCircle) {
							Ellipse2D.Double c = ((myCircle)testObj).getSelectedEntity();
							if ((c!=null) && (zoomRect.contains(c.getBounds2D()))) {
								((myCircle)testObj).setSelected(true);
								vectClickOn.add(testObj);
							}						
						} else if (testObj instanceof myPolyline) {
							GeneralPath p = ((myPolyline)testObj).getSelectedEntity();
							if ((p!=null) && (zoomRect.contains(p.getBounds2D()))) {
								((myPolyline)testObj).setSelected(true);
								vectClickOn.add(testObj);
							}	
						} else if (testObj instanceof myArc) {
							Arc2D.Double a = ((myArc)testObj).getSelectedEntity();
							if ((a!=null) && (zoomRect.contains(a.getBounds2D())) ) {
								((myArc)testObj).setSelected(true);
								vectClickOn.add(testObj);
							}					
						} else if (testObj instanceof myBlockReference) {
							((myBlockReference)testObj).setContainedEntitiesSelection(true,zoomRect,vectClickOn);
						}
					}
				}
			}	
			
			_dxf.sel.setText(_dxf.defSelTxtA+vectClickOn.size()+_dxf.txtB);
			_dxf.clipB.setText(_dxf.defClipTxtA+clipBoard.size()+_dxf.txtB);
			
			xSelecting=false;			
			zoomRect=null;
		}

		_dxf.tree.updateSelection();
		repaint();
	}

	public void mouseClicked(MouseEvent e){
		if (_dxf._typeOutil.getSelectedIndex()==myToolBar.toolPoint) {

			myPoint p =new myPoint(
					myCoord.javaToDXF_X(e.getX()),
					myCoord.javaToDXF_Y(e.getY()),
					DXF_Color.getColor(_dxf._jcc.getColor()),
					_dxf._u.currLayer,
					0,_dxf._u.currThickness
			);

			_dxf._u.currLayer._myEnt.addElement(p);
			_dxf.tree.addEntity(p);
		} else if (_dxf._typeOutil.getSelectedIndex()==myToolBar.toolPoly && !drawingPolyLineStart && !drawingPolyLineEnd) {
			tmpVectVertex=new Vector<myVertex>();
			tmpVectVertex.add(
					new myVertex(myCoord.javaToDXF_X(e.getX()),
							myCoord.javaToDXF_Y(e.getY()),
							0,
							DXF_Color.getColor(_dxf._jcc.getColor()),
							_dxf._u.currLayer,
							null,0)
			);
			this.drawingPolyLineStart=true;
			this.drawingPolyLineEnd=false;
		} else if  (drawingPolyLineStart && !drawingPolyLineEnd) {
			if (SwingUtilities.isRightMouseButton(e)) {
				tmpVectVertex.add(
						new myVertex(myCoord.javaToDXF_X(e.getX()),
								myCoord.javaToDXF_Y(e.getY()),
								0,
								DXF_Color.getColor(_dxf._jcc.getColor()),
								_dxf._u.currLayer,
								null,0)
				);

				myPolyline p = new myPolyline("def",
						0,
						DXF_Color.getColor(_dxf._jcc.getColor()),
						_dxf._u.currLayer,
						tmpVectVertex,0,(myLineType) _dxf._comboLineType.getSelectedItem(),
						_dxf._u.currThickness
				);
				_dxf._u.currLayer._myEnt.addElement(p);
				_dxf.tree.addEntity(p);
				tmpVectVertex = null;
				this.drawingPolyLineStart=false;
				this.drawingPolyLineEnd=false;
			} else if (e.getClickCount()==2) {
				myPolyline p = new myPolyline("def",
						1,
						DXF_Color.getColor(_dxf._jcc.getColor()),
						_dxf._u.currLayer,tmpVectVertex,0,(myLineType) _dxf._comboLineType.getSelectedItem(),
						_dxf._u.currThickness
				);
				_dxf._u.currLayer._myEnt.addElement(p);
				_dxf.tree.addEntity(p);
				tmpVectVertex = null;
				this.drawingPolyLineStart=false;
				this.drawingPolyLineEnd=false;
			} else {
				tmpVectVertex.add(
						new myVertex(myCoord.javaToDXF_X(e.getX()),
								myCoord.javaToDXF_Y(e.getY()),
								0,
								DXF_Color.getColor(_dxf._jcc.getColor()),
								_dxf._u.currLayer,
								null,0)
				);
			}
		} else if (_dxf._typeOutil.getSelectedIndex()==myToolBar.toolZoomCentre) {
			if (e.getClickCount()==2) {
				_dxf.Center(e.getPoint());
			}
		} else if (_dxf._typeOutil.getSelectedIndex()==myToolBar.toolPipette) {
			getSelectedObject(e.getX(),e.getY());
			if (clickOn!=null) {
				DXF_Color.setDefaultColor(clickOn._color);
			} 
		} else if ( (_dxf._typeOutil.getSelectedIndex()==myToolBar.toolTrace) || (_dxf._typeOutil.getSelectedIndex()==myToolBar.toolSolid) ) {
			quadCount++;
			switch (quadCount) {
			case 1 : quadPt[0]=new Point2D.Double(myCoord.javaToDXF_X(e.getX()),myCoord.javaToDXF_Y(e.getY()));
			drawingTrace=true;				
			break;
			case 2 : quadPt[1]=new Point2D.Double(myCoord.javaToDXF_X(e.getX()),myCoord.javaToDXF_Y(e.getY()));
			break;
			case 3 :
				if (SwingUtilities.isRightMouseButton(e)) {
					if (_dxf._typeOutil.getSelectedIndex()==myToolBar.toolTrace) {
						_dxf._u.currLayer._myEnt.add(new myTrace(
								new myPoint(quadPt[0],
										DXF_Color.getColor(_dxf._jcc.getColor()),
										_dxf._u.currLayer,
										0,
										(int)_dxf._u.currThickness),
										new myPoint(quadPt[1],
												DXF_Color.getColor(_dxf._jcc.getColor()),
												_dxf._u.currLayer,
												0,
												(int)_dxf._u.currThickness),
												new myPoint(new Point2D.Double(myCoord.javaToDXF_X(e.getX()),myCoord.javaToDXF_Y(e.getY())),
														DXF_Color.getColor(_dxf._jcc.getColor()),
														_dxf._u.currLayer,
														0,
														(int)_dxf._u.currThickness),
														null,
														_dxf._u.currThickness,
														DXF_Color.getColor(_dxf._jcc.getColor()),
														_dxf._u.currLayer,
														0,
														(myLineType)_dxf._comboLineType.getSelectedItem()));
					} else {
						_dxf._u.currLayer._myEnt.add(new mySolid(
								new myPoint(quadPt[0],
										DXF_Color.getColor(_dxf._jcc.getColor()),
										_dxf._u.currLayer,
										0,
										(int)_dxf._u.currThickness),
										new myPoint(quadPt[1],
												DXF_Color.getColor(_dxf._jcc.getColor()),
												_dxf._u.currLayer,
												0,
												(int)_dxf._u.currThickness),
												new myPoint(new Point2D.Double(myCoord.javaToDXF_X(e.getX()),myCoord.javaToDXF_Y(e.getY())),
														DXF_Color.getColor(_dxf._jcc.getColor()),
														_dxf._u.currLayer,
														0,
														(int)_dxf._u.currThickness),
														null,
														_dxf._u.currThickness,
														DXF_Color.getColor(_dxf._jcc.getColor()),
														_dxf._u.currLayer,
														0,
														(myLineType)_dxf._comboLineType.getSelectedItem()));
					}
					drawingTrace=false;
					quadCount=0;
					_dxf.tree.createNodes();
				} else 
					quadPt[2]=new Point2D.Double(myCoord.javaToDXF_X(e.getX()),myCoord.javaToDXF_Y(e.getY()));
				break;
			case 4 : 
				if (_dxf._typeOutil.getSelectedIndex()==myToolBar.toolTrace) {
					_dxf._u.currLayer._myEnt.add(new myTrace(
							new myPoint(quadPt[0],
									DXF_Color.getColor(_dxf._jcc.getColor()),
									_dxf._u.currLayer,
									0,
									(int)_dxf._u.currThickness),
									new myPoint(quadPt[1],
											DXF_Color.getColor(_dxf._jcc.getColor()),
											_dxf._u.currLayer,
											0,
											(int)_dxf._u.currThickness),
											new myPoint(quadPt[2],
													DXF_Color.getColor(_dxf._jcc.getColor()),
													_dxf._u.currLayer,
													0,
													(int)_dxf._u.currThickness),
													new myPoint(new Point2D.Double(myCoord.javaToDXF_X(e.getX()),myCoord.javaToDXF_Y(e.getY())),
															DXF_Color.getColor(_dxf._jcc.getColor()),
															_dxf._u.currLayer,
															0,
															(int)_dxf._u.currThickness),
															_dxf._u.currThickness,
															DXF_Color.getColor(_dxf._jcc.getColor()),
															_dxf._u.currLayer,
															0,
															(myLineType)_dxf._comboLineType.getSelectedItem()));
				} else {
					_dxf._u.currLayer._myEnt.add(new mySolid(
							new myPoint(quadPt[0],
									DXF_Color.getColor(_dxf._jcc.getColor()),
									_dxf._u.currLayer,
									0,
									(int)_dxf._u.currThickness),
									new myPoint(quadPt[1],
											DXF_Color.getColor(_dxf._jcc.getColor()),
											_dxf._u.currLayer,
											0,
											(int)_dxf._u.currThickness),
											new myPoint(quadPt[2],
													DXF_Color.getColor(_dxf._jcc.getColor()),
													_dxf._u.currLayer,
													0,
													(int)_dxf._u.currThickness),
													new myPoint(new Point2D.Double(myCoord.javaToDXF_X(e.getX()),myCoord.javaToDXF_Y(e.getY())),
															DXF_Color.getColor(_dxf._jcc.getColor()),
															_dxf._u.currLayer,
															0,
															(int)_dxf._u.currThickness),
															_dxf._u.currThickness,
															DXF_Color.getColor(_dxf._jcc.getColor()),
															_dxf._u.currLayer,
															0,
															(myLineType)_dxf._comboLineType.getSelectedItem()));
				}
				//this.lastClick=null;
				drawingTrace=false;
				quadCount=0;
				_dxf.tree.createNodes();
				break;
			}

		} else if (_dxf._typeOutil.getSelectedIndex()==myToolBar.toolTxt) {
			myText m = new myText(
					myCoord.javaToDXF_X(e.getX()),
					myCoord.javaToDXF_Y(e.getY()),
					DXF_Loader.res.getString("myCanvas.0"),
					0,
					_dxf._u.currThickness,
					20, //Height
					5, 	//Align
					"style",
					DXF_Color.getColor(_dxf._jcc.getColor()),
					_dxf._u.currLayer,
					1, // zoomfactor
					0, //Angle
					0, //Visibility
					(myLineType)_dxf._comboLineType.getSelectedItem());
			_dxf._u.currLayer._myEnt.add(m);
			editText=m;
			drawingTxt=true;
			firstTxt=true;
			_dxf.tree.createNodes();
		} 
		_dxf.tree.updateSelection();
		repaint();
	}

	public void mouseExited(MouseEvent e) { 
		_dxf.coordXY.setText("");
	}

	public void mouseEntered(MouseEvent e) {		
		_dxf.coordXY.setText("   X:  "+myCoord.javaToDXF_X(e.getX())+"  Y:  "+(myCoord.javaToDXF_Y(e.getY())-1));
	}

	public void mouseMoved(MouseEvent e){
		currPoint = e.getPoint();
		_dxf.coordXY.setText("   X:  "+myCoord.javaToDXF_X(currPoint.x)+"  Y:  "+(myCoord.javaToDXF_Y(currPoint.y)-1));
	}

	public void stateChanged(ChangeEvent arg0) { }
	public void componentHidden(ComponentEvent arg0) { }
	public void componentResized(ComponentEvent arg0) {
		dim = getSize();
		myCoord.Max = dim.width;
		area = new Rectangle(dim);
		if (dim.width>0 && dim.height>0)
			bi = (BufferedImage)createImage(dim.width,dim.height);
		big = bi.createGraphics();
	}
	public void componentShown(ComponentEvent arg0) { }
	public void componentMoved(ComponentEvent arg0) { }

	public void keyPressed(KeyEvent arg0) { }

	public void keyReleased(KeyEvent arg0) { }

	public void keyTyped(KeyEvent ke) { 
		//if ((ke.getModifiers()== KeyEvent.CTRL_MASK) && (ke.getKeyCode() == KeyEvent.VK_A))
		if ((_dxf._typeOutil.getSelectedIndex()==myToolBar.toolSel)) {
			int x=MouseInfo.getPointerInfo().getLocation().x;
			int y=MouseInfo.getPointerInfo().getLocation().y;
			
			if ((drawingTxt) && (editText!=null)) {
				if (firstTxt) {
					editText.setVal("");
					firstTxt=false;
				}
				if (ke.getKeyChar() == KeyEvent.VK_ENTER) {					
					drawingTxt=false;
					if (this.changingEnt!=null) {
						this.changingEnt.changing=false;
						this.changingEnt=null;
					}
					clickOn=null;
				} else if ((ke.getKeyChar() == KeyEvent.VK_DELETE) || (ke.getKeyChar() == KeyEvent.VK_BACK_SPACE)) {		
					editText.delChar();
				} else 
					editText.appendVal(ke.getKeyChar());
			} else {	
				if (vectClickOn.size()>0) { 
					if ( (KeyEvent.getKeyText(ke.getKeyChar()).equalsIgnoreCase("Annuler")) || (ke.getKeyChar()=='c') || (ke.getKeyChar()=='C')) {
						copySelectedObjectsToClipboard();				
					} else if (ke.getKeyChar()=='x' || ke.getKeyChar()=='X') {
						cutSelectedObjectsToClipboard();
					}
				}
				if (ke.getKeyChar()=='p' || ke.getKeyChar()=='P') {
					if (this.clipBoard!=null) {
						pasteClipboardContents();
					}
				} 
				else if (ke.getKeyChar()=='a' || ke.getKeyChar()=='A') {
					myEntity ent;
					vectClickOn.removeAllElements();
					for (int i=0;i<_dxf._u._myTables.size();i++) {
						for (int j=0;j<_dxf._u._myTables.elementAt(i)._myLayers.size();j++) {
							for (int k=0;k<_dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.size();k++) {
								ent = _dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.elementAt(k);
								ent.setSelected(true);
								vectClickOn.add(ent);
							}
						}
					}
				}
				else if ( (ke.getKeyChar()=='i' || ke.getKeyChar()=='I') ) {
					myBlock b = new myBlock(x, y, 0, myNameGenerator.getBlockName(DXF_Loader.res.getString("myCanvas.1")), new Vector<myEntity>(), DXF_Color.getColor(_dxf._jcc.getColor()), _dxf._u.currLayer, _dxf._u);
					_dxf._u._myBlocks.add(b);
					_dxf._u.currBlock=b;
					myEntity ent;
					for (int i=0;i<_dxf._u._myTables.size();i++) {
						for (int j=0;j<_dxf._u._myTables.elementAt(i)._myLayers.size();j++) {
							int end=_dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.size()-1;
							for (int k=end;k>=0;k--) {
								ent = _dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.elementAt(k);
								if (ent.selected) {
									ent.setSelected(false);
									b._myEnt.addElement(ent);
									_dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.removeElementAt(k);
								}
							}
						}
					}				
					_dxf._u.currLayer._myEnt.add(new myInsert(
							x,
							y,
							b._name,
							b,
							_dxf._u.currLayer,
							0,
							DXF_Color.getColor(_dxf._jcc.getColor()),
							(myLineType)_dxf._comboLineType.getSelectedItem())
					);
				}
				else if ( (ke.getKeyChar()=='d' || ke.getKeyChar()=='D') ) {
					myBlock b = new myBlock(x, y, 0, myNameGenerator.getBlockName(DXF_Loader.res.getString("myCanvas.1")), new Vector<myEntity>(), DXF_Color.getColor(_dxf._jcc.getColor()), _dxf._u.currLayer, _dxf._u);
					_dxf._u._myBlocks.add(b);
					_dxf._u.currBlock=b;
					myEntity ent;
					for (int i=0;i<_dxf._u._myTables.size();i++) {
						for (int j=0;j<_dxf._u._myTables.elementAt(i)._myLayers.size();j++) {
							int end=_dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.size()-1;
							for (int k=end;k>=0;k--) {
								ent = _dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.elementAt(k);
								if (ent.selected) {
									ent.setSelected(false);
									b._myEnt.addElement(ent);
									_dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.removeElementAt(k);
								}
							}
						}
					}				
					_dxf._u.currLayer._myEnt.add(new myDimension(
							0,
							myNameGenerator.getDimensionName(DXF_Loader.res.getString("myCanvas.2")),
							x,
							y,
							b,
							b._name,
							_dxf._u.currLayer,
							0,
							DXF_Color.getColor(_dxf._jcc.getColor()),
							(myLineType)_dxf._comboLineType.getSelectedItem())
					);
				}
				else if ( (ke.getKeyChar()=='b' || ke.getKeyChar()=='B')) {
					myEntity ent;
					for (int i=0;i<_dxf._u._myTables.size();i++) {
						for (int j=0;j<_dxf._u._myTables.elementAt(i)._myLayers.size();j++) {
							int end=_dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.size()-1;
							for (int k=end;k>=0;k--) {
								ent = _dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.elementAt(k);
								if (ent.selected) {
									ent.setSelected(false);
									if (_dxf._u.currBlock==null) {
										myBlock b = new myBlock(x, y, 0, myNameGenerator.getBlockName(DXF_Loader.res.getString("myCanvas.1")), new Vector<myEntity>(), DXF_Color.getColor(_dxf._jcc.getColor()), _dxf._u.currLayer, _dxf._u);
										_dxf._u._myBlocks.add(b);
										_dxf._u.currBlock=b;
									}
									_dxf._u.currBlock._myEnt.addElement(ent);
									_dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.removeElementAt(k);
								}
							}
						}
					}
					vectClickOn.removeAllElements();
				}
				else if ( (ke.getKeyChar() == KeyEvent.VK_DELETE) || (KeyEvent.getKeyText(ke.getKeyChar()).equalsIgnoreCase("Supprimer"))) {
					for (int i=0;i<_dxf._u._myTables.size();i++) {
						for (int j=0;j<_dxf._u._myTables.elementAt(i)._myLayers.size();j++) {
							int end=_dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.size()-1;
							for (int k=end;k>=0;k--) {
								clickOn = _dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.elementAt(k);
								if (clickOn.selected) {
									_dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.removeElementAt(k);
								}
							}
						}
					}
				} else if (ke.getKeyChar() == KeyEvent.VK_ESCAPE) {
					for (int i=0;i<vectClickOn.size();i++) 
						vectClickOn.elementAt(i).setSelected(false);
					vectClickOn.removeAllElements();
				}
			}

			_dxf.sel.setText(_dxf.defSelTxtA+vectClickOn.size()+_dxf.txtB);
			_dxf.clipB.setText(_dxf.defClipTxtA+clipBoard.size()+_dxf.txtB);
			
		} else if (_dxf._typeOutil.getSelectedIndex()==myToolBar.toolSelX) {
			if (ke.getKeyChar() == KeyEvent.VK_ESCAPE) {
				for (int i=0;i<vectClickOn.size();i++) 
					vectClickOn.elementAt(i).setSelected(false);
			}

			_dxf.sel.setText(_dxf.defSelTxtA+vectClickOn.size()+_dxf.txtB);
			_dxf.clipB.setText(_dxf.defClipTxtA+clipBoard.size()+_dxf.txtB);
			
		} else if ((drawingTxt) && (editText!=null)) {
			if (firstTxt) {
				editText.setVal("");
				firstTxt=false;
			}
			if (ke.getKeyChar() == KeyEvent.VK_ENTER) {					
				drawingTxt=false;
			} else if ((ke.getKeyChar() == KeyEvent.VK_DELETE) || (ke.getKeyChar() == KeyEvent.VK_BACK_SPACE)) {		
				editText.delChar();
			} else 
				editText.appendVal(ke.getKeyChar());
		}
		_dxf.tree.createNodes();
	}

	private void pasteClipboardContents() {
		myEntity ent;
		double x=0,y=0;
		if (origPoint!=null) {
			x=origPoint.getX()-MouseInfo.getPointerInfo().getLocation().x;
			y=origPoint.getY()-MouseInfo.getPointerInfo().getLocation().y;
		}
		for (int i=0;i<clipBoard.size();i++) {
			ent=clipBoard.elementAt(i);
			if (ent instanceof myLine) {
				myLine l = new myLine((myLine)ent);
				l.translate(x,y);
				_dxf._u.currLayer._myEnt.add(l);
			} else if (ent instanceof myCircle) {
				myCircle c = new myCircle((myCircle)ent);
				c.translate(x,y);
				_dxf._u.currLayer._myEnt.add(c);
			} else if (ent instanceof myPolyline) {
				myPolyline p = new myPolyline((myPolyline)ent);
				p.translate(x,y);
				_dxf._u.currLayer._myEnt.add(p);
			} else if (ent instanceof myArc) {
				myArc a = new myArc((myArc)ent);
				a.translate(x,y);
				_dxf._u.currLayer._myEnt.add(a);
			} else if (ent instanceof myPoint) {
				myPoint m = new myPoint((myPoint)ent);
				m.translate(x,y);
				_dxf._u.currLayer._myEnt.add(m);
			} else if (ent instanceof myText) {
				myText m = new myText((myText)ent);
				m.translate(x,y);
				_dxf._u.currLayer._myEnt.add(m);
			}  else if (ent instanceof myTrace) {
				myTrace m = new myTrace((myTrace)ent);
				m.translate(x,y);
				_dxf._u.currLayer._myEnt.add(m);
			}  else if (ent instanceof mySolid) {
				mySolid m = new mySolid((mySolid)ent);
				m.translate(x,y);
				_dxf._u.currLayer._myEnt.add(m);
			} else if (ent instanceof myBlockReference) {
				pasteTx(((myBlockReference)ent)._refBlock._myEnt,x,y);
			}		
		}
	}
	
	public static HashSet getTable(){
		HashSet entities= new HashSet();
		myEntity ent;	
		for (int i=0;i<_dxf._u._myTables.size();i++) {
			for (int j=0;j<_dxf._u._myTables.elementAt(i)._myLayers.size();j++) {
				int end=_dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.size()-1;
				for (int k=end;k>=0;k--) {
					ent = _dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.elementAt(k);
					entities.add(ent);
				}
			}
		}
		return entities;					
	}
	
	public void pasteTx(Vector<myEntity> vmy, double x, double y) {
		myEntity my;
		for (int a=0;a<vmy.size();a++) {			
			my = vmy.elementAt(a);
			if (my instanceof myLine) {
				_dxf._u.currLayer._myEnt.add((new myLine((myLine)my)));
				_dxf._u.currLayer._myEnt.lastElement().translate(x,y);
				_dxf._u.currLayer._myEnt.lastElement()._refLayer=_dxf._u.currLayer;
			} else if (my instanceof myCircle) {
				_dxf._u.currLayer._myEnt.add((new myCircle((myCircle)my)));
				_dxf._u.currLayer._myEnt.lastElement().translate(x,y);
				_dxf._u.currLayer._myEnt.lastElement()._refLayer=_dxf._u.currLayer;
			} else if (my instanceof myPolyline) {
				_dxf._u.currLayer._myEnt.add((new myPolyline((myPolyline)my)));
				_dxf._u.currLayer._myEnt.lastElement().translate(x,y);
				_dxf._u.currLayer._myEnt.lastElement()._refLayer=_dxf._u.currLayer;
			} else if (my instanceof myArc) {
				_dxf._u.currLayer._myEnt.add((new myArc((myArc)my)));
				_dxf._u.currLayer._myEnt.lastElement().translate(x,y);
				_dxf._u.currLayer._myEnt.lastElement()._refLayer=_dxf._u.currLayer;
			} else if (my instanceof myText) {
				_dxf._u.currLayer._myEnt.add((new myText((myText)my)));
				_dxf._u.currLayer._myEnt.lastElement().translate(x,y);
				_dxf._u.currLayer._myEnt.lastElement()._refLayer=_dxf._u.currLayer;
			} else if (my instanceof mySolid) {
				_dxf._u.currLayer._myEnt.add((new mySolid((mySolid)my)));
				_dxf._u.currLayer._myEnt.lastElement().translate(x,y);
				_dxf._u.currLayer._myEnt.lastElement()._refLayer=_dxf._u.currLayer;
			} else if (my instanceof myPoint) {
				_dxf._u.currLayer._myEnt.add((new myPoint((myPoint)my)));
				_dxf._u.currLayer._myEnt.lastElement().translate(x,y);
				_dxf._u.currLayer._myEnt.lastElement()._refLayer=_dxf._u.currLayer;
			} else if (my instanceof myBlockReference) {
				pasteTx(((myBlockReference)my)._refBlock._myEnt,x,y); //Catch overflow ?
			} 			
		}
	}

	public void cutSelectedObjectsToClipboard() {
		copySelectedObjectsToClipboard();
		origPoint=MouseInfo.getPointerInfo().getLocation();
		myEntity test; 
		for (int i=0;i<_dxf._u._myTables.size();i++) {
			for (int j=0;j<_dxf._u._myTables.elementAt(i)._myLayers.size();j++) {
				int end=_dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.size()-1;
				for (int k=end;k>=0;k--) {
					test = _dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.elementAt(k);
					if (test.selected) {
						_dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.removeElementAt(k);
					}
				}
			}
		}
		vectClickOn.removeAllElements();
	}

	public void copySelectedObjectsToClipboard() {
		origPoint=null;
		this.clipBoard.removeAllElements();
		for (int i=0;i<vectClickOn.size();i++) {
			this.clipBoard.addElement(vectClickOn.elementAt(i));
		}
		
	}

}

/*public void delClosestObject(double x, double y) {
	double min=Double.MAX_VALUE,calcMin=0;
	Point2D.Double clickPoint=new Point2D.Double();
	int closestI=-1,closestJ=-1,closestK=-1;
	for (int i=0;i<_dxf._u._myTables.size();i++) {
		for (int j=0;j<_dxf._u._myTables.elementAt(i)._myLayers.size();j++) {
			for (int k=0;k<_dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.size();k++) {
				clickOn = _dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.elementAt(k);
				if (clickOn instanceof myPoint) { 
			    	Point2D.Double p = ((myPoint)clickOn).getSelectedEntity();
					if ( (p!=null) &&  ((p.getX()==x)&&(p.getY()==y)) ) {
						_dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.removeElementAt(k);
					}
					clickPoint.setLocation(x,y);					    
					calcMin=p.distance(clickPoint);
					if (calcMin  < min) {
						min=calcMin;
						closestI=i;
						closestJ=j;
						closestK=k;							
					}						
			    } else if (clickOn instanceof myLine) {
					Line2D.Double l = ((myLine)clickOn).getSelectedEntity();
					if ((l!=null) && (l.intersects(x, y, 5, 5))) {
						_dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.removeElementAt(k);
					}
					clickPoint.setLocation(x,y);					    
					calcMin=l.getP1().distance(clickPoint);
					if (calcMin  < min) {
						min=calcMin;
						closestI=i;
						closestJ=j;
						closestK=k;
					}											    
					calcMin=l.getP2().distance(clickPoint);
					if (calcMin  < min) {
						min=calcMin;
						closestI=i;
						closestJ=j;
						closestK=k;
					}						
			    } else if (clickOn instanceof myCircle) {
					Ellipse2D.Double c = ((myCircle)clickOn).getSelectedEntity();
					Ellipse2D.Double in_c =  myCircle.getSmallerGraphicEntity(c);
					if ((c!=null) && (c.intersects(x, y, 5, 5)) && (!in_c.intersects(x, y, 1, 1))) {
						_dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.removeElementAt(k);
					}
					clickPoint.setLocation(x,y);					    
					calcMin=new Point2D.Double(c.getCenterX(),c.getCenterY()).distance(clickPoint);
					if (calcMin  < min) {
						min=calcMin;
						closestI=i;
						closestJ=j;
						closestK=k;
					}						
				} else if (clickOn instanceof myEllipse) {
					Arc2D.Double e = ((myEllipse)clickOn).getSelectedEntity();
					if ((e!=null) && (e.intersects(x, y, 5, 5))) {
						_dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.removeElementAt(k);
					}
					clickPoint.setLocation(x,y);					    
					calcMin=new Point2D.Double(e.getCenterX(),e.getCenterY()).distance(clickPoint);
					if (calcMin  < min) {
						min=calcMin;
						closestI=i;
						closestJ=j;
						closestK=k;
					}						
				} else if (clickOn instanceof myPolyline) {
					GeneralPath p = ((myPolyline)clickOn).getSelectedEntity();
					if ((p!=null) && (p.intersects(x, y, 5, 5))) {
						_dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.removeElementAt(k);
					}
					clickPoint.setLocation(x,y);					    
					for (int a=0;a<((myPolyline)clickOn)._myVertex.size();a++) {
						calcMin=new Point2D.Double(((myPolyline)clickOn)._myVertex.elementAt(a).X(),((myPolyline)clickOn)._myVertex.elementAt(a).Y()).distance(clickPoint);
						if (calcMin  < min) {
							min=calcMin;
							closestI=i;
							closestJ=j;
							closestK=k;
						}						
					}
				} else if (clickOn instanceof myArc) {
					Arc2D.Double a = ((myArc)clickOn).getSelectedEntity();
					if ((a!=null) && (a.intersects(x, y, 5, 5))) {
						_dxf._u._myTables.elementAt(i)._myLayers.elementAt(j)._myEnt.removeElementAt(k);
					}				
					clickPoint.setLocation(x,y);					    
					calcMin=new Point2D.Double(a.getCenterX(),a.getCenterY()).distance(clickPoint);
					if (calcMin  < min) {
						min=calcMin;
						closestI=i;
						closestJ=j;
						closestK=k;
					}						
				}
			}
		}
	}
	if (closestI!=-1) {
		_dxf._u._myTables.elementAt(closestI)._myLayers.elementAt(closestJ)._myEnt.removeElementAt(closestK);
	} 
}*/

