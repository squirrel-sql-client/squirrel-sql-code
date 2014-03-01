/*
 * Copyright (C) 2012 Stefan Willinger
 * wis775@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.squirrel_sql.plugins.swingviolations;

import java.applet.Applet;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Window;
import java.lang.ref.WeakReference;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * @author Stefan Willinger
 *
 */
public class EDTViolationRepaintManager extends RepaintManager{
	private RepaintManager delegate;
	private boolean completeCheck = true;
	private WeakReference<JComponent> lastComponent;
	
	/** Logger for this class. */
	private static ILogger log = LoggerController.createLogger(EDTViolationRepaintManager.class);

	/** Application API. */
	private IApplication app;

	public EDTViolationRepaintManager(boolean completeCheck, IApplication app) {
		this.completeCheck = completeCheck;
		delegate = RepaintManager.currentManager(null);
		
		this.app = app;
	}
	
	/**
	 * @return
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return delegate.hashCode();
	}

	/**
	 * @param obj
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return delegate.equals(obj);
	}

	/**
	 * @param invalidComponent
	 * @see javax.swing.RepaintManager#addInvalidComponent(javax.swing.JComponent)
	 */
	public synchronized void addInvalidComponent(JComponent invalidComponent) {
		checkThreadViolations(invalidComponent);
		delegate.addInvalidComponent(invalidComponent);
	}

	/**
	 * @param component
	 * @see javax.swing.RepaintManager#removeInvalidComponent(javax.swing.JComponent)
	 */
	public void removeInvalidComponent(JComponent component) {
		delegate.removeInvalidComponent(component);
	}

	/**
	 * @param c
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @see javax.swing.RepaintManager#addDirtyRegion(javax.swing.JComponent, int, int, int, int)
	 */
	public void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
		checkThreadViolations(c);
		delegate.addDirtyRegion(c, x, y, w, h);
	}

	/**
	 * @param window
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @see javax.swing.RepaintManager#addDirtyRegion(java.awt.Window, int, int, int, int)
	 */
	public void addDirtyRegion(Window window, int x, int y, int w, int h) {
		delegate.addDirtyRegion(window, x, y, w, h);
	}

	/**
	 * @param applet
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @see javax.swing.RepaintManager#addDirtyRegion(java.applet.Applet, int, int, int, int)
	 */
	public void addDirtyRegion(Applet applet, int x, int y, int w, int h) {
		delegate.addDirtyRegion(applet, x, y, w, h);
	}

	/**
	 * @param aComponent
	 * @return
	 * @see javax.swing.RepaintManager#getDirtyRegion(javax.swing.JComponent)
	 */
	public Rectangle getDirtyRegion(JComponent aComponent) {
		return delegate.getDirtyRegion(aComponent);
	}

	/**
	 * @param aComponent
	 * @see javax.swing.RepaintManager#markCompletelyDirty(javax.swing.JComponent)
	 */
	public void markCompletelyDirty(JComponent aComponent) {
		delegate.markCompletelyDirty(aComponent);
	}

	/**
	 * @param aComponent
	 * @see javax.swing.RepaintManager#markCompletelyClean(javax.swing.JComponent)
	 */
	public void markCompletelyClean(JComponent aComponent) {
		delegate.markCompletelyClean(aComponent);
	}

	/**
	 * @param aComponent
	 * @return
	 * @see javax.swing.RepaintManager#isCompletelyDirty(javax.swing.JComponent)
	 */
	public boolean isCompletelyDirty(JComponent aComponent) {
		return delegate.isCompletelyDirty(aComponent);
	}

	/**
	 * 
	 * @see javax.swing.RepaintManager#validateInvalidComponents()
	 */
	public void validateInvalidComponents() {
		delegate.validateInvalidComponents();
	}

	/**
	 * 
	 * @see javax.swing.RepaintManager#paintDirtyRegions()
	 */
	public void paintDirtyRegions() {
		delegate.paintDirtyRegions();
	}

	/**
	 * @return
	 * @see javax.swing.RepaintManager#toString()
	 */
	public String toString() {
		return delegate.toString();
	}

	/**
	 * @param c
	 * @param proposedWidth
	 * @param proposedHeight
	 * @return
	 * @see javax.swing.RepaintManager#getOffscreenBuffer(java.awt.Component, int, int)
	 */
	public Image getOffscreenBuffer(Component c, int proposedWidth, int proposedHeight) {
		return delegate.getOffscreenBuffer(c, proposedWidth, proposedHeight);
	}

	/**
	 * @param c
	 * @param proposedWidth
	 * @param proposedHeight
	 * @return
	 * @see javax.swing.RepaintManager#getVolatileOffscreenBuffer(java.awt.Component, int, int)
	 */
	public Image getVolatileOffscreenBuffer(Component c, int proposedWidth, int proposedHeight) {
		return delegate.getVolatileOffscreenBuffer(c, proposedWidth, proposedHeight);
	}

	/**
	 * @param d
	 * @see javax.swing.RepaintManager#setDoubleBufferMaximumSize(java.awt.Dimension)
	 */
	public void setDoubleBufferMaximumSize(Dimension d) {
		delegate.setDoubleBufferMaximumSize(d);
	}

	/**
	 * @return
	 * @see javax.swing.RepaintManager#getDoubleBufferMaximumSize()
	 */
	public Dimension getDoubleBufferMaximumSize() {
		return delegate.getDoubleBufferMaximumSize();
	}

	/**
	 * @param aFlag
	 * @see javax.swing.RepaintManager#setDoubleBufferingEnabled(boolean)
	 */
	public void setDoubleBufferingEnabled(boolean aFlag) {
		delegate.setDoubleBufferingEnabled(aFlag);
	}

	/**
	 * @return
	 * @see javax.swing.RepaintManager#isDoubleBufferingEnabled()
	 */
	public boolean isDoubleBufferingEnabled() {
		return delegate.isDoubleBufferingEnabled();
	}
	
	 private void checkThreadViolations(JComponent c) {
	        if (!SwingUtilities.isEventDispatchThread() && (completeCheck || c.isShowing())) {
	            boolean repaint = false;
	            boolean fromSwing = false;
	            boolean imageUpdate = false;
	            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
	            for (StackTraceElement st : stackTrace) {
	                if (repaint && st.getClassName().startsWith("javax.swing.") &&
	                        // for details see 
	                        // https://swinghelper.dev.java.net/issues/show_bug.cgi?id=1
	                         !st.getClassName().startsWith("javax.swing.SwingWorker")) {
	                    fromSwing = true;
	                }
	                if (repaint && "imageUpdate".equals(st.getMethodName())) {
	                    imageUpdate = true;
	                }
	                if ("repaint".equals(st.getMethodName())) {
	                    repaint = true;
	                    fromSwing = false;
	                }
	            }
	            if (imageUpdate) {
	                //assuming it is java.awt.image.ImageObserver.imageUpdate(...) 
	                //image was asynchronously updated, that's ok 
	                return;
	            }
	            if (repaint && !fromSwing) {
	                //no problems here, since repaint() is thread safe
	                return;
	            }
	            //ignore the last processed component
	            if (lastComponent != null && c == lastComponent.get()) {
	                return;
	            }
	            lastComponent = new WeakReference<JComponent>(c);
	            violationFound(c, stackTrace);
	        }
	    }

	    protected void violationFound(JComponent c, StackTraceElement[] stackTrace) {
          String message = "EDT violation detected in " + c;
          log.error(message, new RuntimeException(message));
	       app.getMessageHandler().showErrorMessage("EDT violation detected!");
	    }
	
}
