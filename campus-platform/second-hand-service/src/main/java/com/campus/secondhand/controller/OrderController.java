package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.common.constant.Constants;
import com.campus.common.result.Result;
import com.campus.secondhand.entity.Goods;
import com.campus.secondhand.entity.Order;
import com.campus.secondhand.mapper.GoodsMapper;
import com.campus.secondhand.mapper.OrderMapper;
import com.campus.secondhand.service.GoodsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "订单接口")
@RestController
@RequestMapping("/second-hand/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderMapper orderMapper;
    private final GoodsMapper goodsMapper;
    private final GoodsService goodsService;

    @Operation(summary = "创建订单（购买）")
    @PostMapping
    public Result<Order> create(
            @RequestBody Order order,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        Goods goods = goodsMapper.selectById(order.getGoodsId());
        if (goods == null) return Result.fail("商品不存在");
        if (!"active".equals(goods.getStatus())) return Result.fail("商品已下架或已售");

        order.setBuyerId(userId);
        order.setGoodsTitle(goods.getTitle());
        order.setAmount(goods.getPrice());
        order.setSellerName(goods.getSellerName());
        order.setStatus("paid");
        orderMapper.insert(order);

        // 标记商品为已售
        goodsService.changeStatus(goods.getId(), userId, "sold");

        return Result.ok(order);
    }

    @Operation(summary = "我的购买记录")
    @GetMapping("/my")
    public Result<List<Order>> myOrders(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        List<Order> orders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getBuyerId, userId)
                        .orderByDesc(Order::getCreateTime));
        return Result.ok(orders);
    }
}
