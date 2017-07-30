import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.ec2.model.SecurityGroup;

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

    public static void createSecurityGroup(final SecurityGroupConfig securityGroup){
        final AmazonEC2 ec2 = AmazonEc2.getAmazonEC2(AwsCredentials.getAwsCredentials());
        CreateSecurityGroupRequest createRequest = new
                CreateSecurityGroupRequest()
                .withGroupName(securityGroup.getGroupName())
                .withDescription(securityGroup.getGroupDescription())
                .withVpcId(securityGroup.getVpcId());

        CreateSecurityGroupResult createResponse =
                ec2.createSecurityGroup(createRequest);

        System.out.printf(
                "Successfully created security group named %s",
                securityGroup.getGroupName());

        IpRange ipRange = new IpRange()
                .withCidrIp(securityGroup.getcIDrIp());

        IpPermission ipPerm = new IpPermission()
                .withIpProtocol(securityGroup.getIpProtocol1())
                .withToPort(securityGroup.getToPort1())
                .withFromPort(securityGroup.getFromPort1())
                .withIpv4Ranges(ipRange);

        IpPermission ipPerm2 = new IpPermission()
                .withIpProtocol(securityGroup.getIpProtocol2())
                .withToPort(securityGroup.getToPort2())
                .withFromPort(securityGroup.getFromPort2())
                .withIpv4Ranges(ipRange);

        AuthorizeSecurityGroupIngressRequest authRequest = new
                AuthorizeSecurityGroupIngressRequest()
                .withGroupName(securityGroup.getGroupName())
                .withIpPermissions(ipPerm, ipPerm2);

        AuthorizeSecurityGroupIngressResult authResponse =
                ec2.authorizeSecurityGroupIngress(authRequest);

        System.out.printf(
                "Successfully added ingress policy to security group %s",
                securityGroup.getGroupName());
    }

    public static void describeSecurityGroup(final String[] groupId)
    {
        final AmazonEC2 ec2 = AmazonEc2.getAmazonEC2(AwsCredentials.getAwsCredentials());

        /*if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String group_id = args[0];*/

        DescribeSecurityGroupsRequest request =
                new DescribeSecurityGroupsRequest()
                        .withGroupIds(groupId);

        DescribeSecurityGroupsResult response =
                ec2.describeSecurityGroups(request);

        for(SecurityGroup group : response.getSecurityGroups()) {
            System.out.printf(
                    "Found security group with id %s, " +
                            "vpc id %s " +
                            "and description %s",
                    group.getGroupId(),
                    group.getVpcId(),
                    group.getDescription());
        }
    }

    public static void deleteSecurityGroup(final String groupId)
    {
        final AmazonEC2 ec2 = AmazonEc2.getAmazonEC2(AwsCredentials.getAwsCredentials());

        DeleteSecurityGroupRequest request = new DeleteSecurityGroupRequest()
                .withGroupId(groupId);

        DeleteSecurityGroupResult response = ec2.deleteSecurityGroup(request);

        System.out.printf(
                "Successfully deleted security group with id %s", groupId);
    }

    public static Results.InstanceResults runInstance(final AmazonEc2 ec2){

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

        ArrayList<String> singleTagWithOnly2Items = new ArrayList<String>();
        singleTagWithOnly2Items.add("key");
        singleTagWithOnly2Items.add("value");
        ec2.addTag(singleTagWithOnly2Items);

        for (Instance instance : instances) {
            CreateTagsRequest createTagsRequest = new CreateTagsRequest();
            for (ArrayList<String> tag : ec2.getTags()){
                createTagsRequest.withResources(instance.getInstanceId()) //
                        .withTags(new Tag(tag.get(0), tag.get(1)+" - "+id));
            }
            ec2.getEc2Instance().createTags(createTagsRequest);
            System.out.println("Instance: "+instance.getInstanceId());
            id++;
        }

        return result;
    }

    public static void stopInstance(final String instanceId){
        final AmazonEC2 ec2 = AmazonEc2.getAmazonEC2(AwsCredentials.getAwsCredentials());
        StopInstancesRequest stopInstancesRequest = new StopInstancesRequest().withInstanceIds(instanceId);
        StopInstancesResult stopInstancesResult = ec2.stopInstances(stopInstancesRequest);
        List<InstanceStateChange> stateChangeList = stopInstancesResult.getStoppingInstances();
        System.out.println("Stopping instance '" + instanceId + "':");
        System.out.println(stateChangeList.toString());
    }

    public static void startInstance(final String instanceId){
        final AmazonEC2 ec2 = AmazonEc2.getAmazonEC2(AwsCredentials.getAwsCredentials());
        StartInstancesRequest startRequest = new StartInstancesRequest()
                .withInstanceIds(instanceId);
        StartInstancesResult startResult = ec2.startInstances(startRequest);
        List<InstanceStateChange> stateChangeList = startResult.getStartingInstances();
        System.out.println("Starting instance '" + instanceId + "':");
        System.out.println(stateChangeList.toString());
    }

    public static void rebootInstance(final String instanceId){
        final AmazonEC2 ec2 = AmazonEc2.getAmazonEC2(AwsCredentials.getAwsCredentials());
        RebootInstancesRequest request = new RebootInstancesRequest()
                .withInstanceIds(instanceId);
        RebootInstancesResult response = ec2.rebootInstances(request);
        System.out.printf(
                "Successfully rebooted instance %s", instanceId);
    }

    public static void enableInstanceMonitoring(final String instanceId){
        final AmazonEC2 ec2 = AmazonEc2.getAmazonEC2(AwsCredentials.getAwsCredentials());
        MonitorInstancesRequest request = new MonitorInstancesRequest()
                .withInstanceIds(instanceId);
        MonitorInstancesResult response = ec2.monitorInstances(request);
        List<InstanceMonitoring> instanceMonitoringList = response.getInstanceMonitorings();
        System.out.println("Enabled Monitoring on instance '" + instanceId + "':");
        System.out.println(instanceMonitoringList.toString());
    }

    public static void disableInstanceMonitoring(final String instanceId){
        final AmazonEC2 ec2 = AmazonEc2.getAmazonEC2(AwsCredentials.getAwsCredentials());
        UnmonitorInstancesRequest request = new UnmonitorInstancesRequest()
                .withInstanceIds(instanceId);
        UnmonitorInstancesResult response = ec2.unmonitorInstances(request);
        List<InstanceMonitoring> instanceMonitoringList = response.getInstanceMonitorings();
        System.out.println("Disabled Monitoring on instance '" + instanceId + "':");
        System.out.println(instanceMonitoringList.toString());
    }

    public static void describeInstances(){
        final AmazonEC2 ec2 = AmazonEc2.getAmazonEC2(AwsCredentials.getAwsCredentials());
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

    public static void terminateInstance(final String instanceId)
    {
        final AmazonEC2 ec2 = AmazonEc2.getAmazonEC2(AwsCredentials.getAwsCredentials());
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

    public static void describeElasticIP(){
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        DescribeAddressesResult response = ec2.describeAddresses();

        for(Address address : response.getAddresses()) {
            System.out.printf(
                    "Found address with public IP %s, " +
                            "domain %s, " +
                            "allocation id %s " +
                            "and NIC id %s",
                    address.getPublicIp(),
                    address.getDomain(),
                    address.getAllocationId(),
                    address.getNetworkInterfaceId());
        }
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

    public static void adjustInstanceVolume(final String instanceId, final String volumeId, final int volumeSize) {
        final AmazonEC2 ec2 = AmazonEc2.getAmazonEC2(AwsCredentials.getAwsCredentials());
        ModifyVolumeRequest modifyVolumeRequest = new ModifyVolumeRequest();
        modifyVolumeRequest.setSize(volumeSize);
        modifyVolumeRequest.withVolumeId(volumeId);

        ModifyVolumeResult modifyVolumeResult = ec2.modifyVolume(modifyVolumeRequest);
        VolumeModification volumeModification = modifyVolumeResult.getVolumeModification();
        System.out.println("Modifying instance '" + instanceId + "':");
        System.out.println(volumeModification.toString());

    }

    public static void changeInstanceType(final String instanceId, final String instanceType){
        final AmazonEC2 ec2 = AmazonEc2.getAmazonEC2(AwsCredentials.getAwsCredentials());
        ModifyInstanceAttributeRequest modifyInstanceAttributeRequest = new ModifyInstanceAttributeRequest();
        modifyInstanceAttributeRequest.withInstanceId(instanceId);
        modifyInstanceAttributeRequest.withInstanceType(instanceType);

        ModifyInstanceAttributeResult modifyInstanceAttributeResult = ec2.modifyInstanceAttribute(modifyInstanceAttributeRequest);
        System.out.println(modifyInstanceAttributeResult.toString());
        System.out.println("Modifying instance '" + instanceId + "':");
    }

    public static void createVolume(final AmazonVolume amazonVolume)
    {
        final AmazonEC2 ec2 = AmazonEc2.getAmazonEC2(AwsCredentials.getAwsCredentials());
        /* Yet to Test Code */
        CreateVolumeRequest createVolumeRequest = new CreateVolumeRequest()
                .withVolumeType(amazonVolume.getVolumeType())
                .withAvailabilityZone(amazonVolume.getAvailabilityZone()) // The AZ in which to create the volume.
                .withSize(amazonVolume.getVolumeSize()); // The size of the volume, in gigabytes.

        CreateVolumeResult createVolumeResult = ec2.createVolume(createVolumeRequest);
        createVolumeResult.getVolume().getVolumeType();
        System.out.println("Volume of size - "+createVolumeResult.getVolume().getSize() +
                ", volume id - " + createVolumeResult.getVolume().getVolumeId() +
                " and volume type - "+createVolumeResult.getVolume().getVolumeType()+
                " has been created successfully");

    }

    public static void attachVolumeToEC2Instance(final String instanceId, final AmazonVolume amazonVolume)
    {
        final AmazonEC2 ec2 = AmazonEc2.getAmazonEC2(AwsCredentials.getAwsCredentials());
        AttachVolumeRequest attachRequest = new AttachVolumeRequest()
                .withInstanceId(instanceId)
                .withDevice(amazonVolume.getDevice())
                .withVolumeId(amazonVolume.getVolumeId());

        AttachVolumeResult attachResult = ec2.attachVolume(attachRequest);

        System.out.println("Volume of volume id - "+attachResult.getAttachment().getVolumeId() +
                ", and device - " + attachResult.getAttachment().getDevice() +
                " has been successfully attached to"+
                " instance with id - "+attachResult.getAttachment().getInstanceId());
    }
    public static void detachVolume(final String instanceId, final AmazonVolume amazonVolume)
    {
        final AmazonEC2 ec2 = AmazonEc2.getAmazonEC2(AwsCredentials.getAwsCredentials());
        /* Yet to Test Code */
        DetachVolumeRequest detachRequest = new DetachVolumeRequest()
                .withInstanceId(instanceId)
                .withDevice(amazonVolume.getDevice())
                .withVolumeId(amazonVolume.getVolumeId());

        DetachVolumeResult detachResult = ec2.detachVolume(detachRequest);

        System.out.println("Volume of volume id - "+detachResult.getAttachment().getVolumeId() +
                ", and device - " + detachResult.getAttachment().getDevice() +
                " has been successfully detached from "+
                " instance with id - "+detachResult.getAttachment().getInstanceId());

    }


    public static void describeRegionsAndZones()
    {
        final AmazonEC2 ec2 = AmazonEc2.getAmazonEC2(AwsCredentials.getAwsCredentials());

        DescribeRegionsResult regionsResponse = ec2.describeRegions();

        for(Region region : regionsResponse.getRegions()) {
            System.out.printf(
                    "Found region %s " +
                            "with endpoint %s",
                    region.getRegionName(),
                    region.getEndpoint()+
                            "\n\n");
        }

        DescribeAvailabilityZonesResult zonesResponse =
                ec2.describeAvailabilityZones();

        for(AvailabilityZone zone : zonesResponse.getAvailabilityZones()) {
            System.out.printf(
                    "Found availability zone %s " +
                            "with status %s " +
                            "in region %s",
                    zone.getZoneName(),
                    zone.getState(),
                    zone.getRegionName()+
                            "\n\n");
        }
    }

}
