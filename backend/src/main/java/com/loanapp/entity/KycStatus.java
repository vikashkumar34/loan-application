package com.loanapp.entity;

/**
 * Represents the status of a user's KYC (Know Your Customer) verification.
 */
public enum KycStatus {
    /**
     * The user has not yet submitted their KYC documents.
     */
    PENDING,

    /**
     * The user's KYC documents have been submitted and are awaiting review.
     */
    SUBMITTED,

    /**
     * The user's KYC has been successfully verified.
     */
    VERIFIED,

    /**
     * The user's KYC verification was rejected.
     */
    REJECTED
}
