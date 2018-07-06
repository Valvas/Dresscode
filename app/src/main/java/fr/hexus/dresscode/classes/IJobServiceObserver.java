package fr.hexus.dresscode.classes;

public interface IJobServiceObserver
{
    void jobDone(boolean rescheduleJob) throws Exception;
}
