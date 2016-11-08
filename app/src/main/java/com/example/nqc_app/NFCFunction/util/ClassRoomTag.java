package com.example.nqc_app.NFCFunction.util;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aa2646 on 2016/11/2.
 */

public class ClassRoomTag  implements Parcelable{
    private String ClassRoomInfo;

    public static final Parcelable.Creator<ClassRoomTag> CREATOR = new Parcelable.Creator<ClassRoomTag>(){
        @Override
        public ClassRoomTag createFromParcel(Parcel source){
            return new ClassRoomTag(source.readString());
        }

        @Override
        public ClassRoomTag[] newArray(int size){
            return new ClassRoomTag[size];
        }
    };

    public ClassRoomTag(String ClassRoomInfo) {
        this.ClassRoomInfo = ClassRoomInfo;
    }

    public String getClassRoomInfo(){
        return getClassRoomInfo();
    }

    @Override
    public String toString(){
        return  "ClassRoomInfo:" + ClassRoomInfo;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest,int flags){
        dest.writeString(ClassRoomInfo);
    }


}
