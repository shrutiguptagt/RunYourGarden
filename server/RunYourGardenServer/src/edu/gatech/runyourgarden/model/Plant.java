package edu.gatech.runyourgarden.model;

public class Plant {
    private int plantId;
    private int cost;
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
