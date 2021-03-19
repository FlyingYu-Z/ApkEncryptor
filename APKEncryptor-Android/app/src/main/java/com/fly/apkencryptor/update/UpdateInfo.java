package com.fly.apkencryptor.update;

public class UpdateInfo
{
    private String version;
    private boolean force;
    private String url;
    private String description;
    
    
    public String getVersion()
    {
        return version;
    }
    
    public void setVersion(String version)
    {
        this.version = version;
    }

    public boolean getForce(){
        return force;
    }
    public void setForce(boolean value){
        this.force=value;
    }

    public String getUrl()
    {
        return url;
    }
    
    public void setUrl(String url)
    {
        this.url = url;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
}

