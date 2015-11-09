package fr.epsi.dxf.Entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import fr.epsi.dxf.DXF_Loader;
import fr.epsi.dxf.Graphics.myCanvas;
import fr.epsi.dxf.Graphics.myLabel;
import fr.epsi.dxf.Header.myBlock;
import fr.epsi.dxf.Header.myLayer;
import fr.epsi.dxf.Header.myLineType;
import fr.epsi.dxf.Header.myTable;

public abstract class myBlockReference extends myEntity {

	public myBlock	_refBlock;
	public String	_blockName;

	abstract public void draw(Graphics g);
	abstract public myLabel getNewLabel(String code, Object newValue);
	abstract public DefaultMutableTreeNode getNode() ;
	abstract public void write(FileWriter out) throws IOException ;
	
	public myBlockReference(int c, myLayer l, int visibility, myLineType lineType, String nomBlock, myBlock refBlock) {
		super(c, l, visibility, lineType, myTable.defaultThickness);
		
		_refBlock 	= refBlock;
		_blockName 	= nomBlock;
	}

	public myEntity getSelectedEntity() {
		getSelectedEntity(myCanvas.selEntityVar);
		return myCanvas.clickOn;
	}
	
	public void writeCommon(FileWriter out) throws IOException {
		out.write("2\n");
		out.write(_blockName+"\n");
		super.writeCommon(out);		
	}

	public boolean getSelectedEntity(double var[]) {
		double x=var[0],y=var[1],min=var[2];
		if (_refBlock==null) return false;
		double calcMin=0;
		myEntity testObj = null;
		myEntity closestObj = null;
		Point2D.Double clickPoint=new Point2D.Double();
		for (int k=0;k<_refBlock._myEnt.size();k++) {
			testObj =_refBlock._myEnt.elementAt(k);
			if (testObj instanceof myPoint) { 
				Rectangle2D.Double p = ((myPoint)testObj).getSelectedEntity();
				if ( (p!=null) &&  (p.intersects(x, y, 5, 5))) {
					myCanvas.clickOn=testObj;return true;
				}
				clickPoint.setLocation(x,y);					    
				calcMin=((myPoint)testObj)._point.distance(clickPoint);
				if (calcMin  < min) {
					min=calcMin;
					closestObj=testObj;
				}				
		    } else if (testObj instanceof myLine) {
				Line2D.Double l = ((myLine)testObj).getSelectedEntity();
				if ((l!=null) && (l.intersects(x, y, 5, 5))) {
					myCanvas.clickOn=testObj;return true;
				}
				clickPoint.setLocation(x,y);					    
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
					myCanvas.clickOn=testObj;return true;
				}
				clickPoint.setLocation(x,y);					    
				calcMin=new Point2D.Double(c.getCenterX(),c.getCenterY()).distance(clickPoint);
				if (calcMin  < min) {
					min=calcMin;
					closestObj=testObj;
				}						
			} else if (testObj instanceof myPolyline) {
				GeneralPath p = ((myPolyline)testObj).getSelectedEntity();
				if ((p!=null) && (p.intersects(x, y, 5, 5))) {
					myCanvas.clickOn=testObj;return true;
				}
				clickPoint.setLocation(x,y);					    
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
					myCanvas.clickOn=testObj;return true;
				}			
				clickPoint.setLocation(x,y);					    
				calcMin=new Point2D.Double(a.getCenterX(),a.getCenterY()).distance(clickPoint);
				if (calcMin  < min) {
					min=calcMin;
					closestObj=testObj;
				}					
			} else if (testObj instanceof myBlockReference) {
				myCanvas.selEntityVar[0]=x;
				myCanvas.selEntityVar[1]=y;
				myCanvas.selEntityVar[2]=min;
				if (((myBlockReference)testObj).getSelectedEntity(myCanvas.selEntityVar)) {
					myCanvas.clickOn=testObj;return true;
				}
			}
			testObj=null;
		}
		if (DXF_Loader._mc._dxf.proximitySelection) {
			if (myCanvas.clickOn==null)
				myCanvas.clickOn=closestObj;
		}
		return false;
	}


	public void setSelected(boolean s) {
		for (int i=0;i<_refBlock._myEnt.size();i++) {
			_refBlock._myEnt.elementAt(i).setSelected(s);
		}
		this.selected=s;
	}


	public void translate(double x, double y) {
		myEntity clickOn;
		for (int i=0;i<_refBlock._myEnt.size();i++) {
			clickOn = _refBlock._myEnt.elementAt(i);
			if (clickOn instanceof myLine) {
				((myLine)clickOn).translate(x,y);
			} else if (clickOn instanceof myCircle) {
				((myCircle)clickOn).translate(x,y);
			} else if (clickOn instanceof myPolyline) {
				((myPolyline)clickOn).translate(x,y);
			} else if (clickOn instanceof myArc) {
				((myArc)clickOn).translate(x,y);
			} else if (clickOn instanceof myText) {
				((myText)clickOn).translate(x,y);
			} else if (clickOn instanceof mySolid) {
				((mySolid)clickOn).translate(x,y);
			} else if (clickOn instanceof myPoint) {
				((myPoint)clickOn).translate(x,y);
			} else if (clickOn instanceof myBlockReference) {
				((myBlockReference)clickOn).translate(x,y);
			} 
		}
	}

	public void setContainedEntitiesSelection(boolean b,Rectangle zoomRect,Vector <myEntity> vectClickOn) {
		myEntity testObj;
		for (int k=0;k<_refBlock._myEnt.size();k++) {
			testObj = _refBlock._myEnt.elementAt(k);
			if (testObj instanceof myPoint) { 
				Rectangle2D.Double p = ((myPoint)testObj).getSelectedEntity();
				if ( (p!=null) &&  (zoomRect.contains(p)) ) {
					((myPoint)testObj).setSelected(b);
					vectClickOn.add(testObj);
				}				    
			} else if (testObj instanceof myText) {
				Rectangle2D.Double r = (Rectangle2D.Double)((myText)testObj).getSelectedEntity();
				if ((r!=null) && (zoomRect.contains(r))) {
					((myText)testObj).setSelected(b);
					vectClickOn.add(testObj);
				}							
			} else if (testObj instanceof mySolid) {
				GeneralPath p = ((mySolid)testObj).getSelectedEntity();
				if ((p!=null) && (zoomRect.contains(p.getBounds2D()))) {
					((mySolid)testObj).setSelected(b);
					vectClickOn.add(testObj);
				}	
		    }  else if (testObj instanceof myLine) {
				Line2D.Double l = ((myLine)testObj).getSelectedEntity();
				if ((l!=null) && ((zoomRect.contains(l.x1, l.y1)) && (zoomRect.contains(l.x2, l.y2)))) {
					((myLine)testObj).setSelected(b);
					vectClickOn.add(testObj);
				}					
		    } else if (testObj instanceof myCircle) {
				Ellipse2D.Double c = ((myCircle)testObj).getSelectedEntity();
				if ((c!=null) && (zoomRect.contains(c.getBounds2D()))) {
					((myCircle)testObj).setSelected(b);
					vectClickOn.add(testObj);
				}						
			} else if (testObj instanceof myPolyline) {
				GeneralPath p = ((myPolyline)testObj).getSelectedEntity();
				if ((p!=null) && (zoomRect.contains(p.getBounds2D()))) {
					((myPolyline)testObj).setSelected(b);
					vectClickOn.add(testObj);
				}	
			} else if (testObj instanceof myArc) {
				Arc2D.Double a = ((myArc)testObj).getSelectedEntity();
				if ((a!=null) && (zoomRect.contains(a.getBounds2D())) ) {
					((myArc)testObj).setSelected(b);
					vectClickOn.add(testObj);
				}					
			} else if (testObj instanceof myBlockReference) {
				((myBlockReference)testObj).setContainedEntitiesSelection(b,zoomRect,vectClickOn);
			}
		}
	}
}
