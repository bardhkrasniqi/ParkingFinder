package com.parking.bardh.parkingfinder;

import android.content.Context;
import android.content.SharedPreferences;

import com.parking.bardh.parkingfinder.model.User;

public class UserLocalStore {

    public static final String SP_NAME = "userDetails";
    SharedPreferences userLocalDatbase;

    public UserLocalStore(Context context) {
        userLocalDatbase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(User u) {
        SharedPreferences.Editor spEditor = userLocalDatbase.edit();
        if(u != null) {
            spEditor.putInt("userId", u.getUserId());
            spEditor.putInt("userRole", u.getUserRoleId());
            spEditor.putString("userFullanme", u.getFullname());
            spEditor.putString("userEmail", u.getEmail());
            spEditor.putString("userCar", u.getCarIdentification());
            spEditor.commit();
        }else{
            spEditor.clear();
        }
    }

    public User getUserLoggedIn() {
        if (isLoggedIn()) {
            int uId = userLocalDatbase.getInt("userId", -1);
            int uRoleId = userLocalDatbase.getInt("userRole", -1);
            String uFname = userLocalDatbase.getString("userFullanme","");
            String uEmail = userLocalDatbase.getString("userEmail","");
            String uCar = userLocalDatbase.getString("userCar","");
            User storedUser = new User(uId,uFname,uCar,uEmail,uRoleId);
            return storedUser;
        } else {
            return null;
        }
    }

    public void setUserLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor spEditor = userLocalDatbase.edit();
        spEditor.putBoolean("loggedIn", loggedIn);
        spEditor.commit();
    }

    public boolean isLoggedIn() {
        return userLocalDatbase.getBoolean("loggedIn", false);
    }

    public void clearUserData() {
        SharedPreferences.Editor spEditor = userLocalDatbase.edit();
        spEditor.clear();
        spEditor.commit();
    }
}
