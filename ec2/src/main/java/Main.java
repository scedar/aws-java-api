import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.VolumeType;

import java.util.ArrayList;

public class Main {

    public static void main(String[] arge){

        AmazonEc2 ec2 = new AmazonEc2(AwsCredentials.getAwsCredentials());
        /*ec2.setRegion(Regions.US_WEST_2);*/
        ec2.setImageId("ami-835b4efa");
        ec2.setInstanceType(Constants.EC2InstanceType.T2_NANO);
        ec2.setMinCount(1);
        ec2.setMaxCount(1);
        ec2.setBlockDeviceMappingName("/dev/sda1");
        ec2.setVolumeSize(10);
        ec2.setVolumeType(VolumeType.Gp2);
        ec2.setDeleteVolumeOnTerminate(true);
        ec2.setKeyName("skyworld");
        ec2.setAdditionalInfo("Created By Scedar Technologies Co.");
        ec2.setPrivateIpAddress("172.31.16.15");
        ec2.setSubnetId("subnet-5d20c33b");


        /*
         * Available Resources
         */

        SecurityGroupConfig securityGroupConfig = new SecurityGroupConfig();

        //Be sure to set appropriate values in the securityConfig object before using it
        Requests.createSecurityGroup(securityGroupConfig);
        Requests.describeSecurityGroup(new String[] {"groupId1", "groupId2"});
        Requests.deleteSecurityGroup("sky-23421");

        Results.InstanceResults runInstanceResults = Requests.runInstance(ec2);
        Requests.stopInstance("abcd-1234-wxyz-7890");
        Requests.startInstance("abcd-1234-wxyz-7890");
        Requests.rebootInstance("abcd-1234-wxyz-7890");
        Requests.enableInstanceMonitoring("abcd-1234-wxyz-7890");
        Requests.disableInstanceMonitoring("abcd-1234-wxyz-7890");
        Requests.describeInstances();
        Requests.changeInstanceType("abcd-1234-wxyz-7890",
                Constants.EC2InstanceType.T2_NANO);
        Requests.terminateInstance("abcd-1234-wxyz-7890");

        Requests.allocateElasticIP("abcd-1234-wxyz-7890");
        Requests.describeElasticIP();
        Requests.releaseElasticIP("asdf-45678");

        AmazonVolume amazonVolume = new AmazonVolume();
        amazonVolume.setAvailabilityZone("availabilityZone");
        amazonVolume.setVolumeSize(10);

        Requests.adjustInstanceVolume("abcd-1234-wxyz-7890",
                "v-123-asd",
                10);
        Requests.createVolume(amazonVolume);
        Requests.attachVolumeToEC2Instance("abcd-1234-wxyz-7890", amazonVolume);
        Requests.detachVolume("abcd-1234-wxyz-7890", amazonVolume);

        Requests.describeRegionsAndZones();
    }
}
