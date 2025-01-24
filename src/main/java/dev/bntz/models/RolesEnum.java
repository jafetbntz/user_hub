package dev.bntz.models;

public enum RolesEnum {
    CLIENT(0), USER(1), ADMIN(2);

    private int value;

    private RolesEnum(int id){
        this.value = id;
    }
    
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
