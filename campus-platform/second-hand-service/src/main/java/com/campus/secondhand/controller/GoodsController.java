package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.constant.Constants;
import com.campus.common.result.Result;
import com.campus.secondhand.entity.Goods;
import com.campus.secondhand.service.GoodsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "二手交易接口")
@RestController
@RequestMapping("/second-hand")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;

    @Operation(summary = "商品列表")
    @GetMapping("/list")
    public Result<Page<Goods>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "latest") String sortBy) {
        return Result.ok(goodsService.pageList(page, size, category, minPrice, maxPrice, keyword, sortBy));
    }

    @Operation(summary = "商品详情")
    @GetMapping("/detail/{id}")
    public Result<Goods> detail(@PathVariable Long id) {
        return Result.ok(goodsService.getDetail(id));
    }

    @Operation(summary = "发布商品")
    @PostMapping
    public Result<Void> publish(
            @RequestBody Goods goods,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @RequestHeader(Constants.HEADER_USERNAME) String username) {
        goodsService.publish(goods, userId, username);
        return Result.ok("发布成功", null);
    }

    @Operation(summary = "标记已售/下架")
    @PutMapping("/status/{id}")
    public Result<Void> markSold(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        goodsService.changeStatus(id, userId, status);
        return Result.ok("更新成功", null);
    }

    @Operation(summary = "收藏/取消收藏")
    @PostMapping("/favorite/{id}")
    public Result<Boolean> favorite(
            @PathVariable Long id,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return Result.ok(goodsService.toggleFavorite(id, userId));
    }

    @Operation(summary = "查询是否已收藏")
    @GetMapping("/favorite/{id}/status")
    public Result<Boolean> isFavorited(
            @PathVariable Long id,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return Result.ok(goodsService.isFavorited(id, userId));
    }

    @Operation(summary = "我的收藏列表")
    @GetMapping("/favorites")
    public Result<List<Goods>> myFavorites(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return Result.ok(goodsService.getMyFavorites(userId));
    }

    @Operation(summary = "编辑商品（仅卖家）")
    @PutMapping("/{id}")
    public Result<Void> update(
            @PathVariable Long id,
            @RequestBody Goods goods,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        goods.setId(id);
        goodsService.updateGoods(goods, userId);
        return Result.ok("修改成功", null);
    }

    @Operation(summary = "我的发布列表")
    @GetMapping("/my-list")
    public Result<Page<Goods>> myList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return Result.ok(goodsService.myGoods(page, size, userId));
    }
}
