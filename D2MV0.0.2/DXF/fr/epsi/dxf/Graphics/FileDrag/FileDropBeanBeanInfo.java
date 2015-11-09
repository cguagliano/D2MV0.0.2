package fr.epsi.dxf.Graphics.FileDrag;

import java.beans.EventSetDescriptor;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class FileDropBeanBeanInfo extends SimpleBeanInfo
{
  private static PropertyDescriptor[] properties = null;
  private static EventSetDescriptor[] eventSets = null;
  private static MethodDescriptor[] methods = null;

  private static java.awt.Image iconColor16 = null; 
  private static java.awt.Image iconColor32 = null;
  private static java.awt.Image iconMono16 = null;
  private static java.awt.Image iconMono32 = null;
  private static String iconNameC16 = null;
  private static String iconNameC32 = null;
  private static String iconNameM16 = null;
  private static String iconNameM32 = null;

  private static int defaultPropertyIndex = -1;
  //private static int defaultEventIndex = -1;

    public PropertyDescriptor[] getPropertyDescriptors() {
        return properties;
    }
    
    public EventSetDescriptor[] getEventSetDescriptors() {
        return eventSets;
    }
    
    public MethodDescriptor[] getMethodDescriptors() {
        return methods;
    }
    
    public int getDefaultPropertyIndex() {
        return defaultPropertyIndex;
    }

    public int getDefaultEventIndex() {
        return defaultPropertyIndex;
    }

    public java.awt.Image getIcon(int iconKind) {
        switch ( iconKind ) {
        case ICON_COLOR_16x16:
            if ( iconNameC16 == null )
                return null;
            else {
                if( iconColor16 == null )
                    iconColor16 = loadImage( iconNameC16 );
                return iconColor16;
            }
        case ICON_COLOR_32x32:
            if ( iconNameC32 == null )
                return null;
            else {
                if( iconColor32 == null )
                    iconColor32 = loadImage( iconNameC32 );
                return iconColor32;
            }
        case ICON_MONO_16x16:
            if ( iconNameM16 == null )
                return null;
            else {
                if( iconMono16 == null )
                    iconMono16 = loadImage( iconNameM16 );
                return iconMono16;
            }
        case ICON_MONO_32x32:
            if ( iconNameM32 == null )
                return null;
            else {
                if( iconNameM32 == null )
                    iconMono32 = loadImage( iconNameM32 );
                return iconMono32;
            }
        }
        return null;
    }
}

