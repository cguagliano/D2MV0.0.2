package fr.epsi.dxf.Graphics;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import fr.epsi.dxf.DXF_Loader;


public class myToolBar extends JToolBar implements ActionListener{

	private static final long serialVersionUID = 1L;
	public int selectedIndex=0;
	
	public static final int toolPoint=0;
	public static final int toolLine=1;
	public static final int toolArc=2;
	public static final int toolPoly=3;
	public static final int toolCircle=4;
	public static final int toolEllipse=5;
	public static final int toolSel=6;
	public static final int toolDeplace=7;
	public static final int toolZoom=8;
	public static final int toolZoomCentre=9;
	public static final int toolPipette=10;
	public static final int toolTrace=11;
	public static final int toolTxt=12;
	public static final int toolSolid=13;
	public static final int toolSelX=14;
	/**
	 * @uml.property  name="lastTool"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JButton lastTool;
	
	public myToolBar() {
		super(DXF_Loader.res.getString("myToolBar.0"));
		this.setLayout(new GridLayout(1,10));
		this.setMinimumSize(new Dimension(350,30));
		this.setPreferredSize(new Dimension(350,30));
		
		JButton point = new JButton();
		point.setEnabled(false);
		point.addActionListener(this);
		point.setActionCommand("point");
		point.setToolTipText(DXF_Loader.res.getString("myToolBar.2"));
		point.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/point.gif")));
		this.add(point);
		lastTool=point;

		JButton line = new JButton();
		line.addActionListener(this);
		line.setActionCommand("line");
		line.setToolTipText(DXF_Loader.res.getString("myToolBar.5"));
		line.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/ligne.gif")));
		this.add(line);

		JButton arc = new JButton();
		arc.addActionListener(this);
		arc.setActionCommand("arc");
		arc.setToolTipText(DXF_Loader.res.getString("myToolBar.8"));
		arc.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/arc.gif")));
		this.add(arc);

		JButton cercle = new JButton();
		cercle.addActionListener(this);
		cercle.setActionCommand("cercle");
		cercle.setToolTipText(DXF_Loader.res.getString("myToolBar.11"));
		cercle.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/cercle.gif")));
		this.add(cercle);

		JButton ellipse = new JButton();
		ellipse.addActionListener(this);
		ellipse.setActionCommand("ellipse");
		ellipse.setToolTipText(DXF_Loader.res.getString("myToolBar.14"));
		ellipse.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/ellipse.gif")));
		this.add(ellipse);
		ellipse.setEnabled(false);

		JButton poly = new JButton();
		poly.addActionListener(this);
		poly.setActionCommand("poly");
		poly.setToolTipText(DXF_Loader.res.getString("myToolBar.17"));
		poly.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/poly.gif")));
		this.add(poly);

		JButton trace = new JButton();
		trace.addActionListener(this);
		trace.setActionCommand("trace");
		trace.setToolTipText(DXF_Loader.res.getString("myToolBar.20"));
		trace.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/Trace.png")));
		this.add(trace);

		JButton solid = new JButton();
		solid.addActionListener(this);
		solid.setActionCommand("solid");
		solid.setToolTipText(DXF_Loader.res.getString("myToolBar.23"));
		solid.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/solid.png")));
		this.add(solid);

		JButton txt = new JButton();
		txt.addActionListener(this);
		txt.setActionCommand("txt");
		txt.setToolTipText(DXF_Loader.res.getString("myToolBar.26"));
		txt.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/texte.jpg")));
		this.add(txt);

		JButton selection = new JButton();
		selection.addActionListener(this);
		selection.setActionCommand("selection");
		selection.setToolTipText(DXF_Loader.res.getString("myToolBar.29"));
		selection.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/selection.gif")));
		this.add(selection);
		
		JButton grab = new JButton();
		grab.addActionListener(this);
		grab.setActionCommand("grab");
		grab.setToolTipText(DXF_Loader.res.getString("myToolBar.32"));
		grab.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/multiselect.png")));
		this.add(grab);

		JButton move = new JButton();
		move.addActionListener(this);
		move.setActionCommand("move");
		move.setToolTipText(DXF_Loader.res.getString("myToolBar.35"));
		move.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/move.gif")));
		this.add(move);

		JButton zoom = new JButton();
		zoom.addActionListener(this);
		zoom.setActionCommand("zoom");
		zoom.setToolTipText(DXF_Loader.res.getString("myToolBar.38"));
		zoom.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/zoom.gif")));
		this.add(zoom);

		JButton pipette = new JButton();
		pipette.addActionListener(this);
		pipette.setActionCommand("pipette");
		pipette.setToolTipText(DXF_Loader.res.getString("myToolBar.41"));
		pipette.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/pipette.gif")));
		this.add(pipette);
}

	public void reset(boolean s) {
		if (s) {
			for (int i=0;i<DXF_Loader._mc.vectClickOn.size();i++) 
				DXF_Loader._mc.vectClickOn.elementAt(i).setSelected(false);
			DXF_Loader._mc.vectClickOn.removeAllElements();
		}
		
		DXF_Loader._mc.selecting=false;
		DXF_Loader._mc.moving=false;
		DXF_Loader._mc.zooming=false;
		DXF_Loader._mc.drawingCircle=false;
		DXF_Loader._mc.drawingPolyLineStart=false;
		DXF_Loader._mc.drawingPolyLineEnd=false;
		DXF_Loader._mc.drawingArc=false;
		DXF_Loader._mc.drawingArcAngleStart=false;
		DXF_Loader._mc.drawingArcAngleEnd=false;
		DXF_Loader._mc.drawingEllipse=false;
		DXF_Loader._mc.drawingTrace=false;
		DXF_Loader._mc.drawingTxt=false;
		DXF_Loader._mc.drawingSolid=false;
		
		DXF_Loader._mc._dxf.sel.setText(DXF_Loader._mc._dxf.defSelTxtA+DXF_Loader._mc.vectClickOn.size()+DXF_Loader._mc._dxf.txtB);
		DXF_Loader._mc._dxf.clipB.setText(DXF_Loader._mc._dxf.defClipTxtA+DXF_Loader._mc.clipBoard.size()+DXF_Loader._mc._dxf.txtB);
	}
	
	public void actionPerformed(ActionEvent e) {
		setCursor(Cursor.getDefaultCursor());
		DXF_Loader._mc.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		boolean s = true;
		if (e.getActionCommand()=="point") {
			selectedIndex=toolPoint;
		} else if (e.getActionCommand()=="line") {
			selectedIndex=toolLine;
		} else if (e.getActionCommand()=="arc") {
			selectedIndex=toolArc;	
		} else if (e.getActionCommand()=="cercle") {
			selectedIndex=toolCircle;
		} else if (e.getActionCommand()=="ellipse") {
			selectedIndex=toolEllipse;
		} else if (e.getActionCommand()=="poly") {
			selectedIndex=toolPoly;	
		} else if (e.getActionCommand()=="selection") {
			DXF_Loader._mc.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) );
			selectedIndex=toolSel;	
			s=false;
		}  else if (e.getActionCommand()=="grab") {
			DXF_Loader._mc.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) );
			selectedIndex=toolSelX;	
		} else if (e.getActionCommand()=="move") {
			DXF_Loader._mc.setCursor (Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR) );
			selectedIndex=toolDeplace;	
		} else if (e.getActionCommand()=="zoom") {
			selectedIndex=toolZoom;	
		} else if (e.getActionCommand()=="pipette") {
			selectedIndex=toolPipette;			
		} else if (e.getActionCommand()=="trace") {
			selectedIndex=toolTrace;	
			DXF_Loader._mc.quadCount=0;
		} else if (e.getActionCommand()=="txt") {
			selectedIndex=toolTxt;		
			DXF_Loader._mc.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		} else if (e.getActionCommand()=="solid") {
			selectedIndex=toolSolid;	
			DXF_Loader._mc.quadCount=0;
		} 

		reset(s);		
		
		lastTool.setEnabled(true);
		lastTool=(JButton) e.getSource();
		lastTool.setEnabled(false);		
	}
	
	public int getSelectedIndex() {
		return selectedIndex;
	}
	
	

}
