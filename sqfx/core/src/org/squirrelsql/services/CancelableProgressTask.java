package org.squirrelsql.services;

import org.squirrelsql.services.progress.ProgressTask;

public interface CancelableProgressTask<T> extends ProgressTask<T>
{
   void cancel();
}
