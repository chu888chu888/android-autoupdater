package com.github.snowdream.android.api.autoupdate;



public interface IUpdateCheckListener {
    
    /**
     * OnStart <BR>
     * 
     * You can show ui here.
     */
    public abstract void OnStart();
    
    /**
     * OnFinish <BR>
     * 
     * You can show ui here.
     */
    public abstract void OnFinish();
    
    /**
     * OnSuccess <BR>
     * 
     * @param isUpdate  whether to update
     */
    public abstract void OnSuccess(Boolean isUpdate);    
    
    /**
     * OnSuccess <BR>
     * 
     * @param versionInfo  The new package version info
     */
    public abstract void OnSuccess(VersionInfo versionInfo);    
    
    /**
     * OnFailure <BR>
     * 
     * @param e exception while check update info
     */
    public abstract void OnFailure(UpdateException e);        
}
