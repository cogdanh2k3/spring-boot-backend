package com.springboot.admin.dto;

public class BulkImportResult {
    private Integer total;
    private Integer success;
    private Integer failed;
    private String message;

    public BulkImportResult() {
    }

    public BulkImportResult(Integer total, Integer success, Integer failed, String message) {
        this.total = total;
        this.success = success;
        this.failed = failed;
        this.message = message;
    }

    // Getters and Setters
    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public Integer getFailed() {
        return failed;
    }

    public void setFailed(Integer failed) {
        this.failed = failed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
