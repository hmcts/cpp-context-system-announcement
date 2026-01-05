package uk.gov.moj.cpp.system.announcement.domain.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalTime;

public abstract class BaseAnnouncement {

    @JsonProperty("category")
    private Category category;
    @JsonProperty("type")
    private Type type;
    @JsonProperty("startDate")
    private LocalDate startDate;
    @JsonProperty("endDate")
    private LocalDate endDate;
    @JsonProperty("startTime")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @JsonProperty("endTime")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    @JsonProperty("title")
    private String title;
    @JsonProperty("details")
    private String details;

    public Category category() {
        return category;
    }

    public Type type() {
        return type;
    }

    public LocalDate startDate() {
        return startDate;
    }

    public LocalDate endDate() {
        return endDate;
    }

    public LocalTime startTime() {
        return startTime;
    }

    public LocalTime endTime() {
        return endTime;
    }

    public String title() {
        return title;
    }

    public String details() {
        return details;
    }

    protected BaseAnnouncement(Builder<?> builder) {
        this.category = builder.category;
        this.type = builder.type;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.title = builder.title;
        this.details = builder.details;
    }

    public static abstract class Builder<T extends Builder<T>> {
        private Category category;
        private Type type;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private String title;
        private String details;

        public T category(Category category) {
            this.category = category;
            return self();
        }

        public T type(Type type) {
            this.type = type;
            return self();
        }

        public T startDate(LocalDate startDate) {
            this.startDate = startDate;
            return self();
        }

        public T endDate(LocalDate endDate) {
            this.endDate = endDate;
            return self();
        }

        public T startTime(LocalTime startTime) {
            this.startTime = startTime;
            return self();
        }

        public T endTime(LocalTime endTime) {
            this.endTime = endTime;
            return self();
        }

        public T title(String title) {
            this.title = title;
            return self();
        }

        public T details(String details) {
            this.details = details;
            return self();
        }

        protected abstract T self();

        public abstract BaseAnnouncement build();
    }
}