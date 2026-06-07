package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.common.exception.BizException;
import com.campus.secondhand.entity.Goods;
import com.campus.secondhand.mapper.GoodsMapper;
import com.campus.secondhand.service.GoodsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商品 Service — Redis Set 实现收藏
 *
 * <pre>
 * Redis 数据结构:
 *   goods:fav:{goodsId} → Set(userId)   收藏用户集合
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    private final StringRedisTemplate redis;

    // ──────── Redis Key ────────
    private static final String GOODS_FAV = "goods:fav:";

    private String favKey(Long goodsId) {
        return GOODS_FAV + goodsId;
    }

    // ──────────── 查询 ────────────

    @Override
    public Page<Goods> pageList(int pageNum, int pageSize, String category,
                                 BigDecimal minPrice, BigDecimal maxPrice,
                                 String keyword, String sortBy) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<Goods>()
                .eq(Goods::getStatus, "active")
                .eq(category != null && !category.isBlank(), Goods::getCategory, category)
                .ge(minPrice != null, Goods::getPrice, minPrice)
                .le(maxPrice != null, Goods::getPrice, maxPrice)
                .and(keyword != null && !keyword.isBlank(), q -> q
                        .like(Goods::getTitle, keyword)
                        .or()
                        .like(Goods::getDescription, keyword));

        if ("price_asc".equals(sortBy)) {
            wrapper.orderByAsc(Goods::getPrice);
        } else if ("price_desc".equals(sortBy)) {
            wrapper.orderByDesc(Goods::getPrice);
        } else {
            wrapper.orderByDesc(Goods::getCreateTime);
        }

        Page<Goods> result = page(new Page<>(pageNum, pageSize), wrapper);
        // 用 Redis 实时收藏数补齐
        result.getRecords().forEach(g -> {
            Long cnt = redis.opsForSet().size(favKey(g.getId()));
            if (cnt != null && cnt > 0) g.setFavCount(cnt.intValue());
        });
        return result;
    }

    @Override
    public Goods getDetail(Long id) {
        Goods goods = getById(id);
        if (goods == null) {
            throw new BizException(404, "商品不存在");
        }
        goods.setViewCount(goods.getViewCount() + 1);
        updateById(goods);

        Long cnt = redis.opsForSet().size(favKey(id));
        if (cnt != null) goods.setFavCount(cnt.intValue());
        return goods;
    }

    // ──────────── 写入 ────────────

    @Override
    public void publish(Goods goods, Long sellerId, String sellerName) {
        goods.setSellerId(sellerId);
        goods.setSellerName(sellerName);
        goods.setStatus("active");
        goods.setViewCount(0);
        goods.setFavCount(0);
        save(goods);
    }

    @Override
    public void changeStatus(Long id, Long sellerId, String status) {
        Goods goods = getById(id);
        if (goods == null) {
            throw new BizException(404, "商品不存在");
        }
        if (!goods.getSellerId().equals(sellerId)) {
            throw new BizException(403, "只能操作自己的商品");
        }
        goods.setStatus(status);
        updateById(goods);
    }

    // ──────────── 收藏（Redis Set）────────────

    @Override
    public boolean toggleFavorite(Long goodsId, Long userId) {
        Goods goods = getById(goodsId);
        if (goods == null || !"active".equals(goods.getStatus())) {
            throw new BizException(404, "商品不存在或已下架");
        }

        String key = favKey(goodsId);
        String member = userId.toString();
        Boolean isMember = redis.opsForSet().isMember(key, member);

        if (Boolean.TRUE.equals(isMember)) {
            redis.opsForSet().remove(key, member);
            // 更新 DB 计数
            Long cnt = redis.opsForSet().size(key);
            goods.setFavCount(cnt != null ? cnt.intValue() : 0);
            updateById(goods);
            return false;   // 已取消收藏
        } else {
            redis.opsForSet().add(key, member);
            Long cnt = redis.opsForSet().size(key);
            goods.setFavCount(cnt != null ? cnt.intValue() : 0);
            updateById(goods);
            return true;    // 已收藏
        }
    }

    @Override
    public boolean isFavorited(Long goodsId, Long userId) {
        return Boolean.TRUE.equals(
                redis.opsForSet().isMember(favKey(goodsId), userId.toString()));
    }

    @Override
    public List<Goods> getMyFavorites(Long userId) {
        // 扫描所有 goods:fav:* 的 key，找出包含该用户的
        Set<String> keys = redis.keys(GOODS_FAV + "*");
        if (keys == null || keys.isEmpty()) return List.of();

        String member = userId.toString();
        List<Long> goodsIds = new ArrayList<>();
        for (String key : keys) {
            if (Boolean.TRUE.equals(redis.opsForSet().isMember(key, member))) {
                try {
                    goodsIds.add(Long.parseLong(key.substring(GOODS_FAV.length())));
                } catch (NumberFormatException ignored) {}
            }
        }
        if (goodsIds.isEmpty()) return List.of();

        // 查询商品并按收藏时间倒序
        List<Goods> goodsList = listByIds(goodsIds);
        // 补齐 Redis 中的实时收藏数
        goodsList.forEach(g -> {
            Long cnt = redis.opsForSet().size(favKey(g.getId()));
            if (cnt != null) g.setFavCount(cnt.intValue());
        });
        return goodsList;
    }

    @Override
    public void updateGoods(Goods goods, Long userId) {
        Goods exist = getById(goods.getId());
        if (exist == null) throw new BizException(404, "商品不存在");
        if (!exist.getSellerId().equals(userId)) throw new BizException(403, "只能编辑自己的商品");

        // 只更新允许修改的字段
        exist.setTitle(goods.getTitle());
        exist.setDescription(goods.getDescription());
        exist.setCategory(goods.getCategory());
        exist.setPrice(goods.getPrice());
        exist.setOriginalPrice(goods.getOriginalPrice());
        exist.setGoodsCondition(goods.getGoodsCondition());
        exist.setContact(goods.getContact());
        if (goods.getImages() != null) exist.setImages(goods.getImages());
        // 编辑后保持原状态，不自动上架
        updateById(exist);
    }

    @Override
    public Page<Goods> myGoods(int pageNum, int pageSize, Long userId) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<Goods>()
                .eq(Goods::getSellerId, userId)
                .orderByDesc(Goods::getCreateTime);
        Page<Goods> result = page(new Page<>(pageNum, pageSize), wrapper);
        result.getRecords().forEach(g -> {
            Long cnt = redis.opsForSet().size(favKey(g.getId()));
            if (cnt != null) g.setFavCount(cnt.intValue());
        });
        return result;
    }
}
