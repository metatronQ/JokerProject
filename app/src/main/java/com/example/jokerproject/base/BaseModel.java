package com.example.jokerproject.base;


import androidx.annotation.Nullable;

public class BaseModel<T>{

    @Nullable
    private T mCallback;

    public BaseModel(){
        this(null);
    }

    public BaseModel(T callback){
        this.mCallback = callback;
    }

    public void detachView(){

    }
}
