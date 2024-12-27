package com.amaropticals.model;

public class ReconModel {


    private long productId;

    private String productCode;

    private String reconDateTime;

    private int reconQty;
    private int displayQty;

    private  int diff;

    public int getDiff() {
        return diff;
    }

    public void setDiff(int diff) {
        this.diff = diff;
    }

    public int getReconQty() {
        return reconQty;
    }

    public void setReconQty(int reconQty) {
        this.reconQty = reconQty;
    }

    public int getDisplayQty() {
        return displayQty;
    }

    public void setDisplayQty(int displayQty) {
        this.displayQty = displayQty;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getReconDateTime() {
        return reconDateTime;
    }

    public void setReconDateTime(String reconDateTime) {
        this.reconDateTime = reconDateTime;
    }
}
