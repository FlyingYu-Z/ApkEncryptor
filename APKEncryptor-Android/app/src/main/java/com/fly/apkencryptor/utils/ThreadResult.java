package com.fly.apkencryptor.utils;

public class ThreadResult {


    long Allstart;
    long Allend;

    public ThreadResult(){
        Allstart = System.currentTimeMillis();

    }

    public long getUsedTime(){
        Allend = System.currentTimeMillis();
        long total = (Allend - Allstart) / 1000;
        return total;
    }

    boolean isHasError;

    public void setHasError(boolean value){
        this.isHasError=value;
    }

    public boolean getHasError(){
        return this.isHasError;
    }

    Exception error;

    public void setError(Exception value){
        this.error=value;
    }

    public Exception getError(){
        return this.error;
    }

}
