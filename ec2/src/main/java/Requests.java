import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;

import java.util.ArrayList;
import java.util.List;

public class Requests {

    /**TODO
     * While in production mode, Always dry run a request before actually making it.
     * To dry run, use the lambda resource below:
     *       DryRunSupportedRequest<[Your Request]> dryRequest =
     *               () -> {
     *                       [Your Request] request = new [Your Request]()
     *                   .withInstanceIds(instance_id);
     *
     *           return request.getDryRunRequest();
     *       };
     *
     *       DryRunResult dryResponse = ec2.dryRun(dryRequest);
     *
     *           if (!dryResponse.isSuccessful()) {
     *           System.out.printf(
     *                   "Failed dry run to enable monitoring on instance %s",
     *                   instance_id);
     *
     *           throw dryResponse.getDryRunResponse();
     *       }
     */

    public static Results.InstanceResults runInstance(AmazonEc2 ec2){

        RunInstancesRequest runInstancesRequest = new RunInstancesRequest();

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

        return result;
    }

    public static void stopInstance(final String instanceId, final AmazonEC2 ec2){
        StopInstancesRequest stopInstancesRequest = new StopInstancesRequest().withInstanceIds(instanceId);
        StopInstancesResult stopInstancesResult = ec2.stopInstances(stopInstancesRequest);
        List<InstanceStateChange> stateChangeList = stopInstancesResult.getStoppingInstances();
        System.out.println("Stopping instance '" + instanceId + "':");
        System.out.println(stateChangeList.toString());
    }

    public static void startInstance(final String instanceId, final AmazonEC2 ec2){
        StartInstancesRequest startRequest = new StartInstancesRequest()
                .withInstanceIds(instanceId);
        StartInstancesResult startResult = ec2.startInstances(startRequest);
        List<InstanceStateChange> stateChangeList = startResult.getStartingInstances();
        System.out.println("Starting instance '" + instanceId + "':");
        System.out.println(stateChangeList.toString());
    }

    public static void rebootInstance(final String instanceId, final AmazonEC2 ec2){
        RebootInstancesRequest request = new RebootInstancesRequest()
                .withInstanceIds(instanceId);
        RebootInstancesResult response = ec2.rebootInstances(request);
        System.out.printf(
                "Successfully rebooted instance %s", instanceId);
    }

    public static void enableMonitorInstance(final String instanceId, final AmazonEC2 ec2){
        MonitorInstancesRequest request = new MonitorInstancesRequest()
                .withInstanceIds(instanceId);
        MonitorInstancesResult response = ec2.monitorInstances(request);
        List<InstanceMonitoring> instanceMonitoringList = response.getInstanceMonitorings();
        System.out.println("Enabled Monitoring on instance '" + instanceId + "':");
        System.out.println(instanceMonitoringList.toString());
    }

    public static void disableeMonitorInstance(final String instanceId, final AmazonEC2 ec2){
        UnmonitorInstancesRequest request = new UnmonitorInstancesRequest()
                .withInstanceIds(instanceId);
        UnmonitorInstancesResult response = ec2.unmonitorInstances(request);
        List<InstanceMonitoring> instanceMonitoringList = response.getInstanceMonitorings();
        System.out.println("Disabled Monitoring on instance '" + instanceId + "':");
        System.out.println(instanceMonitoringList.toString());
    }

    public static void describeInstance(final AmazonEC2 ec2){
        boolean done = false;

        while(!done) {
            DescribeInstancesRequest request = new DescribeInstancesRequest();
            DescribeInstancesResult response = ec2.describeInstances(request);

            for(Reservation reservation : response.getReservations()) {
                for(Instance instance : reservation.getInstances()) {
                    System.out.printf(
                            "Found reservation with id %s, " +
                                    "AMI %s, " +
                                    "type %s, " +
                                    "state %s " +
                                    "and monitoring state %s",
                            instance.getInstanceId(),
                            instance.getImageId(),
                            instance.getInstanceType(),
                            instance.getState().getName(),
                            instance.getMonitoring().getState() + "\n\n");
                }
            }

            request.setNextToken(response.getNextToken());

            if(response.getNextToken() == null) {
                done = true;
            }
        }
    }

    public static void terminateInstance(final String instanceId, final AmazonEC2 ec2)
    {
        TerminateInstancesRequest terminateInstancesRequest =
                new TerminateInstancesRequest().withInstanceIds(instanceId);
        TerminateInstancesResult terminateInstancesResult =
                ec2.terminateInstances(terminateInstancesRequest);
        List<InstanceStateChange> stateChangeList =
                terminateInstancesResult.getTerminatingInstances();
        System.out.println("Terminating instance '" + instanceId + "':");
        System.out.println(stateChangeList.toString());
    }

    public static void allocateElasticIP(final String instance_id){
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        AllocateAddressRequest allocate_request = new AllocateAddressRequest()
                .withDomain(DomainType.Vpc);

        AllocateAddressResult allocate_response =
                ec2.allocateAddress(allocate_request);

        String allocation_id = allocate_response.getAllocationId();
        allocate_response.getPublicIp();
        System.out.println("Alloc ID - "+allocate_response.getAllocationId()+
                "\nIP - "+allocate_response.getPublicIp());

        AssociateAddressRequest associate_request =
                new AssociateAddressRequest()
                        .withInstanceId(instance_id)
                        .withAllocationId(allocation_id);

        AssociateAddressResult associate_response =
                ec2.associateAddress(associate_request);

        System.out.println(associate_request.toString());
        System.out.println(associate_response.toString());
    }

    public static void createSecurityGroup(final SecurityGroup securityGroup, final AmazonEC2 ec2){
        CreateSecurityGroupRequest create_request = new
                CreateSecurityGroupRequest()
                .withGroupName(securityGroup.getGroupName())
                .withDescription(securityGroup.getGroupDescription())
                .withVpcId(securityGroup.getVpcId());

        CreateSecurityGroupResult create_response =
                ec2.createSecurityGroup(create_request);

        System.out.printf(
                "Successfully created security group named %s",
                securityGroup.getGroupName());

        IpRange ip_range = new IpRange()
                .withCidrIp(securityGroup.getcIDrIp());

        IpPermission ip_perm = new IpPermission()
                .withIpProtocol(securityGroup.getIpProtocol1())
                .withToPort(securityGroup.getToPort1())
                .withFromPort(securityGroup.getFromPort1())
                .withIpv4Ranges(ip_range);

        IpPermission ip_perm2 = new IpPermission()
                .withIpProtocol(securityGroup.getIpProtocol2())
                .withToPort(securityGroup.getToPort2())
                .withFromPort(securityGroup.getFromPort2())
                .withIpv4Ranges(ip_range);

        AuthorizeSecurityGroupIngressRequest auth_request = new
                AuthorizeSecurityGroupIngressRequest()
                .withGroupName(securityGroup.getGroupName())
                .withIpPermissions(ip_perm, ip_perm2);

        AuthorizeSecurityGroupIngressResult auth_response =
                ec2.authorizeSecurityGroupIngress(auth_request);

        System.out.printf(
                "Successfully added ingress policy to security group %s",
                securityGroup.getGroupName());
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

    public static void adjustVolume(final String instanceId, final AmazonEC2 ec2, String volumeId, int volumeSize) {

        ModifyVolumeRequest modifyVolumeRequest = new ModifyVolumeRequest();
        modifyVolumeRequest.setSize(volumeSize);
        modifyVolumeRequest.withVolumeId(volumeId);

        ModifyVolumeResult modifyVolumeResult = ec2.modifyVolume(modifyVolumeRequest);
        VolumeModification volumeModification = modifyVolumeResult.getVolumeModification();
        System.out.println("Modifying instance '" + instanceId + "':");
        System.out.println(volumeModification.toString());

    }

    public static void changeInstanceType(final String instanceId, final AmazonEC2 ec2, String instanceType){
        ModifyInstanceAttributeRequest modifyInstanceAttributeRequest = new ModifyInstanceAttributeRequest();
        modifyInstanceAttributeRequest.withInstanceId(instanceId);
        modifyInstanceAttributeRequest.withInstanceType(instanceType);

        ModifyInstanceAttributeResult modifyInstanceAttributeResult = ec2.modifyInstanceAttribute(modifyInstanceAttributeRequest);
        System.out.println(modifyInstanceAttributeResult.toString());
        System.out.println("Modifying instance '" + instanceId + "':");
    }

    public static void createEC2Volume(final String instanceId, final AmazonEC2 ec2, int volumeSize, Regions region)
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
