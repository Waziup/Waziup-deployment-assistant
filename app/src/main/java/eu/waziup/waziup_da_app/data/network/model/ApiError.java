package eu.waziup.waziup_da_app.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApiError {

    private int errorCode;

    @Expose
    @SerializedName("error")
    private String error;

    @Expose
    @SerializedName("description")
    private String description;

    public ApiError() {
    }

    public ApiError(int errorCode, String statusCode, String message) {
        this.errorCode = errorCode;
        this.error = statusCode;
        this.description = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getStatusCode() {
        return error;
    }

    public void setStatusCode(String statusCode) {
        this.error = statusCode;
    }

    public String getMessage() {
        return description;
    }

    public void setMessage(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        ApiError apiError = (ApiError) object;

        if (errorCode != apiError.errorCode) return false;
        if (error != null ? !error.equals(apiError.error)
                : apiError.error != null)
            return false;
        return description != null ? description.equals(apiError.description) : apiError.description == null;

    }

    @Override
    public int hashCode() {
        int result = errorCode;
        result = 31 * result + (error != null ? error.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
