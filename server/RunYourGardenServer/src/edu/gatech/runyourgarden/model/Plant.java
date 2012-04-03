package edu.gatech.runyourgarden.model;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Plant {
    @PrimaryKey
    @Persistent
    private int plantId;
    
    @Persistent
    private int cost;
    
    @Persistent
    private String imgLocation;
    
    public int getPlantId() {
        return plantId;
    }
    public void setPlantId(int plantId) {
        this.plantId = plantId;
    }
    public int getCost() {
        return cost;
    }
    public void setCost(int cost) {
        this.cost = cost;
    }
    public String getImgLocation() {
        return imgLocation;
    }
    public void setImgLocation(String imgLocation) {
        this.imgLocation = imgLocation;
    }
    
}
