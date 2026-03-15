package backend.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddressUpdateRequest {

    private Integer number;

    private String zipCode;

    private String district;

    private String streetName;

    private String city;

    private String state;

}
