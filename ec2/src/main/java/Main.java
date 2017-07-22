import com.amazonaws.regions.Regions;

import java.util.ArrayList;

public class Main {

    public static void main(String[] arge){

        Requests.runInstance();

        Requests.stopInstance("abcd-1234-wxyz-7890",
                AmazonEc2.initializeObject(AwsCredentials.getAwsCredentials()));

        Requests.startInstance("abcd-1234-wxyz-7890",
                AmazonEc2.initializeObject(AwsCredentials.getAwsCredentials()));

        Requests.changeInstanceType("abcd-1234-wxyz-7890",
                AmazonEc2.initializeObject(AwsCredentials.getAwsCredentials()),
                Constants.EC2InstanceType.T2_NANO);

        Requests.createElasticIP("abcd-1234-wxyz-7890");

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
