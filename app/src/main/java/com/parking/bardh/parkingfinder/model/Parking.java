package com.parking.bardh.parkingfinder.model;

import com.parking.bardh.parkingfinder.App.AppConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Parking {
    private int parkingId;
    private String parking_name;
    private int parking_space;
    private String parking_address;
    private String parking_work_time;
    private String parking_price;
    private String parking_description;
    private String parking_latitude;
    private String parking_longitude;
    private Zone  zone;
    private int active;


    public Parking(int parkingId, String parking_name, int parking_space, String parking_address, String parking_work_time, String parking_price, String parking_description, String parking_latitude, String parking_longitude, int zoneID, int active) {
        this.parkingId = parkingId;
        this.parking_name = parking_name;
        this.parking_space = parking_space;
        this.parking_address = parking_address;
        this.parking_work_time = parking_work_time;
        this.parking_price = parking_price;
        this.parking_description = parking_description;
        this.parking_latitude = parking_latitude;
        this.parking_longitude = parking_longitude;
        this.zone = AppConfig.mapZoneList.get(zoneID);
        this.active = active;
    }


    public int getParkingId() {
        return parkingId;
    }

    public String getParking_name() {
        return parking_name;
    }

    public int getParking_space() {
        return parking_space;
    }

    public String getParking_address() {
        return parking_address;
    }

    public String getParking_work_time() {
        return parking_work_time;
    }

    public String getParking_price() {
        return parking_price;
    }

    public String getParking_description() {
        return parking_description;
    }

    public String getParking_latitude() {
        return parking_latitude;
    }

    public String getParking_longitude() {
        return parking_longitude;
    }

    public Zone getZone() {
        return zone;
    }

    public int getActive(){ return active; };

    public static List<Parking> parkingFromJson(JSONArray jsonArray) {

        try {
            List<Parking> listParking = new ArrayList<Parking>();
            for(int i=0; i<jsonArray.length();i++){
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                int parkingID = jsonObject.getInt("parking_id");
                String parking_name = jsonObject.getString("parking_name");
                int parking_space = jsonObject.getInt("parking_space");
                String parking_address = jsonObject.getString("parking_address");
                String parking_work_time = jsonObject.getString("parking_work_time");
                String parking_price = jsonObject.getString("parking_price");
                String parking_description = jsonObject.getString("parking_description");
                String parking_latitude = jsonObject.getString("parking_latitude");
                String parking_longitude = jsonObject.getString("parking_longitude");
                int parking_zone = jsonObject.getInt("parking_zone");
                int active = jsonObject.getInt("active");
                Parking p =  new Parking(parkingID,parking_name,parking_space,parking_address,parking_work_time,parking_price,parking_description,parking_latitude,parking_longitude,parking_zone,active);
                listParking.add(p);
            }
            return listParking;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public String toString(){
        return parking_name;
    }
}
