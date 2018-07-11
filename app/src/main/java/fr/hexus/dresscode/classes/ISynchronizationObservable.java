package fr.hexus.dresscode.classes;

public interface ISynchronizationObservable
{
    void addObserver(ISynchronizationObserver o);
    void removeObserver(ISynchronizationObserver o);
    void notifyObservers(boolean isSyncOver, boolean lastTaskSucceeded) throws Exception;
}
