package uk.gov.moj.cpp.system.announcement.domain.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.UUID;

@JsonDeserialize(builder = SystemAnnouncement.Builder.class)
public class SystemAnnouncement extends BaseAnnouncement {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("createdBy")
    private String createdBy;

    public UUID id() {
        return id;
    }

    public String createdBy() {
        return createdBy;
    }

    private SystemAnnouncement(Builder builder) {
        super(builder);
        this.id = builder.id;
        this.createdBy = builder.createdBy;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder extends BaseAnnouncement.Builder<Builder> {
        private UUID id;
        private String createdBy;

        public Builder id(UUID id) {
            this.id = id;
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
        public SystemAnnouncement build() {
            return new SystemAnnouncement(this);
        }
    }
}