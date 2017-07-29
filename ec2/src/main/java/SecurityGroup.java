public class SecurityGroup {
    private String groupName;
    private String groupDescription;
    private String vpcId;
    private String cIDrIp;
    private String ipProtocol1;
    private int toPort1;
    private int fromPort1;
    private String ipProtocol2;
    private int toPort2;
    private int fromPort2;

    SecurityGroup(){
        this.cIDrIp = "0.0.0.0/0";
        this.ipProtocol1 = "tcp";
        this.ipProtocol2 = "tcp";
        this.toPort1 = 80;
        this.toPort2 = 80;
        this.fromPort1 = 22;
        this.fromPort2 = 22;
    }


    public String getGroupName() {
        return groupName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public String getVpcId() {
        return vpcId;
    }

    public String getcIDrIp() {
        return cIDrIp;
    }

    public String getIpProtocol1() {
        return ipProtocol1;
    }

    public int getToPort1() {
        return toPort1;
    }

    public int getFromPort1() {
        return fromPort1;
    }

    public String getIpProtocol2() {
        return ipProtocol2;
    }

    public int getToPort2() {
        return toPort2;
    }

    public int getFromPort2() {
        return fromPort2;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public void setVpcId(String vpcId) {
        this.vpcId = vpcId;
    }

    public void setcIDrIp(String cIDrIp) {
        this.cIDrIp = cIDrIp;
    }

    public void setIpProtocol1(String ipProtocol1) {
        this.ipProtocol1 = ipProtocol1;
    }

    public void setToPort1(int toPort1) {
        this.toPort1 = toPort1;
    }

    public void setFromPort1(int fromPort1) {
        this.fromPort1 = fromPort1;
    }

    public void setIpProtocol2(String ipProtocol2) {
        this.ipProtocol2 = ipProtocol2;
    }

    public void setToPort2(int toPort2) {
        this.toPort2 = toPort2;
    }

    public void setFromPort2(int fromPort2) {
        this.fromPort2 = fromPort2;
    }
}
