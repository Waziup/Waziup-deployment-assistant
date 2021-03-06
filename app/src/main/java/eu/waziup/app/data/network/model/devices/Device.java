
package eu.waziup.app.data.network.model.devices;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import me.aflak.filter_annotation.Filterable;

@Filterable
public class Device implements Serializable {

    @SerializedName("deployed")
    @Expose
    private Boolean deployed;
    @SerializedName("gateway_id")
    @Expose
    private String gatewayId;
    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("domain")
    @Expose
    private String domain;
    @SerializedName("owner")
    @Expose
    private String owner;
    @SerializedName("visibility")
    @Expose
    private String visibility;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("sensors")
    @Expose
    private List<Sensor> sensors = null;
    @SerializedName("actuators")
    @Expose
    private List<Actuator> actuators = null;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Boolean getDeployed() {
        return deployed;
    }

    public void setDeployed(Boolean deployed) {
        this.deployed = deployed;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public eu.waziup.app.data.network.model.sensor.Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
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

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    public List<Actuator> getActuators() {
        return actuators;
    }

    public void setActuators(List<Actuator> actuators) {
        this.actuators = actuators;
    }

}
