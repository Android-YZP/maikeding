package com.mcwonders.mkd.login.maixinlogin;

/**
 * Created by Smith on 2016/12/20.
 */

public class MKJUserInfo {

    /**
     * userimg : http://img.maikejia.com/uface/57.jpg
     * username : zzzmfx
     * usergender : false
     * userbirthday : {"date":11,"day":5,"hours":9,"minutes":27,"month":10,"nanos":0,"seconds":45,"time":1320974865000,"timezoneOffset":-480,"year":111}
     * userphone : 13291218351
     * useremail : 851239358@qq.com
     * usersignature : 我是个性签名
     * success : true
     */

    private String userimg;
    private String username;
    private String nickname;
    private String usergender;
    /**
     * date : 11
     * day : 5
     * hours : 9
     * minutes : 27
     * month : 10
     * nanos : 0
     * seconds : 45
     * time : 1320974865000
     * timezoneOffset : -480
     * year : 111
     */

    private UserbirthdayBean userbirthday;
    private String userphone;
    private String useremail;
    private String usersignature;
    private boolean success;

    public String getUserimg() {
        return userimg;
    }

    public void setUserimg(String userimg) {
        this.userimg = userimg;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUsergender() {
        return usergender;
    }

    public void setUsergender(String usergender) {
        this.usergender = usergender;
    }

    public UserbirthdayBean getUserbirthday() {
        return userbirthday;
    }

    public void setUserbirthday(UserbirthdayBean userbirthday) {
        this.userbirthday = userbirthday;
    }

    public String getUserphone() {
        return userphone;
    }

    public void setUserphone(String userphone) {
        this.userphone = userphone;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getUsersignature() {
        return usersignature;
    }

    public void setUsersignature(String usersignature) {
        this.usersignature = usersignature;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static class UserbirthdayBean {
        private int date;
        private int day;
        private int hours;
        private int minutes;
        private int month;
        private int nanos;
        private int seconds;
        private String time;
        private int timezoneOffset;
        private int year;

        public int getDate() {
            return date;
        }

        public void setDate(int date) {
            this.date = date;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getHours() {
            return hours;
        }

        public void setHours(int hours) {
            this.hours = hours;
        }

        public int getMinutes() {
            return minutes;
        }

        public void setMinutes(int minutes) {
            this.minutes = minutes;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getNanos() {
            return nanos;
        }

        public void setNanos(int nanos) {
            this.nanos = nanos;
        }

        public int getSeconds() {
            return seconds;
        }

        public void setSeconds(int seconds) {
            this.seconds = seconds;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getTimezoneOffset() {
            return timezoneOffset;
        }

        public void setTimezoneOffset(int timezoneOffset) {
            this.timezoneOffset = timezoneOffset;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }
    }
}
