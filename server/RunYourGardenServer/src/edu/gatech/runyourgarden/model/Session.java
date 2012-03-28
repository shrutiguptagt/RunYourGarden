package edu.gatech.runyourgarden.model;

import java.util.ArrayList;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

import edu.gatech.runyourgarden.server.Constants;


@PersistenceCapable
public class Session {
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    
    private String sessionId;
    
    @Persistent
    private UserProfile user;
        
    @Persistent
    private ArrayList<String> coordinates;
    
    public Session(UserProfile user, String sessionId) {
        this.user = user;
        this.sessionId = sessionId;
        coordinates = new ArrayList<String>();
    }
    
    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }
        
    public ArrayList<String> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<String> coordinates) {
        this.coordinates = coordinates;
    }
    
    public static double getTimestamp(String coord) {
        return Double.parseDouble(coord.split(";")[0]);
    }
    
    public static double getLatitude(String coord) {
        return Double.parseDouble(coord.split(";")[1]);
    }
    
    public static double getLongitude(String coord) {
        return Double.parseDouble(coord.split(";")[2]);
    }
    
    public static double getAltitude(String coord) {
        return Double.parseDouble(coord.split(";")[3]);
    }
    
    public static String setLatLgnAlt(long timestamp, double lat, double lgn, double alt) {
        return timestamp + Constants.COORDINATES_SEP + lat + Constants.COORDINATES_SEP + lgn + Constants.COORDINATES_SEP + alt;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
