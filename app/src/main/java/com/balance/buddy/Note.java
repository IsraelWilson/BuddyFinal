package com.balance.buddy;

/**
 * Created by israe_000 on 4/22/2015.
 */
public class Note {

    String subject;
    String location;

    Note(){}

    Note(String subject, String location){
        this.subject = subject;
        this.location = location;
    }

    void setSubject(String subject){
        this.subject = subject;
    }
    void setLocation(String location){
        this.location = location;
    }

    String getSubject(){
        return this.subject;
    }

    String getLocation(){
        return this.location;
    }
}
