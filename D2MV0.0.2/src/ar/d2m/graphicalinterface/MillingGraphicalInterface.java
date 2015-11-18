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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import ar.d2m.features.ContourMilling;

/**
 * @author CGG
 *
 */
public class MillingGraphicalInterface extends JPanel implements ActionListener{

	 JButton boton1;
	    public MillingGraphicalInterface() {
	        setLayout(null);
	        boton1=new JButton("ObtenerTabla");
	        boton1.setBounds(300,250,100,30);
	        add(boton1);
	        boton1.addActionListener(this);
	    }
	    
	    public void actionPerformed(ActionEvent e) {
	        if (e.getSource()==boton1) {
	           new ContourMilling();	           
	        }
	    }
	    
}
