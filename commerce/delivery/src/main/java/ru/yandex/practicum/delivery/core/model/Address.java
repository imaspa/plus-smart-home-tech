package ru.yandex.practicum.delivery.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Address {
    @Id
    @UuidGenerator
    private UUID addressId;

    @Column(length = 20)
    private String country;

    @Column(length = 30)
    private String city;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "Улица обязательна")
    private String street;

    @Column(length = 10)
    private String house;

    @Column(length = 10)
    private String flat;
}
