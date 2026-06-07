package com.campus.secondhand.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_order")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long buyerId;
    private Long goodsId;
    private String goodsTitle;
    private BigDecimal amount;
    private String sellerName;
    private String status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
