package fr.epsi.dxf.Graphics.FileDrag;

public class FileDropBean 
implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	private javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();
    
    public FileDropBean() { }
    
    public void addFileDropTarget( java.awt.Component comp ) {
        FileDrop.Listener listener = new FileDrop.Listener()
        {   public void filesDropped( java.io.File[] files )
            {   fireFileDropHappened( files );
            }  
        }; 
        boolean recursive = true;
        new FileDrop( comp, recursive, listener );
    }  
    
    public boolean removeFileDropTarget( java.awt.Component comp ) {
    	return FileDrop.remove( comp );
    }  
    
    public void addFileDropListener (FileDropListener listener) {
    	listenerList.add( FileDropListener.class, listener );
    }  

    public void removeFileDropListener (FileDropListener listener) {
    	listenerList.remove( FileDropListener.class, listener );
    }   
    
    protected void fireFileDropHappened( java.io.File[] files ) {
        FileDropEvent evt = new FileDropEvent( files, this );
        
        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length-2; i>=0; i-=2) 
        {   if (listeners[i]==FileDropListener.class) 
            {   ((FileDropListener)listeners[i+1]).filesDropped(evt);
            }   
        }  
    }
}
