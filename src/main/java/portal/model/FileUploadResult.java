package portal.model;

public class FileUploadResult {
    String error;
    String success;

    public FileUploadResult( String success,String error) {
        this.error = error;
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
}
