package com.example.nqc_app.NFCFunction.util;

/**
 * Created by aa2646 on 2016/8/8.
 */
public class StudentInfoConverter {
    public static String toString(StudentInfo studentInfo){
        return studentInfo.getStudentID() + ";" + studentInfo.getStudentName()+ ";" + studentInfo.getGender()+ ";" + studentInfo.getEmail()+ ";" + studentInfo.getDepartment()+ ";" + studentInfo.getStatus();
    }

    public static StudentInfo formString(String content){
        String[] split = content.split(";");
        if(split.length == 3){
            return new StudentInfo(split[0],split[1],split[2],split[3],split[4],split[5]);
        }else {
            throw new IllegalArgumentException("Cannot parse given content ");
        }
    }
}
