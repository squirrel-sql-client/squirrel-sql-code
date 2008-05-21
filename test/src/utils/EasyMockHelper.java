package utils;
/*
 * Copyright (C) 2008 Rob Manning
 * manningr@users.sourceforge.net
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

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;


/**
 * A utility class designed to satisfy some of the repetitive and error-prone actions
 * required when using EasyMock.  This class allows tests to register EasyMock objects for the purpose 
 * of keeping track of them for replay and verify operations.  
 *  
 * Greatly inspired by Steve Cosenza's EasyMockHelper.  Thanks Steve!
 */
public class EasyMockHelper {
   
   List<IMocksControl> mockControls = new ArrayList<IMocksControl>(); 
   
   public <T> T createMock(Class<T> mockClass) {
      return createMock(null, mockClass);
   }

   public <T> T createMock(String name, Class<T> mockClass) {
      IMocksControl control = null;
      if (mockClass.isInterface()) {
         // an interface
         control = EasyMock.createControl();
      } else {
         // a class
         control = org.easymock.classextension.EasyMock.createControl();
      }
      
      mockControls.add(control);
      if (name != null) {
      	return control.createMock(name, mockClass);
      } else {
      	return control.createMock(mockClass);
      }
   }
   
   
   public void replayAll() {
      for (IMocksControl control : mockControls) {
         control.replay();
      }
   }   
   
   public void resetAll() {
      for (IMocksControl control : mockControls) {
         control.reset();
      }
   }
   
   public void verifyAll() {
      for (IMocksControl control : mockControls) {
         control.verify();
      }
   }
   
}
