package com.orderflow.auth.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserProfileResponseDTO {
        private Long userId;
        private String fullName;
        private String email;
        private String role;
        private Boolean active;
}
