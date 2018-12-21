package com.parking.bardh.parkingfinder.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Zone {
    private int zoneID;
    private String name;

    public Zone(int id, String name) {
        zoneID = id;
        this.name = name;
    }

    public int getZoneID() {
        return zoneID;
    }


    public String getName() {
        return name;
    }

    public static List<Zone> zoneFromJson(JSONArray jsonArray) {

        try {
            List<Zone> listZone = new ArrayList<Zone>();
            for(int i=0; i<jsonArray.length();i++){
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                int zoneID = jsonObject.getInt("id_zone");
                String zoneName = jsonObject.getString("zone_name");
                Zone z =  new Zone(zoneID,zoneName);
                listZone.add(z);
            }
            return listZone;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Zone zone = (Zone) o;

        return zoneID == zone.zoneID;
    }

    @Override
    public int hashCode() {
        return zoneID;
    }
}
