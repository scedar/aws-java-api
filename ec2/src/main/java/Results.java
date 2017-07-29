import com.amazonaws.services.ec2.model.RunInstancesResult;
import org.json.JSONArray;
import org.json.JSONObject;

class Results {

    static InstanceResults getCreateInstanceResults(RunInstancesResult createInstanceResults){
        JSONArray response = new JSONArray(createInstanceResults);
        JSONObject responseObj2 = response.getJSONObject(0);

        return new InstanceResults(
                createInstanceResults,
                responseObj2.getString(""),
                responseObj2.getString(""));
    }



    public static class InstanceResults{
        private String instanceId;
        private String ipAddress;
        private RunInstancesResult originalResults;

        InstanceResults(RunInstancesResult originalResults, String instanceId, String ipAddress) {
            this.originalResults = originalResults;
            this.instanceId = instanceId;
            this.ipAddress = ipAddress;
        }

        public RunInstancesResult getOriginalResults(){
            return originalResults;
        }

        public String getInstanceId() {
            return instanceId;
        }

        public String getIpAddress() {
            return ipAddress;
        }
    }
}
