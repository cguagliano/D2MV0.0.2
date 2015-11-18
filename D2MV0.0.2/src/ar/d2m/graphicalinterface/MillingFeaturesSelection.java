/*---------------------------------------------------------------------------------------- 
Copyright 2015, Celeste Gabriela Guagliano. 

This file is part of DXF2Machine project. 

DXF2Machine is free software: you can redistribute it and/or modify it under the terms of 
the GNU General Public License as published by the Free Software Foundation, either 
version 2 of the License. 

DXF2Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
See the GNU General Public License for more details. 

You should have received a copy of the GNU General Public License along with DXF2Machine. 
If not, see <http://www.gnu.org/licenses/>.

For more information, contact us at: dxf2machine@gmail.com
  ----------------------------------------------------------------------------------------*/

package ar.d2m.graphicalinterface;

/**
 * @author CGG
 *
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.epsi.dxf.DXF_Loader;
import fr.epsi.dxf.Graphics.DXF_Color;
import fr.epsi.dxf.Graphics.myCanvas;
import fr.epsi.dxf.Graphics.myToolBar;
import ar.d2m.loader.*;
//import fr.epsi.dxf.DXF_Loader;
//import fr.epsi.dxf.Graphics.*;


public class MillingFeaturesSelection extends JPanel implements ActionListener{
	
		//public int selectedIndex = 0;

	   
		public static final int changeColor = 100;
		public static final int toolNone = 0;

		private static final long serialVersionUID = 1L;
		public static JButton col;
		public static JButton colLayer;
		public static Hashtable colores=new Hashtable();
	    public static JLabel color= new JLabel();
	    public static JButton rojo=new JButton();
	    public static JButton verde=new JButton();
	    public static JButton cian=new JButton();
	    public static JButton azul=new JButton();
	    public static JButton nada=new JButton();
	    public int indice=0;
		public MillingFeaturesSelection(DXF_Loader dXF) {
			colores.put(1,"Origen");
			colores.put(3, "Taladrado");
			colores.put(4, "Grabado");
			colores.put(5, "Contorno");
			colores.put(0,"Ningun Rasgo");
			this.setLayout(new BorderLayout());
			this.setPreferredSize(new Dimension(200, 20));
			JPanel p = new JPanel(new GridLayout(0, 10));
			color.setText((String)colores.get(0));
			rojo.setCursor(Cursor
					.getPredefinedCursor(Cursor.HAND_CURSOR));
			rojo.setPreferredSize(new Dimension(30,15));
			rojo.addActionListener(this);
			rojo.setActionCommand("changeColor");
			rojo.setBackground(new Color (0xff, 0x00, 0x00));
			rojo.setToolTipText((String) colores.get(1));
			p.add(rojo);
			rojo.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == rojo) {
						color.setText((String) colores.get(1));
						D2MLoader.DXF._jcc.col.setBackground(new Color (0xff,0x00,0x00));
						D2MLoader.DXF._typeOutil.selectedIndex=6;
				}}}
			);
			verde.setCursor(Cursor
					.getPredefinedCursor(Cursor.HAND_CURSOR));
			verde.setPreferredSize(new Dimension(8, 8));
			verde.addActionListener(this);
			verde.setActionCommand("changeColor");
			verde.setBackground(new Color (0x00,0xff,0x00));
			verde.setToolTipText((String) colores.get(3));
			p.add(verde);
			verde.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == verde) {
						color.setText((String) colores.get(3));
						D2MLoader.DXF._jcc.col.setBackground(new Color (0x00,0xff,0x00));
						//D2MLoader.selected=D2MLoader.DXF._typeOutil.toolSel;
						D2MLoader.DXF._typeOutil.selectedIndex=6;
							
				}}}
			);
			cian.setCursor(Cursor
					.getPredefinedCursor(Cursor.HAND_CURSOR));
			cian.setPreferredSize(new Dimension(8, 8));
			cian.addActionListener(this);
			cian.setActionCommand("changeColor");
			cian.setBackground(new Color (0x00, 0xff, 0xff));
			cian.setToolTipText((String) colores.get(4));
			p.add(cian);
			cian.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == cian) {
						color.setText((String) colores.get(4));
						D2MLoader.DXF._jcc.col.setBackground(new Color (0x00, 0xff, 0xff));
						D2MLoader.DXF._typeOutil.selectedIndex=6;
				}}}
			);
			azul.setCursor(Cursor
					.getPredefinedCursor(Cursor.HAND_CURSOR));
			azul.setPreferredSize(new Dimension(8, 8));
			azul.addActionListener(this);
			azul.setActionCommand("changeColor");
			azul.setBackground(new Color (0x00, 0x00, 0xff));
			azul.setToolTipText((String) colores.get(5));
			p.add(azul);
			azul.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == azul) {
						color.setText((String) colores.get(5));
						D2MLoader.DXF._jcc.col.setBackground(new Color (0x00, 0x00, 0xff));
						D2MLoader.DXF._typeOutil.selectedIndex=6;
				}}}
			);
			
			nada.setCursor(Cursor
					.getPredefinedCursor(Cursor.HAND_CURSOR));
			nada.setPreferredSize(new Dimension(8, 8));
			nada.addActionListener(this);
			nada.setActionCommand("nada");
			nada.setBackground(new Color (0xff, 0xff, 0xff));
			nada.setToolTipText((String) colores.get(0));
			p.add(nada);
			nada.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == nada) {
						color.setText((String) colores.get(0));
						D2MLoader.DXF._jcc.col.setBackground(new Color (0xff, 0xff, 0xff));
						D2MLoader.DXF._typeOutil.selectedIndex=6;
				}}}
			);
			
	
			this.add(p, BorderLayout.NORTH);
			this.setPreferredSize(new Dimension(200, 20));

			this.setMinimumSize(new Dimension(200, 20));

			JPanel p_current = new JPanel();

	
			p_current.add(color);
			col = new JButton("");
			col.setMinimumSize(new Dimension(50, 20));
			col.setPreferredSize(new Dimension(50, 20));
			if (dXF._mc == null || myCanvas._dxf == null
					|| myCanvas._dxf._u == null
					|| myCanvas._dxf._u.currLayer == null)
				col.setBackground(DXF_Color.getDefaultColor());
			else
				col.setBackground(DXF_Color
						.getColor(myCanvas._dxf._u.currLayer._color));

			col.setEnabled(false);
			col.setToolTipText(DXF_Loader.res.getString("myJColorChooser.4"));

			p_current.add(col);
			this.add(p_current, BorderLayout.EAST);

		}
/*

		public Color getColor() {
			return col.getBackground();
		}
*/
		
		public void actionPerformed(ActionEvent a) {
			col.setBackground(((JButton) a.getSource()).getBackground());
			
			setCursor(Cursor.getDefaultCursor());
			D2MLoader.DXF._mc.setCursor(Cursor
					.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			//	D2MLoader.DXF._typeOutil.actionPerformed(a);
			boolean s = true;

			

				if (a.getActionCommand() == "nada") {
					D2MLoader.DXF._mc.setCursor(Cursor
							.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
					D2MLoader.DXF._typeOutil.actionPerformed(a);
					s = true;
				}
					if (a.getActionCommand() == "changeColor") {
					D2MLoader.DXF._mc.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));
					D2MLoader.DXF._typeOutil.actionPerformed(a);
					s = false;
				}

			
		
			reset(s);
			
		}
		
		public void reset(boolean s) {
			if (s) {
				for (int i = 0; i < D2MLoader.DXF._mc.vectClickOn.size(); i++)
					DXF_Loader._mc.vectClickOn.elementAt(i).setSelected(false);
				DXF_Loader._mc.vectClickOn.removeAllElements();
			}

			
		}
		

		public int getSelectedIndex() {
			return D2MLoader.DXF._typeOutil.selectedIndex;
		}

	}
