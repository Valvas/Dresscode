package fr.hexus.dresscode.classes;

public interface IJobServiceObservable
{
    void addObserver(IJobServiceObserver o);
    void removeObserver(IJobServiceObserver o);
    void notifyObservers(boolean rescheduleJob) throws Exception;
}
