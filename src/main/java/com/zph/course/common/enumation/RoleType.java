package com.zph.course.common.enumation;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author cheney
 */

@AllArgsConstructor
@NoArgsConstructor
public enum RoleType {
    // 学生
    STUDENT("Student"),
    GUARDIAN("Guardian"),
    // 教师
    TEACHER("Teacher");

    private String role;

    public String getRole() {
        return role;
    }
}
