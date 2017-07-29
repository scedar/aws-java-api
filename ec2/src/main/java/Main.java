import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.model.VolumeType;

import java.util.ArrayList;

public class Main {

    public static void main(String[] arge){

        AmazonEc2 ec2 = new AmazonEc2();
        ec2.setEc2Instance(AmazonEc2.initializeObject(AwsCredentials.getAwsCredentials()));
        ec2.setRegion(Regions.US_WEST_2);
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

        Results.InstanceResults runInstanceResults = Requests.runInstance(ec2);

        Requests.stopInstance("abcd-1234-wxyz-7890",
                AmazonEc2.initializeObject(AwsCredentials.getAwsCredentials()));

        Requests.startInstance("abcd-1234-wxyz-7890",
                AmazonEc2.initializeObject(AwsCredentials.getAwsCredentials()));

        Requests.changeInstanceType("abcd-1234-wxyz-7890",
                AmazonEc2.initializeObject(AwsCredentials.getAwsCredentials()),
                Constants.EC2InstanceType.T2_NANO);

        Requests.allocateElasticIP("abcd-1234-wxyz-7890");

        Requests.releaseElasticIP("asdf-45678");

        Requests.terminateInstance("abcd-1234-wxyz-7890",
                AmazonEc2.initializeObject(AwsCredentials.getAwsCredentials()));

        Requests.adjustVolume("abcd-1234-wxyz-7890",
                AmazonEc2.initializeObject(AwsCredentials.getAwsCredentials()),
                "v-123-asd",
                10);

        Requests.createEC2Volume("abcd-1234-wxyz-7890",
                AmazonEc2.initializeObject(AwsCredentials.getAwsCredentials()),
                10,
                Regions.US_WEST_2);
    }
}
