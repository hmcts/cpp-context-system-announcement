package uk.gov.moj.cpp.system.announcement.domain.common;

import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = UpdateSystemAnnouncement.Builder.class)
public class UpdateSystemAnnouncement extends BaseAnnouncement {

    private UUID systemAnnouncementId;

    private String createdBy;

    public UUID systemAnnouncementId() {
        return systemAnnouncementId;
    }

    public String createdBy() {
        return createdBy;
    }

    private UpdateSystemAnnouncement(Builder builder) {
        super(builder);
        this.systemAnnouncementId = builder.systemAnnouncementId;
        this.createdBy = builder.createdBy;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder extends BaseAnnouncement.Builder<Builder> {
        private UUID systemAnnouncementId;
        private String createdBy;

        public Builder systemAnnouncementId(UUID systemAnnouncementId) {
            this.systemAnnouncementId = systemAnnouncementId;
            return self();
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return self();
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public UpdateSystemAnnouncement build() {
            return new UpdateSystemAnnouncement(this);
        }
    }
}