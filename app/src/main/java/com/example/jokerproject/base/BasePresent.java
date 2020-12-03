package com.example.jokerproject.base;

public abstract class BasePresent<T extends BaseModel,V extends IVIew> {
    protected V mView;
    protected T mModel;
    protected void attach(V view){
        this.mView = view;
        this.mModel = createModel();
    }

    protected void detach(){
        if (mView != null) {
            mView = null;
        }

        if (mModel != null){
            mModel.detachView();
        }
    }

    public abstract T createModel();
}
