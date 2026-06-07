package com.campus.secondhand.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_second_hand")
public class Goods {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String description;
    private String category;
    private String images;       // JSON 数组
    private BigDecimal price;
    private BigDecimal originalPrice;
    @TableField("goods_condition")
    private String goodsCondition;    // new / like_new / good / fair

    /** active / sold / removed */
    private String status;

    private Long sellerId;
    private String sellerName;
    private String contact;

    private Integer viewCount;
    private Integer favCount;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
