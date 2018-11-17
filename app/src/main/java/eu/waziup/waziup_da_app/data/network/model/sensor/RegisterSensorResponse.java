
package eu.waziup.waziup_da_app.data.network.model.sensor;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterSensorResponse implements Serializable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("owner")
    @Expose
    private Owner owner;
    @SerializedName("ownerManagedAccess")
    @Expose
    private Boolean ownerManagedAccess;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("uris")
    @Expose
    private List<String> uris = null;
    @SerializedName("resource_scopes")
    @Expose
    private List<ResourceScope> resourceScopes = null;
    @SerializedName("scopes")
    @Expose
    private List<Scope> scopes = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Boolean getOwnerManagedAccess() {
        return ownerManagedAccess;
    }

    public void setOwnerManagedAccess(Boolean ownerManagedAccess) {
        this.ownerManagedAccess = ownerManagedAccess;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getUris() {
        return uris;
    }

    public void setUris(List<String> uris) {
        this.uris = uris;
    }

    public List<ResourceScope> getResourceScopes() {
        return resourceScopes;
    }

    public void setResourceScopes(List<ResourceScope> resourceScopes) {
        this.resourceScopes = resourceScopes;
    }

    public List<Scope> getScopes() {
        return scopes;
    }

    public void setScopes(List<Scope> scopes) {
        this.scopes = scopes;
    }

}
