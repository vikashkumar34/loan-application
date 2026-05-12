package com.loanapp.dto;

import java.time.LocalDate;

public class UserProfileUpdateRequest {
    private String fullName;
    private String email;
    private String profileImagePath;
    private String parentName;
    private String address;
    private String mobileNumber;
    private String gender;
    private LocalDate dateOfBirth;
    private String bankAccountNumber;
    private String maritalStatus;
    private String nomineeName;
    private String nomineeRelationship;
    private String jobStatus;
    private String religion;
    private String panCard;
    private String aadharNumber;

    // Getters and setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getProfileImagePath() { return profileImagePath; }
    public void setProfileImagePath(String profileImagePath) { this.profileImagePath = profileImagePath; }
    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }
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
