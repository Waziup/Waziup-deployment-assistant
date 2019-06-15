
package eu.waziup.app.data.network.model.devices;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sensor {

    @SerializedName("quantity_kind")
    @Expose
    private String quantityKind;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("sensor_kind")
    @Expose
    private String sensorKind;

    public String getQuantityKind() {
        return quantityKind;
    }

    public void setQuantityKind(String quantityKind) {
        this.quantityKind = quantityKind;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSensorKind() {
        return sensorKind;
    }

    public void setSensorKind(String sensorKind) {
        this.sensorKind = sensorKind;
    }

}
