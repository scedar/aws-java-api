import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;

import java.util.ArrayList;

public class Utility {

    public static ArrayList<BlockDeviceMapping> getBlockDeviceMappingList(AmazonEc2 ec2){
        // Create the block device mapping to describe the root partition.
        BlockDeviceMapping blockDeviceMapping = new BlockDeviceMapping();
        blockDeviceMapping.setDeviceName(ec2.getBlockDeviceMappingName());

        // Set the delete on termination flag to false.
        EbsBlockDevice ebs = new EbsBlockDevice();
        ebs.setDeleteOnTermination(ec2.isDeleteVolumeOnTerminate());
        ebs.setVolumeSize(ec2.getVolumeSize());
        ebs.setVolumeType(ec2.getVolumeType());
        blockDeviceMapping.setEbs(ebs);

        // Add the block device mapping to the block list.
        ArrayList<BlockDeviceMapping> blockList = new ArrayList<BlockDeviceMapping>();
        blockList.add(blockDeviceMapping);

        return blockList;
    }

    public static AmazonEc2 addTags(AmazonEc2 ec2){
        ArrayList<String> tagName = new ArrayList<String>();
        tagName.add("Name");
        tagName.add("Scedar Technologies Co.");

        ArrayList<String> tagIP = new ArrayList<String>();
        tagIP.add("IP");
        tagIP.add("172.30.16.10");

        ArrayList<String> tagOwner = new ArrayList<String>();
        tagOwner.add("Owner");
        tagOwner.add("Scedar Co.");

        ArrayList<ArrayList<String>> tags = new ArrayList<ArrayList<String>>();
        tags.add(tagName);
        tags.add(tagIP);
        tags.add(tagOwner);

        ec2.setTags(tags);
        return ec2;
    }
}
