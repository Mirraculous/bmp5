package com.mirra.bmp4;


import java.io.Serializable;
import java.util.Calendar;
import java.util.Comparator;

public class UserEvent implements Serializable {
    String name, Comment;
    int day, month, year, id;
    boolean isFinished;

    public UserEvent(String name,
                     int day, int month, int year,
                     boolean isFinished,
                     String Comment)
    {
        this.name = name;
        this.day = day;
        this.month = month;
        this.year = year;
        this.isFinished = isFinished;
        this.Comment = Comment;
    }

    static class IdComparator implements Comparator<UserEvent>
    {
        @Override
        public int compare(UserEvent o1, UserEvent o2) {
            return o1.id - o2.id;
        }
    }

    static class DateComparator implements Comparator<UserEvent> //descending
    {
        @Override
        public int compare(UserEvent o1, UserEvent o2) {
            Calendar c1 = Calendar.getInstance();
            c1.set(o1.year, o1.month - 1, o1.day);
            Calendar c2 = Calendar.getInstance();
            c2.set(o2.year, o2.month - 1, o2.day);
            return -c1.compareTo(c2);
            //return (o1.year - o2.year) * 365 + (o1.month - o2.month) * 30 + o1.day - o2.day;
        }
    }
}


