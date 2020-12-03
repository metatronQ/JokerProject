package com.example.jokerproject.util;

import com.example.jokerproject.R;
import com.example.jokerproject.custom_control.MessageJoker;

public class Transform {
    private volatile static Transform transform;

    private Transform(){}

    public static Transform getTransform(){
        if (transform == null){
            synchronized(Transform.class){
                if (transform == null){
                    transform = new Transform();
                }
            }
        }
        return transform;
    }

    public int toNumber(MessageJoker s){
        switch (s.getNumber()){
            case "01":
                return 11;
            case "02":
                return 2;
            case "03":
                return 3;
            case "04":
                return 4;
            case "05":
                return 5;
            case "06":
                return 6;
            case "07":
                return 7;
            case "08":
                return 8;
            case "09":
                return 9;
            default:
                return 10;
        }
    }

    public int toUri(MessageJoker s){
        switch (s.getType()){
            //spades
            case "0":
                switch (s.getNumber()){
                    case "01":
                        return R.drawable.spadea;
                    case "02":
                        return R.drawable.spade2;
                    case "03":
                        return R.drawable.spade3;
                    case "04":
                        return R.drawable.spade4;
                    case "05":
                        return R.drawable.spade5;
                    case "06":
                        return R.drawable.spade6;
                    case "07":
                        return R.drawable.spade7;
                    case "08":
                        return R.drawable.spade8;
                    case "09":
                        return R.drawable.spade9;
                    case "10":
                        return R.drawable.spade10;
                    case "11":
                        return R.drawable.spadej;
                    case "12":
                        return R.drawable.spadeq;
                    case "13":
                        return R.drawable.spadek;
                }

            //heard
            case "1":
                switch (s.getNumber()){
                    case "01":
                        return R.drawable.hearta;
                    case "02":
                        return R.drawable.heart2;
                    case "03":
                        return R.drawable.heart3;
                    case "04":
                        return R.drawable.heart4;
                    case "05":
                        return R.drawable.heart5;
                    case "06":
                        return R.drawable.heart6;
                    case "07":
                        return R.drawable.heart7;
                    case "08":
                        return R.drawable.heart8;
                    case "09":
                        return R.drawable.heart9;
                    case "10":
                        return R.drawable.heart10;
                    case "11":
                        return R.drawable.heartj;
                    case "12":
                        return R.drawable.heartq;
                    case "13":
                        return R.drawable.heartk;
                }

            //方块
            case "2":
                switch (s.getNumber()){
                    case "01":
                        return R.drawable.diamonda;
                    case "02":
                        return R.drawable.diamond2;
                    case "03":
                        return R.drawable.diamond3;
                    case "04":
                        return R.drawable.diamond4;
                    case "05":
                        return R.drawable.diamond5;
                    case "06":
                        return R.drawable.diamond6;
                    case "07":
                        return R.drawable.diamond7;
                    case "08":
                        return R.drawable.diamond8;
                    case "09":
                        return R.drawable.diamond9;
                    case "10":
                        return R.drawable.diamond10;
                    case "11":
                        return R.drawable.diamondj;
                    case "12":
                        return R.drawable.diamondq;
                    case "13":
                        return R.drawable.diamondk;
                }
            //club
            case "3":
                switch (s.getNumber()){
                    case "01":
                        return R.drawable.cluba;
                    case "02":
                        return R.drawable.club2;
                    case "03":
                        return R.drawable.club3;
                    case "04":
                        return R.drawable.club4;
                    case "05":
                        return R.drawable.club5;
                    case "06":
                        return R.drawable.club6;
                    case "07":
                        return R.drawable.club7;
                    case "08":
                        return R.drawable.club8;
                    case "09":
                        return R.drawable.club9;
                    case "10":
                        return R.drawable.club10;
                    case "11":
                        return R.drawable.clubj;
                    case "12":
                        return R.drawable.clubq;
                    case "13":
                        return R.drawable.clubk;
                }

        }
        return -1;
    }

    public String toPlayer(String s){
        switch (s){
            case "1":
                return "player1";
            case "2":
                return "player2";
            case "3":
                return "player3";
            case "4":
                return "player4";
            case "5":
                return "player5";
            case "6":
                return "player6";
            default:
                return "server";
        }
    }
}
