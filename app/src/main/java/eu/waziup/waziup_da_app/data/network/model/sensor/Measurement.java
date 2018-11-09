
package eu.waziup.waziup_da_app.data.network.model.sensor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Measurement {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("sensing_device")
    @Expose
    private String sensingDevice;
    @SerializedName("quantity_kind")
    @Expose
    private String quantityKind;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("last_value")
    @Expose
    private LastValue lastValue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSensingDevice() {
        return sensingDevice;
    }

    public void setSensingDevice(String sensingDevice) {
        this.sensingDevice = sensingDevice;
    }

    public String getQuantityKind() {
        return quantityKind;
    }

    public void setQuantityKind(String quantityKind) {
        this.quantityKind = quantityKind;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public LastValue getLastValue() {
        return lastValue;
    }

    public void setLastValue(LastValue lastValue) {
        this.lastValue = lastValue;
    }

}
