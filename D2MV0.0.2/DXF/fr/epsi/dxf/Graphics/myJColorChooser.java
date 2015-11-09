package fr.epsi.dxf.Graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.epsi.dxf.DXF_Loader;


public class myJColorChooser extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	public static JButton col;
	public static JButton colLayer;
	
	public myJColorChooser() {
		JButton tmpJButton =null;
		this.setLayout(new BorderLayout());		
		this.setPreferredSize(new Dimension(200,100));		
		JPanel p = new JPanel(new GridLayout(0,10));		
		
		for (int i=0; i<fr.epsi.dxf.Graphics.DXF_Color.ColorMap.length-1;i++){
			tmpJButton = new JButton();
			tmpJButton.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			tmpJButton.setPreferredSize(new Dimension(8,8));
			tmpJButton.addActionListener(this);
			tmpJButton.setBackground(DXF_Color.ColorMap[i]);
			tmpJButton.setToolTipText(String.valueOf(i));
			p.add(tmpJButton);
		}
		this.add(p, BorderLayout.NORTH);
		this.setPreferredSize(new Dimension(200,200));
		
		this.setMinimumSize(new Dimension(200,200));
		
		JPanel p_current = new JPanel();
		
		
		p_current.add(new JLabel(DXF_Loader.res.getString("myJColorChooser.0")));
		
		colLayer = new JButton();
		colLayer.setMinimumSize(new Dimension(50,9));
		colLayer.setPreferredSize(new Dimension(50,9));
		colLayer.addMouseListener(new MouseAdapter(){
			public void mouseReleased(final MouseEvent e) {
				col.setBackground(colLayer.getBackground());
			}
		});
		colLayer.setBackground(DXF_Color.getDefaultColor());
		colLayer.setEnabled(true);
		colLayer.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		colLayer.setToolTipText("0");
		
		p_current.add(colLayer);		

		p_current.add(new JLabel(DXF_Loader.res.getString("myJColorChooser.2")));
		col = new JButton("");
		col.setMinimumSize(new Dimension(50,9));
		col.setPreferredSize(new Dimension(50,9));
		if(DXF_Loader._mc == null || DXF_Loader._mc._dxf == null || DXF_Loader._mc._dxf._u == null || DXF_Loader._mc._dxf._u.currLayer == null)
			col.setBackground(DXF_Color.getDefaultColor());
		else
			col.setBackground(DXF_Color.getColor(DXF_Loader._mc._dxf._u.currLayer._color));
		
		col.setEnabled(false);
		col.setToolTipText(DXF_Loader.res.getString("myJColorChooser.4"));
		
		p_current.add(col);
		this.add(p_current, BorderLayout.EAST);
		
		
	}
	
	public Color getColor() {
		return col.getBackground();
	}
	
	public void actionPerformed(ActionEvent a) {
		col.setBackground(((JButton) a.getSource()).getBackground());
		col.setToolTipText(((JButton) a.getSource()).getToolTipText());
	}
}
