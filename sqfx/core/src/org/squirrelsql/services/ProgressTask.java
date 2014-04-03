package org.squirrelsql.services;

public interface ProgressTask<T>
{
    T call();

    void goOn(T t);
}
