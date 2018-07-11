package fr.hexus.dresscode.classes;

public class UuidForm
{
    private String uuid;

    public UuidForm(String uuid)
    {
        this.uuid = uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    public String getUuid()
    {
        return this.uuid;
    }

    public String toString()
    {
        return "[UUID FORM]\n- UUID : " + this.uuid + "\n";
    }
}
