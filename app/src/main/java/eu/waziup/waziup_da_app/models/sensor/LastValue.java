
package eu.waziup.waziup_da_app.models.sensor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LastValue {

    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("date_received")
    @Expose
    private String dateReceived;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(String dateReceived) {
        this.dateReceived = dateReceived;
    }

}
