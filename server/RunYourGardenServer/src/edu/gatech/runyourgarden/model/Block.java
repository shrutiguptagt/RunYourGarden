package edu.gatech.runyourgarden.model;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Block {
    @PrimaryKey
    @Persistent
	int blockId;
	@Persistent
    int plantId;
	
	public void setPlantBlockId(int plantId,int blockId){
		this.plantId = plantId;
		this.blockId = blockId;
	}

	public int getPlantId() {
        return plantId;
    }
    public void setPlantId(int plantId) {
        this.plantId = plantId;
    }
    public int getBlockId() {
        return blockId;
    }
    public void setBlockId(int blockId) {
    	this.blockId = blockId;
    }
	
}
