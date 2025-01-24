package dev.bntz.rest.json;

public class BooleanDataContainer<T> {
    private boolean success;
    private T data;

    public BooleanDataContainer(boolean v) {
        this.success = v;
    }

    public BooleanDataContainer(boolean v, T data) {
        this.success = v;
        this.data =  data;
    }

    
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


}
