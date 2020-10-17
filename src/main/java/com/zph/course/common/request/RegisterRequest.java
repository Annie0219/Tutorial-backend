package com.zph.course.common.request;

import lombok.Data;

/**
 * @author cheney
 */
@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String gender;
    private String password;
    private String role;
    private String verification;
}
