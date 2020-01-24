package com.trading.forex.payload;

import com.trading.forex.entity.Address;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class SignUpRequest {

    @NotBlank
    @Size(max = 20)
    private String firstName;

    @NotBlank
    @Size(max = 20)
    private String lastName;

    @NotBlank
    @Size(min = 4, max = 15)
    private String username;

    @NotBlank
    @Size(min = 6, max = 30)
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 20)
    private String password;

    @NotBlank
    @Size(max = 10)
    private String phone;

    private Date dob;

    @NotBlank
    @Size(max = 3)
    private String currencyType;

    private List<Address> address;

}
