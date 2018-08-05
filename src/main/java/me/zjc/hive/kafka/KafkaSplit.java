package me.zjc.hive.kafka;

import kafka.cluster.BrokerEndPoint;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.InputSplit;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Metadata of a topic partition and groupId's last read offset
 * @author Zhu Jiachuan
 */
public class KafkaSplit implements InputSplit {
    private String leaderUri;
    private List<String> replicaURI;
    private String topic;
    private int partitionId;
    private long earliestOffset;
    private long latestOffset;
    private String groupId;
    private long lastReadOffset;

    public KafkaSplit(String leaderUri,
                      List<BrokerEndPoint> replicaEndPoint,
                      String topic,
                      int partitionId,
                      String groupId,
                      long earliestOffset,
                      long latestOffset,
                      long lastReadOffset) {
        this.leaderUri = leaderUri;
        this.setReplicaURI(replicaEndPoint);
        this.topic = topic;
        this.partitionId = partitionId;
        this.earliestOffset = earliestOffset;
        this.latestOffset = latestOffset;
        this.groupId = groupId;
        this.lastReadOffset = lastReadOffset;
    }

    public void setLeaderUri(String leaderUri) {
        this.leaderUri = leaderUri;
    }

    @Override
    public long getLength() throws IOException {
        return latestOffset - earliestOffset;
    }

    @Override
    public String[] getLocations() throws IOException {
        return new String[0];
    }



    @Override
    public void write(DataOutput out) throws IOException {
        Text.writeString(out, leaderUri);
        String str = "";
        for (String uri : replicaURI) {
            str += uri + ",";
        }
        Text.writeString(out, str.substring(0, str.length() -1));
        Text.writeString(out, topic);
        out.writeInt(partitionId);
        out.writeLong(earliestOffset);
        out.writeLong(latestOffset);
        Text.writeString(out, groupId);
        out.writeLong(lastReadOffset);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.leaderUri = Text.readString(in);
        String str = Text.readString(in);
        this.replicaURI = Arrays.asList(str.split(","));
        this.topic = Text.readString(in);
        this.partitionId = in.readInt();
        this.earliestOffset = in.readLong();
        this.latestOffset = in.readLong();
        this.groupId = Text.readString(in);
        this.lastReadOffset = in.readLong();
    }

    public List<String> getReplicaURI() {
        return replicaURI;
    }

    public void setReplicaURI(List<BrokerEndPoint> replicaEndPoint) {
        List<String> replicas = new ArrayList<>();
        for (BrokerEndPoint brokerEndPoint : replicaEndPoint) {
            replicas.add(brokerEndPoint.connectionString());
        }
        this.replicaURI = replicas;
    }

    public String getLeaderUri() {
        return leaderUri;
    }

    public String getTopic() {
        return topic;
    }

    public int getPartitionId() {
        return partitionId;
    }

    public long getEarliestOffset() {
        return earliestOffset;
    }

    public long getLatestOffset() {
        return latestOffset;
    }

    public String getGroupId() {
        return groupId;
    }

    public long getLastReadOffset() {
        return lastReadOffset;
    }

    @Override
    public String toString() {
        return "KafkaSplit{" +
                ", leaderUri=" + leaderUri +
                ", topic='" + topic + '\'' +
                ", partitionId=" + partitionId +
                ", earliestOffset=" + earliestOffset +
                ", latestOffset=" + latestOffset +
                ", groupId='" + groupId + '\'' +
                ", lastReadOffset=" + lastReadOffset +
                '}';
    }
}
