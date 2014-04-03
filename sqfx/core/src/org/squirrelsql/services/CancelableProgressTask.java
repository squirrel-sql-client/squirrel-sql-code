package org.squirrelsql.services;

public interface CancelableProgressTask<T> extends ProgressTask<T>
{
   void cancel();
}
