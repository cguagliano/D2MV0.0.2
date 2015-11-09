package fr.epsi.dxf.Graphics.FileDrag;

public class TransferableObject implements java.awt.datatransfer.Transferable
{
    public final static String MIME_TYPE = "application/x-net.iharder.dnd.TransferableObject";
    
    public final static java.awt.datatransfer.DataFlavor DATA_FLAVOR = 
        new java.awt.datatransfer.DataFlavor( fr.epsi.dxf.Graphics.FileDrag.TransferableObject.class, MIME_TYPE );
    
    
    private Fetcher fetcher;
   private Object data;
    
    private java.awt.datatransfer.DataFlavor customFlavor; 
    
    public TransferableObject( Object data ) {   
    	this.data = data;
        this.customFlavor = new java.awt.datatransfer.DataFlavor( data.getClass(), MIME_TYPE );
    }  
    
    public TransferableObject( Fetcher fetcher ) {   
    	this.fetcher = fetcher;
    }  
    
    public TransferableObject( Class dataClass, Fetcher fetcher ) {   
    	this.fetcher = fetcher;
        this.customFlavor = new java.awt.datatransfer.DataFlavor( dataClass, MIME_TYPE );
    }  
    
    public java.awt.datatransfer.DataFlavor getCustomDataFlavor() {   
    	return customFlavor;
    } 
    
    public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors() 
    {   
        if( customFlavor != null )
            return new java.awt.datatransfer.DataFlavor[]
            {   customFlavor,
                DATA_FLAVOR,
                java.awt.datatransfer.DataFlavor.stringFlavor
            };
        else
            return new java.awt.datatransfer.DataFlavor[]
            {   
        		DATA_FLAVOR,
                java.awt.datatransfer.DataFlavor.stringFlavor
            }; 
    }  
    
    public Object getTransferData( java.awt.datatransfer.DataFlavor flavor )
    throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException 
    {   
        if( flavor.equals( DATA_FLAVOR ) )
            return fetcher == null ? data : fetcher.getObject();

        if( flavor.equals( java.awt.datatransfer.DataFlavor.stringFlavor ) )
            return fetcher == null ? data.toString() : fetcher.getObject().toString();
            
        throw new java.awt.datatransfer.UnsupportedFlavorException(flavor);
    } 
    
    public boolean isDataFlavorSupported( java.awt.datatransfer.DataFlavor flavor ) 
    {
        if( flavor.equals( DATA_FLAVOR ) )
            return true;
        
        if( flavor.equals( java.awt.datatransfer.DataFlavor.stringFlavor ) )
            return true;
        
        return false;
    }

    public static interface Fetcher
    {
        public abstract Object getObject();
    }  
}  