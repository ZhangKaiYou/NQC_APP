package com.example.nqc_app.NFCFunction.util;

/**
 * Created by aa2646 on 2016/11/2.
 */
public class ClassRoomTagConverter {
    public static String toString(ClassRoomTag classRoomTag){
        return classRoomTag.getClassRoomInfo();
    }

    public static ClassRoomTag formString(String content){
        String [] split = content.split(";");
        if(split.length == 3){
            return new ClassRoomTag(split[0]);
        }else {
            throw new IllegalArgumentException("Cannot pares given content");
        }
    }
}
