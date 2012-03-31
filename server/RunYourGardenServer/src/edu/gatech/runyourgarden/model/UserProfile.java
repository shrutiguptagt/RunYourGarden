package edu.gatech.runyourgarden.model;

import java.util.ArrayList;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import edu.gatech.runyourgarden.model.Session;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class UserProfile {
    
    @Persistent
    private String userId;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    
    @Persistent
    private long accumCalories;
    
    @Persistent
    private double accumDistance;
    
    @Persistent
    private long accumTime;
    
    @Persistent
    private long remainCalories;
    
    @Persistent
    private double remainDistance;
    
    @Persistent
    private long remainTime;
    
    @Persistent
    private ArrayList<String> weightList;
    
    @Persistent
    private ArrayList<String> heightList;
    
    @Persistent
    private int age;
    
    @Persistent(mappedBy = "user")
    private ArrayList<Session> sessionsSet;
    
    @Persistent
    private ArrayList<String> bodyWeight;

    public UserProfile(String userId) {
        this.setUserId(userId);
        setSessionsSet(new ArrayList<Session>());
    }
    
    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public long getAccumCalories() {
        return accumCalories;
    }

    public void setAccumCalories(long accumCalories) {
        this.accumCalories = accumCalories;
    }

    public double getAccumDistance() {
        return accumDistance;
    }

    public void setAccumDistance(double accumDistance) {
        this.accumDistance = accumDistance;
    }

    public long getAccumTime() {
        return accumTime;
    }

    public void setAccumTime(long accumTime) {
        this.accumTime = accumTime;
    }

    public long getRemainCalories() {
        return remainCalories;
    }

    public void setRemainCalories(long remainCalories) {
        this.remainCalories = remainCalories;
    }

    public double getRemainDistance() {
        return remainDistance;
    }

    public void setRemainDistance(double remainDistance) {
        this.remainDistance = remainDistance;
    }

    public long getRemainTime() {
        return remainTime;
    }

    public void setRemainTime(long remainTime) {
        this.remainTime = remainTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<Session> getSessionsSet() {
        return sessionsSet;
    }

    public void setSessionsSet(ArrayList<Session> sessionsSet) {
        this.sessionsSet = sessionsSet;
    }

    public ArrayList<String> getBodyWeight() {
        return bodyWeight;
    }

    public void setBodyWeight(ArrayList<String> bodyWeight) {
        this.bodyWeight = bodyWeight;
    }

    public ArrayList<String> getWeightList() {
        return weightList;
    }

    public void setWeightList(ArrayList<String> weightList) {
        this.weightList = weightList;
    }
    
    public static float getWeightValue(String str) {
        return Float.parseFloat(str.split(";")[1]);
    }
    
    public static long getWeightTimeStamp(String str) {
        return Long.parseLong(str.split(";")[0]);        
    }

    public ArrayList<String> getHeightList() {
        return heightList;
    }

    public void setHeightList(ArrayList<String> heightList) {
        this.heightList = heightList;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
    
}
