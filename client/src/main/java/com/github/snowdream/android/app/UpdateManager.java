package com.github.snowdream.android.app;

import android.content.Context;

import com.github.snowdream.android.util.Log;

/**
 * Created by snowdream on 12/30/13.
 */
public class UpdateManager {
    private Context context = null;
    private UpdateListener listener = null;
    private UpdateManager() {
    }

    public UpdateManager(Context context) {
        super();
        this.context = context;
    }

    public void check(Context context,UpdateOptions options){
        check(context, options,updateListener);
    }

    public void check(Context context,UpdateOptions options,UpdateListener listener){
        if (this.context == null && context == null){
            Log.w("The Context is NUll!");
            return;
        }

        if (options == null){
            Log.w("The UpdateOptions is NUll!");
            return;
        }

        if (listener == null){
           this.listener = updateListener;
        }

        if (options.shouldCheckUpdate()){
            //TODO
        }else{
            //TODO
        }
    }

    private UpdateListener updateListener = new UpdateListener(){

    };
}
