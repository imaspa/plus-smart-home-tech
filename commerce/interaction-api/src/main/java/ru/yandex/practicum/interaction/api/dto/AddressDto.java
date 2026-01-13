package ru.yandex.practicum.interaction.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDto {
    private UUID addressId;

    @Size(max = 20)
    private String country;

    @Size(max = 30)
    private String city;

    @NotBlank
    @Size(max = 50)
    private String street;

    @Size(max = 10)
    private String house;

    @Size(max = 10)
    private String flat;
}
