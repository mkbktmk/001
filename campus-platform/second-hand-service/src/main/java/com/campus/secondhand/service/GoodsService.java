package com.campus.secondhand.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.secondhand.entity.Goods;

import java.math.BigDecimal;

public interface GoodsService extends IService<Goods> {

    /** 分页搜索 */
    Page<Goods> pageList(int pageNum, int pageSize, String category, BigDecimal minPrice, BigDecimal maxPrice, String keyword, String sortBy);

    /** 商品详情 */
    Goods getDetail(Long id);

    /** 发布商品 */
    void publish(Goods goods, Long sellerId, String sellerName);

    /** 标记已售/下架 */
    void changeStatus(Long id, Long sellerId, String status);

    /** 收藏/取消收藏 */
    boolean toggleFavorite(Long goodsId, Long userId);

    /** 查询是否已收藏 */
    boolean isFavorited(Long goodsId, Long userId);

    /** 获取用户收藏的商品列表 */
    java.util.List<Goods> getMyFavorites(Long userId);

    /** 分页查询用户自己发布的商品 */
    Page<Goods> myGoods(int pageNum, int pageSize, Long userId);

    /** 编辑商品（仅卖家可操作） */
    void updateGoods(Goods goods, Long userId);
}
