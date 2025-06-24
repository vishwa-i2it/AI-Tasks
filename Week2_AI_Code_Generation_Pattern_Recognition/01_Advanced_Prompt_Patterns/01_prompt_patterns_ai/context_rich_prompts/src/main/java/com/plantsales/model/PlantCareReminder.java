package com.plantsales.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "plant_care_reminders")
public class PlantCareReminder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant;
    
    @NotNull(message = "Reminder type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "reminder_type", nullable = false)
    private ReminderType reminderType;
    
    @Column(name = "plant_name")
    private String plantName;
    
    @Column(name = "frequency_days", nullable = false)
    private Integer frequencyDays;
    
    @Column(name = "next_reminder_date", nullable = false)
    private LocalDateTime nextReminderDate;
    
    @Column(name = "last_reminder_date")
    private LocalDateTime lastReminderDate;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enums
    public enum ReminderType {
        WATERING, FERTILIZING, PRUNING, REPOTTING, PEST_CHECK, GENERAL_CARE
    }
    
    // Constructors
    public PlantCareReminder() {}
    
    public PlantCareReminder(User user, Plant plant, ReminderType reminderType, Integer frequencyDays) {
        this.user = user;
        this.plant = plant;
        this.reminderType = reminderType;
        this.frequencyDays = frequencyDays;
        this.plantName = plant.getName();
        this.nextReminderDate = LocalDateTime.now().plusDays(frequencyDays);
        this.createdAt = LocalDateTime.now();
    }
    
    // Pre-persist and pre-update methods
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Business methods
    public boolean isDue() {
        return LocalDateTime.now().isAfter(nextReminderDate) || LocalDateTime.now().isEqual(nextReminderDate);
    }
    
    public void markAsCompleted() {
        this.lastReminderDate = LocalDateTime.now();
        this.nextReminderDate = LocalDateTime.now().plusDays(frequencyDays);
    }
    
    public void skipReminder() {
        this.nextReminderDate = LocalDateTime.now().plusDays(frequencyDays);
    }
    
    public String getReminderMessage() {
        switch (reminderType) {
            case WATERING:
                return "Time to water your " + plantName + "!";
            case FERTILIZING:
                return "Time to fertilize your " + plantName + "!";
            case PRUNING:
                return "Time to prune your " + plantName + "!";
            case REPOTTING:
                return "Time to repot your " + plantName + "!";
            case PEST_CHECK:
                return "Time to check your " + plantName + " for pests!";
            case GENERAL_CARE:
                return "Time for general care of your " + plantName + "!";
            default:
                return "Care reminder for your " + plantName + "!";
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Plant getPlant() { return plant; }
    public void setPlant(Plant plant) { this.plant = plant; }
    
    public ReminderType getReminderType() { return reminderType; }
    public void setReminderType(ReminderType reminderType) { this.reminderType = reminderType; }
    
    public String getPlantName() { return plantName; }
    public void setPlantName(String plantName) { this.plantName = plantName; }
    
    public Integer getFrequencyDays() { return frequencyDays; }
    public void setFrequencyDays(Integer frequencyDays) { this.frequencyDays = frequencyDays; }
    
    public LocalDateTime getNextReminderDate() { return nextReminderDate; }
    public void setNextReminderDate(LocalDateTime nextReminderDate) { this.nextReminderDate = nextReminderDate; }
    
    public LocalDateTime getLastReminderDate() { return lastReminderDate; }
    public void setLastReminderDate(LocalDateTime lastReminderDate) { this.lastReminderDate = lastReminderDate; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
} 