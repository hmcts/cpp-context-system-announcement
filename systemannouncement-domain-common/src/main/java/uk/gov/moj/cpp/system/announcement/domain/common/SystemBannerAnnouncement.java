package uk.gov.moj.cpp.system.announcement.domain.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = SystemBannerAnnouncement.Builder.class)
public class SystemBannerAnnouncement extends BaseAnnouncement {

    private SystemBannerAnnouncement(Builder builder) {
        super(builder);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder extends BaseAnnouncement.Builder<Builder> {

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public SystemBannerAnnouncement build() {
            return new SystemBannerAnnouncement(this);
        }
    }

}