package org.squirrelsql.services;


import javafx.concurrent.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * An instance of this class is member of AppState. So its scope is static.
 *
 * This class exists because of the following problem:
 * It used to happen that the task executing SQLs didn't call any of its callback
 * methods. That was because in our ProgressUtil class the Service was just held as a
 * local variable. This caused the problem because in javafx.beans.property.ObjectPropertyBase.Listener
 * listeners are held in WeakRefernces and were garbage collected once and again when the
 * Service instance was local in ProgressUtil.
 */
public class RunningServicesManager
{
   private Set<Service> _runningServices = Collections.synchronizedSet(new HashSet<>());

   public void registerService(Service service)
   {
      _runningServices.add(service);
   }

   public void unRegisterService(Service service)
   {
      _runningServices.remove(service);
   }
}
