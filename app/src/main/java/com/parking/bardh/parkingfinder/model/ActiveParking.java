package com.parking.bardh.parkingfinder.model;

import com.parking.bardh.parkingfinder.App.AppConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActiveParking {

    private int activeParkingID;
    private int id_user;
    private Parking parking;
    private String carID;
    private String dateIN;
    private String dateOUT;
    private String confirmationCode;
    private int minutes_left;


    public ActiveParking(int activeParkingID, int id_user, Parking parking, String carID, String dateIN, String dateOUT, String confirmationCode, int minutes_left) {
        this.activeParkingID = activeParkingID;
        this.id_user = id_user;
        this.parking = parking;
        this.carID = carID;
        this.dateIN = dateIN;
        this.dateOUT = dateOUT;
        this.confirmationCode = confirmationCode;
        this.minutes_left = minutes_left;
    }

    public int getActiveParkingID() {
        return activeParkingID;
    }

    public int getId_user() {
        return id_user;
    }

    public Parking getParking(){
        return parking;
    }

    public String getCarID() {
        return carID;
    }

    public String getDateIN() {
        return dateIN;
    }

    public String getDateOUT() {
        return dateOUT;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public int getMinutes_left() {
        return minutes_left;
    }

    public static List<ActiveParking> parkingFromJson(JSONArray jsonArray) {

        try {
            List<ActiveParking> listActiveParking = new ArrayList<ActiveParking>();
            for(int i=0; i<jsonArray.length();i++){
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                int activeParking_id = jsonObject.getInt("parking_active_id");
                int activeParking_user = jsonObject.getInt("user_id");
                int activeParking_parking = jsonObject.getInt("parking_id");
                Parking p = AppConfig.mapParkingList.get(activeParking_parking);
                String activeParking_carID = jsonObject.getString("parking_active_carID");
                String activeParking_dateIN = jsonObject.getString("parking_active_dateIn");
                String activeParking_dateOUT = jsonObject.getString("parking_active_dateOut");
                String activeParking_confirmCode = jsonObject.getString("parking_active_confirmationCode");
                int activeParking_minutesLeft = jsonObject.getInt("minutes_left");
                ActiveParking ap =  new ActiveParking(activeParking_id,activeParking_user,p,activeParking_carID,activeParking_dateIN,activeParking_dateOUT,activeParking_confirmCode,activeParking_minutesLeft);
                listActiveParking.add(ap);
            }
            return listActiveParking;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
