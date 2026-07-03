package com.mindora.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreferencesRequest {
    private String language;
    private String[] favoriteMusicGenres;
    private String notificationFrequency;
    private Boolean darkMode;
    private Boolean emailNotifications;
    private Boolean pushNotifications;
}
