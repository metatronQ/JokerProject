package com.example.jokerproject.util;

import com.example.jokerproject.custom_control.MessageJoker;

public class Analyse {
    private volatile static Analyse analyse;
    private Analyse(){}
    public static Analyse getAnalyse(){
        if (analyse == null){
            synchronized(Transform.class){
                if (analyse == null){
                    analyse = new Analyse();
                }
            }
        }
        return analyse;
    }

    public MessageJoker toMesSection(String message){
        MessageJoker mes = new MessageJoker();

        String[] strings = message.split("@");
        mes.setMyPlayer(strings[0]);
        mes.setStatus(strings[1]);
        mes.setPlayer(strings[2]);
        mes.setType(strings[3]);
        mes.setNumber(strings[4]);
        return mes;
    }
}
