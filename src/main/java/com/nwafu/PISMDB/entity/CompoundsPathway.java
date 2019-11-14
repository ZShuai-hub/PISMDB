package com.nwafu.PISMDB.entity;

import lombok.Data;

@Data
public class CompoundsPathway {
    private String pismid;
    private String pathwayID;
    private String pathwayName;

    @Override
    public String toString() {
        return "CompoundsPathway{" +
                "PISMID='" + pismid + '\'' +
                ", PathwayID='" + pathwayID + '\'' +
                '}';
    }
}
