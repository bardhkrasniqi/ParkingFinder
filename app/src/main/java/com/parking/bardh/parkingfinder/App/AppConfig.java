package com.parking.bardh.parkingfinder.App;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.parking.bardh.parkingfinder.R;
import com.parking.bardh.parkingfinder.model.Parking;
import com.parking.bardh.parkingfinder.model.Zone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AppConfig {

    public static String URL = "YOUR_URL";


    public static List<Zone> ZoneList = new ArrayList<Zone>();

    public static Map<Integer,Zone> mapZoneList = new HashMap<Integer, Zone>();

    public static void setZoneList(List<Zone> list){
        mapZoneList.clear();
        ZoneList.clear();
        ZoneList = list;
        Iterator<Zone> it_zone = list.iterator();
        while(it_zone.hasNext()){
            Zone z = (Zone) it_zone.next();
            mapZoneList.put(z.getZoneID(),z);
        }
    }

    public static List<Parking> ParkingList =  new ArrayList<Parking>();
    public static Map<Integer,Parking> mapParkingList = new HashMap<Integer, Parking>();

    public static void setParkingList(List<Parking> list){
        mapParkingList.clear();
        ParkingList.clear();
        ParkingList = list;
        Iterator<Parking> it_parking = list.iterator();
        while(it_parking.hasNext()){
            Parking p = (Parking) it_parking.next();
            mapParkingList.put(p.getParkingId(),p);
        }
    }

}
