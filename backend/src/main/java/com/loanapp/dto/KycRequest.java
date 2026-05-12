package com.loanapp.dto;

import org.springframework.web.multipart.MultipartFile;

public class KycRequest {
    private String panCardNumber;
    private String accountNumber;
    private String pincode;
    private String mobileNumber;
    private String documentType;
    private MultipartFile document;

    // Getters and setters
    public String getPanCardNumber() { return panCardNumber; }
    public void setPanCardNumber(String panCardNumber) { this.panCardNumber = panCardNumber; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public MultipartFile getDocument() { return document; }
    public void setDocument(MultipartFile document) { this.document = document; }
}
