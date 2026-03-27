package com.marketplace.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String university;
    private String avatar;
    private Double rating;
    private boolean verified;
    private String createdAt;
}
