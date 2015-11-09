package fr.epsi.dxf.Graphics.FileDrag;

public class DnDList 
extends javax.swing.JList
implements  java.awt.dnd.DropTargetListener, 
            java.awt.dnd.DragSourceListener, 
            java.awt.dnd.DragGestureListener    
{

	private static final long serialVersionUID = 1L;
	
    private java.awt.dnd.DragSource dragSource = null;
    
    private int sourceIndex = -1;

    public DnDList() 
    {   
        super( new javax.swing.DefaultListModel() );
        initComponents();
    } 
    
    public DnDList( javax.swing.DefaultListModel model )
    {   super( model );
        initComponents();
    } 
    
    public DnDList( Object[] data )
    {   this();
        ((javax.swing.DefaultListModel)getModel()).copyInto( data );
    }  
    
    public DnDList( java.util.Vector data )
    {   this();
        ((javax.swing.DefaultListModel)getModel()).copyInto( data.toArray() );
    }  

    private void initComponents()
    {
        dragSource = new java.awt.dnd.DragSource();
        dragSource.createDefaultDragGestureRecognizer( this, java.awt.dnd.DnDConstants.ACTION_MOVE, this);
    }  
  
    public void dragGestureRecognized( java.awt.dnd.DragGestureEvent event) 
    {   
        final Object selected = getSelectedValue();
        if ( selected != null )
        {
            sourceIndex = getSelectedIndex();
            java.awt.datatransfer.Transferable transfer = new TransferableObject( new TransferableObject.Fetcher()
            {  
                public Object getObject()
                {   
                    ((javax.swing.DefaultListModel)getModel()).remove( sourceIndex );
                    return selected;
                }  
            });
            dragSource.startDrag (event, java.awt.dnd.DragSource.DefaultLinkDrop, transfer, this);
        }
    }
    
    public void dragDropEnd( java.awt.dnd.DragSourceDropEvent evt ) { }
    public void dragEnter( java.awt.dnd.DragSourceDragEvent evt ) { }
    public void dragExit( java.awt.dnd.DragSourceEvent evt ) { }
    public void dragOver( java.awt.dnd.DragSourceDragEvent evt ) { }
    public void dropActionChanged( java.awt.dnd.DragSourceDragEvent evt ) { }    
    public void dragEnter( java.awt.dnd.DropTargetDragEvent evt ) { 
        evt.acceptDrag( java.awt.dnd.DnDConstants.ACTION_MOVE);
    }   
    public void dragExit( java.awt.dnd.DropTargetEvent evt ) { }    
    public void dragOver( java.awt.dnd.DropTargetDragEvent evt ) { }
    public void dropActionChanged( java.awt.dnd.DropTargetDragEvent evt ) {   
    	evt.acceptDrag( java.awt.dnd.DnDConstants.ACTION_MOVE);
    }
    
    public void drop( java.awt.dnd.DropTargetDropEvent evt ) 
    {  
    	java.awt.datatransfer.Transferable transferable = evt.getTransferable();
        
        if( transferable.isDataFlavorSupported( TransferableObject.DATA_FLAVOR ) )
        {
            evt.acceptDrop( java.awt.dnd.DnDConstants.ACTION_MOVE );
            Object obj = null;
            try
            {
                obj = transferable.getTransferData( TransferableObject.DATA_FLAVOR );
            }
            catch( Exception e ) { } 
            
            if( obj != null )
            {
                int dropIndex = locationToIndex( evt.getLocation() );
                javax.swing.DefaultListModel model = (javax.swing.DefaultListModel)getModel();

                if( dropIndex < 0 )
                    model.addElement( obj );
                
                else if( sourceIndex >= 0 && dropIndex > sourceIndex )
                    model.add( dropIndex-1, obj );
                else
                    model.add( dropIndex, obj );

            } 
            else
            {
                evt.rejectDrop();
            }  
        }          
        else evt.rejectDrop();
    } 
}
