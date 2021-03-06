
package eu.waziup.app.data.network.model.sensor;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Device extends eu.waziup.app.data.network.model.devices.Device implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("gateway_id")
    @Expose
    private String gatewayId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("owner")
    @Expose
    private String owner;
    @SerializedName("sensors")
    @Expose
    private List<eu.waziup.app.data.network.model.devices.Sensor> sensors = null;
    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("domain")
    @Expose
    private String domain;
    @SerializedName("date_created")
    @Expose
    private String dateCreated;
    @SerializedName("date_updated")
    @Expose
    private String dateUpdated;
    @SerializedName("visibility")
    @Expose
    private String visibility;

    public Device(String id, String name, String domain, String visibility) {
        this.id = id;
        this.name = name;
        this.domain = domain;
        this.visibility = visibility;
    }



    public Device(String id, String name, String domain, String visibility, Location location) {//, String gatewayId
        this.id = id;
        this.name = name;
        this.domain = domain;
        this.visibility = visibility;
        this.location = location;
//        this.gatewayId = gatewayId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<eu.waziup.app.data.network.model.devices.Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<eu.waziup.app.data.network.model.devices.Sensor> sensors) {
        this.sensors = sensors;
    }

    public Location getLocation() {
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

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

}
