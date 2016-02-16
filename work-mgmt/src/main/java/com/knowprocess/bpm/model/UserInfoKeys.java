package com.knowprocess.bpm.model;

import java.io.Serializable;

public enum UserInfoKeys implements Serializable {

    COMMS_PREFERENCE, OMNY_BAR_POSITION, PHONE, TENANT;

    public String toString() {
        return name().toLowerCase();
    }
}
