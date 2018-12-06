
package eu.waziup.app.data.network.model.sensor;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SensorDevice implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("QK")
    @Expose
    private List<String> qK = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<String> getQK() {
        return qK;
    }

    public void setQK(List<String> qK) {
        this.qK = qK;
    }

}
