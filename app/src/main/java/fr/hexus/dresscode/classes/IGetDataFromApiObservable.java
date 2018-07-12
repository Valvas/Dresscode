package fr.hexus.dresscode.classes;

public interface IGetDataFromApiObservable
{
    void addObserver(IGetDataFromApiObserver o);
    void removeObserver(IGetDataFromApiObserver o);
    void notifyObservers(boolean succeeded) throws Exception;
}
