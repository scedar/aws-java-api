import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;

import java.util.ArrayList;
import java.util.List;

public class Requests {

    public static void runInstance(){

        RunInstancesRequest runInstancesRequest = new RunInstancesRequest();

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

        runInstancesRequest
                .withImageId(ec2.getImageId())
                .withMinCount(ec2.getMinCount())
                .withMaxCount(ec2.getMaxCount())
                .withBlockDeviceMappings(Utility.getBlockDeviceMappingList(ec2))
                .withKeyName(ec2.getKeyName())
                .withAdditionalInfo(ec2.getAdditionalInfo())
                .withPrivateIpAddress(ec2.getPrivateIpAddress())
                .withSubnetId(ec2.getSubnetId());

        Results.InstanceResults result = Results.getCreateInstanceResults(ec2
                .getEc2Instance()
                .runInstances(runInstancesRequest)
        );

        // TAG EC2 INSTANCES
        List<Instance> instances = result.getOriginalResults().getReservation().getInstances();
        int id = 1;
        ArrayList<ArrayList<String>> tags = ec2.getTags();

        for (Instance instance : instances) {
            CreateTagsRequest createTagsRequest = new CreateTagsRequest();
            for (ArrayList<String> tag : tags){
                createTagsRequest.withResources(instance.getInstanceId()) //
                        .withTags(new Tag(tag.get(0), tag.get(1)+" - "+id));
            }
            ec2.getEc2Instance().createTags(createTagsRequest);
            System.out.println("Instance: "+instance.getInstanceId());
            id++;
        }
    }

    public static void stopInstance(final String instanceId, AmazonEC2 ec2){
        StopInstancesRequest stopInstancesRequest = new StopInstancesRequest().withInstanceIds(instanceId);
        StopInstancesResult stopInstancesResult = ec2.stopInstances(stopInstancesRequest);
        List<InstanceStateChange> stateChangeList = stopInstancesResult.getStoppingInstances();
        System.out.println("Stopping instance '" + instanceId + "':");
        System.out.println(stateChangeList.toString());
    }

    public static void startInstance(final String instanceId, AmazonEC2 ec2){
        StartInstancesRequest startRequest = new StartInstancesRequest()
                .withInstanceIds(instanceId);
        StartInstancesResult startResult = ec2.startInstances(startRequest);
        List<InstanceStateChange> stateChangeList = startResult.getStartingInstances();
        System.out.println("Starting instance '" + instanceId + "':");
        System.out.println(stateChangeList.toString());
    }

    public static void terminateInstance(final String instanceId, AmazonEC2 ec2)
    {
        TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest().withInstanceIds(instanceId);
        TerminateInstancesResult terminateInstancesResult = ec2.terminateInstances(terminateInstancesRequest);
        List<InstanceStateChange> stateChangeList = terminateInstancesResult.getTerminatingInstances();
        System.out.println("Terminating instance '" + instanceId + "':");
        System.out.println(stateChangeList.toString());
    }

    public static void createElasticIP(final String instance_id){
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        AllocateAddressRequest allocate_request = new AllocateAddressRequest()
                .withDomain(DomainType.Vpc);

        AllocateAddressResult allocate_response =
                ec2.allocateAddress(allocate_request);

        String allocation_id = allocate_response.getAllocationId();
        allocate_response.getPublicIp();
        System.out.println("Alloc ID - "+allocate_response.getAllocationId()+"\nIP - "+allocate_response.getPublicIp());

        AssociateAddressRequest associate_request =
                new AssociateAddressRequest()
                        .withInstanceId(instance_id)
                        .withAllocationId(allocation_id);

        AssociateAddressResult associate_response =
                ec2.associateAddress(associate_request);

        System.out.println(associate_request.toString());
        System.out.println(associate_response.toString());
    }

    public static void releaseElasticIP(final String alloc_id){
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        ReleaseAddressRequest request = new ReleaseAddressRequest()
                .withAllocationId(alloc_id)
                /* Or .withPublicIp(publicIP)*/;

        ReleaseAddressResult response = ec2.releaseAddress(request);

        System.out.println(request);
        System.out.println(response);
    }

    public static void adjustVolume(final String instanceId, AmazonEC2 ec2, String volumeId, int volumeSize) {

        ModifyVolumeRequest modifyVolumeRequest = new ModifyVolumeRequest();
        modifyVolumeRequest.setSize(volumeSize);
        modifyVolumeRequest.withVolumeId(volumeId);

        ModifyVolumeResult modifyVolumeResult = ec2.modifyVolume(modifyVolumeRequest);
        VolumeModification volumeModification = modifyVolumeResult.getVolumeModification();
        System.out.println("Modifying instance '" + instanceId + "':");
        System.out.println(volumeModification.toString());

    }

    public static void changeInstanceType(final String instanceId, AmazonEC2 ec2, String instanceType){
        ModifyInstanceAttributeRequest modifyInstanceAttributeRequest = new ModifyInstanceAttributeRequest();
        modifyInstanceAttributeRequest.withInstanceId(instanceId);
        modifyInstanceAttributeRequest.withInstanceType(instanceType);

        ModifyInstanceAttributeResult modifyInstanceAttributeResult = ec2.modifyInstanceAttribute(modifyInstanceAttributeRequest);
        System.out.println(modifyInstanceAttributeResult.toString());
        System.out.println("Modifying instance '" + instanceId + "':");
    }

    public static void createEC2Volume(final String instanceId, AmazonEC2 ec2, int volumeSize, Regions region)
    {
        System.out.println("Creating the volume begins...");
        CreateVolumeRequest  creq = new CreateVolumeRequest(volumeSize, region.getName());
        creq.setVolumeType(VolumeType.Gp2);
        CreateVolumeResult cres =  ec2.createVolume(creq);

        // Create the list of tags we want to create
        System.out.println("Setting the tags to the volume...");
        ArrayList<Tag> instanceTags = new ArrayList<Tag>();
        instanceTags.add(new Tag("Name","scedar-group-volume"));

        CreateTagsRequest createTagsRequest = new CreateTagsRequest().withTags(instanceTags).withResources(cres.getVolume().getVolumeId());
        ec2.createTags(createTagsRequest);
        System.out.println("Attaching the volume to the instance....");
        AttachVolumeRequest areq = new AttachVolumeRequest(cres.getVolume().getVolumeId(),instanceId, "/dev/xvdh");
        AttachVolumeResult ares = ec2.attachVolume(areq);
        System.out.println("Creating the volume ends...");

    }

}
