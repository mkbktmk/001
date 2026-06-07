package com.campus.lostfound.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_lost_found")
public class LostFound {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** lost / found */
    private String type;

    private String itemName;
    private String category;
    private String description;
    private String images;       // JSON 数组字符串
    private String location;
    private String contact;

    /** active / found */
    private String status;

    private Long userId;
    private String userName;
    private Integer viewCount;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
