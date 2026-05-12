package com.loanapp.dto;

import com.loanapp.entity.KycStatus;
import java.time.LocalDate;

public class ProfileResponse {
    private String username;
    private String fullName;
    private String parentName;
    private String email;
    private String address;
    private KycStatus kycStatus;
    private String mobileNumber;
    private String gender;
    private LocalDate dateOfBirth;
    private String bankAccountNumber;
    private String profileImagePath;
    private String maritalStatus;
    private String nomineeName;
    private String nomineeRelationship;
    private String jobStatus;
    private String religion;
    private String panCard;
    private String aadharNumber;

    // Constructor, getters, and setters
    public ProfileResponse(String username, String fullName, String parentName, String email, String address, KycStatus kycStatus, String mobileNumber, String gender, LocalDate dateOfBirth, String bankAccountNumber, String profileImagePath, String maritalStatus, String nomineeName, String nomineeRelationship, String jobStatus, String religion, String panCard, String aadharNumber) {
        this.username = username;
        this.fullName = fullName;
        this.parentName = parentName;
        this.email = email;
        this.address = address;
        this.kycStatus = kycStatus;
        this.mobileNumber = mobileNumber;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.bankAccountNumber = bankAccountNumber;
        this.profileImagePath = profileImagePath;
        this.maritalStatus = maritalStatus;
        this.nomineeName = nomineeName;
        this.nomineeRelationship = nomineeRelationship;
        this.jobStatus = jobStatus;
        this.religion = religion;
        this.panCard = panCard;
        this.aadharNumber = aadharNumber;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public KycStatus getKycStatus() { return kycStatus; }
    public void setKycStatus(KycStatus kycStatus) { this.kycStatus = kycStatus; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }
    public String getProfileImagePath() { return profileImagePath; }
    public void setProfileImagePath(String profileImagePath) { this.profileImagePath = profileImagePath; }
    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }
    public String getNomineeName() { return nomineeName; }
    public void setNomineeName(String nomineeName) { this.nomineeName = nomineeName; }
    public String getNomineeRelationship() { return nomineeRelationship; }
    public void setNomineeRelationship(String nomineeRelationship) { this.nomineeRelationship = nomineeRelationship; }
    public String getJobStatus() { return jobStatus; }
    public void setJobStatus(String jobStatus) { this.jobStatus = jobStatus; }
    public String getReligion() { return religion; }
    public void setReligion(String religion) { this.religion = religion; }
    public String getPanCard() { return panCard; }
    public void setPanCard(String panCard) { this.panCard = panCard; }
    public String getAadharNumber() { return aadharNumber; }
    public void setAadharNumber(String aadharNumber) { this.aadharNumber = aadharNumber; }
}
