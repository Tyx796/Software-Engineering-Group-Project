package com.bupt.tarecruit.model;

public class AdminJobSupervisionView {
    private Job job;
    private User organiser;
    private int acceptedCount;
    private int remainingSlots;
    private boolean full;
    private long pendingCount;
    private long reviewingCount;
    private long acceptedStatusCount;
    private long rejectedCount;
    private long withdrawnCount;
    private long cancelledCount;

    public Job getJob() { return job; }
    public void setJob(final Job job) { this.job = job; }
    public User getOrganiser() { return organiser; }
    public void setOrganiser(final User organiser) { this.organiser = organiser; }
    public int getAcceptedCount() { return acceptedCount; }
    public void setAcceptedCount(final int acceptedCount) { this.acceptedCount = acceptedCount; }
    public int getRemainingSlots() { return remainingSlots; }
    public void setRemainingSlots(final int remainingSlots) { this.remainingSlots = remainingSlots; }
    public boolean isFull() { return full; }
    public void setFull(final boolean full) { this.full = full; }
    public long getPendingCount() { return pendingCount; }
    public void setPendingCount(final long pendingCount) { this.pendingCount = pendingCount; }
    public long getReviewingCount() { return reviewingCount; }
    public void setReviewingCount(final long reviewingCount) { this.reviewingCount = reviewingCount; }
    public long getAcceptedStatusCount() { return acceptedStatusCount; }
    public void setAcceptedStatusCount(final long acceptedStatusCount) { this.acceptedStatusCount = acceptedStatusCount; }
    public long getRejectedCount() { return rejectedCount; }
    public void setRejectedCount(final long rejectedCount) { this.rejectedCount = rejectedCount; }
    public long getWithdrawnCount() { return withdrawnCount; }
    public void setWithdrawnCount(final long withdrawnCount) { this.withdrawnCount = withdrawnCount; }
    public long getCancelledCount() { return cancelledCount; }
    public void setCancelledCount(final long cancelledCount) { this.cancelledCount = cancelledCount; }

    public boolean isAcceptedOverQuota() {
        return job != null && acceptedCount > job.getAssistantQuota();
    }

    public boolean hasUnexpectedPendingOrReviewingWhenFull() {
        return full && (pendingCount > 0 || reviewingCount > 0);
    }
}
