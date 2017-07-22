import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.VolumeType;

import java.util.ArrayList;

/*
 * Created by elon on 7/22/17.
 */
public class AmazonEc2 {

    private AmazonEC2 ec2Instance;
    private Regions region;
    private String imageId;
    private String instanceType;
    private int minCount;
    private int maxCount;
    private String keyName;
    private String privateIpAddress;
    private String subnetId;
    private String blockDeviceMappingName;
    private int volumeSize;
    private boolean deleteVolumeOnTerminate;
    private VolumeType volumeType;
    private String additionalInfo;
    private ArrayList<ArrayList<String>> tags;

    public static AmazonEC2 initializeObject(AWSCredentials awsCredentials){
        return new AmazonEC2Client(awsCredentials);
    }

    //Setters

    public void setEc2Instance(AmazonEC2 ec2Instance) {
        this.ec2Instance = ec2Instance;
    }

    public void setRegion(Regions region) {
        this.region = region;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public void setMinCount(int minCount) {
        this.minCount = minCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public void setPrivateIpAddress(String privateIpAddress) {
        this.privateIpAddress = privateIpAddress;
    }

    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
    }

    public void setBlockDeviceMappingName(String blockDeviceMappingName) {
        this.blockDeviceMappingName = blockDeviceMappingName;
    }

    public void setVolumeSize(int volumeSize) {
        this.volumeSize = volumeSize;
    }

    public void setDeleteVolumeOnTerminate(boolean deleteVolumeOnTerminate) {
        this.deleteVolumeOnTerminate = deleteVolumeOnTerminate;
    }

    public void setVolumeType(VolumeType volumeType) {
        this.volumeType = volumeType;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public void setTags(ArrayList<ArrayList<String>> tags) {
        this.tags = tags;
    }


    //Getters

    public AmazonEC2 getEc2Instance() {
        return ec2Instance;
    }

    public Regions getRegion() {
        return region;
    }

    public String getImageId() {
        return imageId;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public int getMinCount() {
        return minCount;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public String getKeyName() {
        return keyName;
    }

    public String getPrivateIpAddress() {
        return privateIpAddress;
    }

    public String getSubnetId() {
        return subnetId;
    }

    public String getBlockDeviceMappingName() {
        return blockDeviceMappingName;
    }

    public int getVolumeSize() {
        return volumeSize;
    }

    public boolean isDeleteVolumeOnTerminate() {
        return deleteVolumeOnTerminate;
    }

    public VolumeType getVolumeType() {
        return volumeType;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public ArrayList<ArrayList<String>> getTags() {
        return tags;
    }
}
