package com.znz.tpip_backend.model;


// import com.znz.tpip_backend.enums.District;
// import com.znz.tpip_backend.enums.Region;
import com.znz.tpip_backend.enums.RelationshipType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Embeddable
public class NextOfKin {

    private String kinFullName;

    @Enumerated(EnumType.STRING)
    private RelationshipType kinRelationship;

    private String kinPhoneNumber;

    // private String kinAlternativePhone;

    // private String kinEmail;

    // private Region kinRegion;
    // private District kinDistrict;
    // private String kinWard;
    // private String kinStreet;

    // private String kinOccupation;
}