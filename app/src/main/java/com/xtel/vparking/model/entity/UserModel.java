package com.xtel.vparking.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vivhp on 11/17/2016.
 */

public class UserModel {

    @SerializedName("error")
    private String error;
    @SerializedName("userInfo")
    private UserInfo userInfo;

    public class UserInfo {
        @SerializedName("first_name")
        private String first_name;
        @SerializedName("last_name")
        private String last_name;
        @SerializedName("gender")
        private int gender;
        @SerializedName("birth_day")
        private long birth_day;
        @SerializedName("phone")
        private String phone;
        @SerializedName("address")
        private String address;
        @SerializedName("avatar")
        private String avatar;
        @SerializedName("email")
        private String email;

        public String getFirst_name() {
            return first_name;
        }

        public String getLast_name() {
            return last_name;
        }

        public int getGender() {
            return gender;
        }

        public long getBirth_day() {
            return birth_day;
        }

        public String getPhone() {
            return phone;
        }

        public String getAddress() {
            return address;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getEmail() {
            return email;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public void setBirth_day(long birth_day) {
            this.birth_day = birth_day;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public String getError() {
        return error;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
