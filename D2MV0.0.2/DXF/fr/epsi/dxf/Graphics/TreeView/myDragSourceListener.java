package fr.epsi.dxf.Graphics.TreeView;

import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

class myDragSourceListener implements DragSourceListener {
	public void dragEnter(DragSourceDragEvent dsde) {
		//System.out.println("myDragSourceListener : drag Enter");
	}

	public void dragOver(DragSourceDragEvent dsde) {
		//System.out.println("myDragSourceListener : drag Over");
	}

	public void dropActionChanged(DragSourceDragEvent dsde) {
		//System.out.println("myDragSourceListener : drop Action Changed");
	}

	public void dragExit(DragSourceEvent dse) {
		//System.out.println("myDragSourceListener : drag Exit");
	}

	public void dragDropEnd(DragSourceDropEvent dsde) {
		//System.out.println("myDragSourceListener : dragDropEnd");
	}

}
