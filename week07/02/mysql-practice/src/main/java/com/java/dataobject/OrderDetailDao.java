package com.java.dataobject;

import java.math.BigDecimal;
import java.util.Date;

public class OrderDetailDao extends OrderDetailDaoKey {
    private String cmmdtyCode;

    private String cmmdtyName;

    private String externalEanCode;

    private BigDecimal normalPrice;

    private BigDecimal salePrice;

    private BigDecimal quantity;

    private BigDecimal saleValue;

    private BigDecimal discount;

    private Date createDate;

    private String createBy;

    private String lastModifiedBy;

    private Date lastModified;

    private Boolean isDeleted;

    public String getCmmdtyCode() {
        return cmmdtyCode;
    }

    public void setCmmdtyCode(String cmmdtyCode) {
        this.cmmdtyCode = cmmdtyCode == null ? null : cmmdtyCode.trim();
    }

    public String getCmmdtyName() {
        return cmmdtyName;
    }

    public void setCmmdtyName(String cmmdtyName) {
        this.cmmdtyName = cmmdtyName == null ? null : cmmdtyName.trim();
    }

    public String getExternalEanCode() {
        return externalEanCode;
    }

    public void setExternalEanCode(String externalEanCode) {
        this.externalEanCode = externalEanCode == null ? null : externalEanCode.trim();
    }

    public BigDecimal getNormalPrice() {
        return normalPrice;
    }

    public void setNormalPrice(BigDecimal normalPrice) {
        this.normalPrice = normalPrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSaleValue() {
        return saleValue;
    }

    public void setSaleValue(BigDecimal saleValue) {
        this.saleValue = saleValue;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy == null ? null : lastModifiedBy.trim();
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}