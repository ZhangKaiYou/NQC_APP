package com.example.nqc_app.NFCFunction.util;

import android.os.Parcelable;
import android.os.Parcel;

/**
 * Created by aa2646 on 2016/8/5.
 */
public class StudentInfo implements Parcelable {
    private String StudentID;
    private String StudentName;
    private String Gender;
    private String Email;
    private String Department;
    private String Status;

    public static final Parcelable.Creator<StudentInfo> CREATOR = new Parcelable.Creator<StudentInfo>(){

        @Override
        public StudentInfo createFromParcel(Parcel source) {
            return new StudentInfo(source.readString(),source.readString(),source.readString(),source.readString(),source.readString(),source.readString());
        }

        @Override
        public StudentInfo[] newArray(int size) {
            return new StudentInfo[size];
        }
    };


    public StudentInfo(String StudentID, String StudentName,String Gender,String Email,String Department,String Status) {
        this.StudentID = StudentID;
        this.StudentName = StudentName;
        this.Gender = Gender;
        this.Email = Email;
        this.Department = Department;
        this.Status = Status;
    }


    public String getStudentID() {
        return StudentID;
    }

    public String getStudentName() {
        return StudentName;
    }

    public String getGender(){
        return Gender;
    }

    public String getEmail(){
        return Email;
    }

    public String getDepartment(){
        return Department;
    }

    public String getStatus(){
        return Status;
    }
    @Override
    public String toString(){
        return  "StudentID: " + StudentID + " StudentName: " + StudentName + "Gender:" + Gender +  "Email: " + Email + "Department: " + Department + "Status: " + Status;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest,int flags){
        dest.writeString(StudentID);
        dest.writeString(StudentName);
        dest.writeString(Gender);
        dest.writeString(Email);
        dest.writeString(Department);
        dest.writeString(Status);
    }
}
