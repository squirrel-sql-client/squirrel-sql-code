package org.squirrelsql.services.progress;

public interface ProgressTask<T>
{
    T call();

    void goOn(T t);
}
