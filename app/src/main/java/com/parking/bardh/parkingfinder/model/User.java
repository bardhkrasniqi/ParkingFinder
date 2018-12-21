package com.parking.bardh.parkingfinder.model;

import org.json.JSONObject;

public class User {
    private int userId;
    private String fullname;
    private String carIdentification;
    private String email;
    private int userRoleId;


    public User(int userId, String fullname, String carIdentification, String email, int userRoleId) {
        this.fullname = fullname;
        this.userId = userId;
        this.carIdentification = carIdentification;
        this.email = email;
        this.userRoleId = userRoleId;
    }

    public static User userFromJson(JSONObject jsonObject){
        try {
            int userId = jsonObject.getInt("id_user");
            int userRoleId = jsonObject.getInt("id_user_role");
            String fullname = jsonObject.getString("user_fullname");
            String carIdentification = jsonObject.getString("user_car_identification");
            String email = jsonObject.getString("user_email");
            User u =  new User(userId,fullname,carIdentification,email,userRoleId);
            return u;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getUserId() {
        return userId;
    }


    public String getCarIdentification() {
        return carIdentification;
    }

    public void setCarIdentification(String carIdentification) {
        this.carIdentification = carIdentification;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getUserRoleId() {
        return userRoleId;
    }

    public static JSONObject registerUser(String fullname, String car ,String email, String password){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fullname", fullname);
            jsonObject.put("car", car);
            jsonObject.put("email",email);
            jsonObject.put("password", password);
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject updateUser(User u, String password){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id_user", u.getUserId());
            jsonObject.put("fullname", u.getFullname());
            jsonObject.put("car", u.getCarIdentification());
            jsonObject.put("email",u.getEmail());
            jsonObject.put("password", password);
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject;
    }

}
