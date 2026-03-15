package backend.model.entity;

import backend.dto.request.user.AddressUpdateRequest;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer number;

    @Column(name = "zip_code", length = 10)
    private String zipCode;

    @Column(length = 100)
    private String district;

    @Column(name = "street_name", length = 150)
    private String streetName;

    @Column(length = 100)
    private String city;

    @Column(length = 2)  // Sigla do estado (MG, SP, etc)
    private String state;

    public void updateFrom(AddressUpdateRequest request) {
        if (request.getNumber() != null) this.number = request.getNumber();
        if (request.getZipCode() != null) this.zipCode = request.getZipCode();
        if (request.getDistrict() != null) this.district = request.getDistrict();
        if (request.getStreetName() != null) this.streetName = request.getStreetName();
        if (request.getCity() != null) this.city = request.getCity();
        if (request.getState() != null) this.state = request.getState();
    }
}
