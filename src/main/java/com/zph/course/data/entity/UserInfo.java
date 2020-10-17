package com.zph.course.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.context.annotation.Primary;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author evan
 * @since 2020-04-07
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String password;

    private String email;

    private String name;

    private Integer age;

    private String gender;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime lastLogin;

    private String avatar;

    private Boolean state;

    private String role;

    private Boolean isGuardian;

    private Long courseHour;
}
