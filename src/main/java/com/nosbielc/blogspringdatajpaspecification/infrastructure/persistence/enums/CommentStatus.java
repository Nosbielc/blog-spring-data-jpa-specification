package com.nosbielc.blogspringdatajpaspecification.infrastructure.persistence.enums;

public enum CommentStatus {
    PENDING_APPROVAL("Pending Approval"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    SPAM("Spam"),
    DELETED("Deleted");

    private String displayName;

    CommentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
