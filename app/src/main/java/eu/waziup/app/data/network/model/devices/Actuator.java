
package eu.waziup.app.data.network.model.devices;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Actuator {

    @SerializedName("actuator_value_type")
    @Expose
    private String actuatorValueType;
    @SerializedName("value")
    @Expose
    private Boolean value;
    @SerializedName("actuator_kind")
    @Expose
    private String actuatorKind;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id")
    @Expose
    private String id;

    public String getActuatorValueType() {
        return actuatorValueType;
    }

    public void setActuatorValueType(String actuatorValueType) {
        this.actuatorValueType = actuatorValueType;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    public String getActuatorKind() {
        return actuatorKind;
    }

    public void setActuatorKind(String actuatorKind) {
        this.actuatorKind = actuatorKind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
