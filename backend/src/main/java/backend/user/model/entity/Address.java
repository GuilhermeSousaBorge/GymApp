package backend.user.model.entity;

import backend.user.model.interfaces.AddressUpdatable;
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

    public Long getId() {
        return id;
    }

    public Integer getNumber() {
        return number;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getDistrict() {
        return district;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public void updateFrom(AddressUpdatable request) {
        if (request.getNumber() != null) this.number = request.getNumber();
        if (request.getZipCode() != null) this.zipCode = request.getZipCode();
        if (request.getDistrict() != null) this.district = request.getDistrict();
        if (request.getStreetName() != null) this.streetName = request.getStreetName();
        if (request.getCity() != null) this.city = request.getCity();
        if (request.getState() != null) this.state = request.getState();
    }
}
