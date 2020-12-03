package com.example.jokerproject.custom_control;

public class MessageJoker {
    String player;
    String type;
    String number;
    String status;
    String myPlayer;
    boolean exist = true;

    public void setMyPlayer(String myPlayer) {
        this.myPlayer = myPlayer;
    }

    public String getMyPlayer() {
        return myPlayer;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getNumber() {
        return number;
    }

    public String getPlayer() {
        return player;
    }

    public String getType() {
        return type;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }
}
