import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.VolumeType;

import java.util.ArrayList;

public class AmazonVolume {
    private String volumeId;
    private int volumeSize;
    private Regions region;
    private VolumeType volumeType;
    private ArrayList<Tag> instanceTags = new ArrayList<Tag>();
    private String availabilityZone;
    private String device;

    //Constructors
    AmazonVolume(Regions region){
        this.region = region;
        this.volumeType = VolumeType.Gp2;
        this.device = "/dev/sdb";
    }

    AmazonVolume(){
        this.region = Regions.US_WEST_2;
        this.volumeType = VolumeType.Gp2;
        this.device = "/dev/sdb";
    }

    public void addInstanceTag(Tag tag){
        this.instanceTags.add(tag);
    }


    //Setters
    public void setVolumeId(String volumeId) {
        this.volumeId = volumeId;
    }

    public void setVolumeSize(int volumeSize) {
        this.volumeSize = volumeSize;
    }

    public void setRegion(Regions region) {
        this.region = region;
    }

    public void setVolumeType(VolumeType volumeType) {
        this.volumeType = volumeType;
    }

    public void setAvailabilityZone(String availabilityZone) {
        this.availabilityZone = availabilityZone;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    //Getters
    public String getVolumeId() {
        return volumeId;
    }

    public int getVolumeSize() {
        return volumeSize;
    }

    public Regions getRegion() {
        return region;
    }

    public VolumeType getVolumeType() {
        return volumeType;
    }

    public ArrayList<Tag> getInstanceTags() {
        return instanceTags;
    }

    public String getAvailabilityZone() {
        return availabilityZone;
    }

    public String getDevice() {
        return device;
    }
}
