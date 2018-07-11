package fr.hexus.dresscode.classes;

public interface ISynchronizationObserver
{
    void syncDone(boolean lastTaskSucceeded);
    void syncProgress(boolean lastTaskSucceeded);
}
