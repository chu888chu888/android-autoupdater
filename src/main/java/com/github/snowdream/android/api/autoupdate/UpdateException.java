package com.github.snowdream.android.api.autoupdate;

public class UpdateException extends Exception {
    public static final int ERROR_UNKNOWN = -1;
    

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3473665604212423993L;
    
    private int type = ERROR_UNKNOWN;
    
    private String msg = "Unknown";
    
    public UpdateException() {
    }
    
    public UpdateException(int type,String msg) {
        this.type = type;
        this.msg = msg;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
