package com.example.backend.dto.dashboardDTOS;

public class AdminDashboardStatsDTO {

    private long totalLawyers;
    private long activeLawyers;
    private long inactiveLawyers;

    private long totalJuniors;
    private long activeJuniors;

    private long totalClients;
    private long activeClients;

    private long totalResearchers;

    private long newSignupsThisMonth;

    // --- Getters and Setters for all fields ---

    public long getTotalLawyers() { return totalLawyers; }
    public void setTotalLawyers(long totalLawyers) { this.totalLawyers = totalLawyers; }
    public long getActiveLawyers() { return activeLawyers; }
    public void setActiveLawyers(long activeLawyers) { this.activeLawyers = activeLawyers; }
    public long getInactiveLawyers() { return inactiveLawyers; }
    public void setInactiveLawyers(long inactiveLawyers) { this.inactiveLawyers = inactiveLawyers; }
    public long getTotalJuniors() { return totalJuniors; }
    public void setTotalJuniors(long totalJuniors) { this.totalJuniors = totalJuniors; }
    public long getActiveJuniors() { return activeJuniors; }
    public void setActiveJuniors(long activeJuniors) { this.activeJuniors = activeJuniors; }
    public long getTotalClients() { return totalClients; }
    public void setTotalClients(long totalClients) { this.totalClients = totalClients; }
    public long getActiveClients() { return activeClients; }
    public void setActiveClients(long activeClients) { this.activeClients = activeClients; }
    public long getTotalResearchers() { return totalResearchers; }
    public void setTotalResearchers(long totalResearchers) { this.totalResearchers = totalResearchers; }
    public long getNewSignupsThisMonth() { return newSignupsThisMonth; }
    public void setNewSignupsThisMonth(long newSignupsThisMonth) { this.newSignupsThisMonth = newSignupsThisMonth; }
}
