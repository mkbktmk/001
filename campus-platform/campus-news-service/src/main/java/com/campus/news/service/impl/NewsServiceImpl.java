package com.campus.news.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.common.exception.BizException;
import com.campus.news.entity.News;
import com.campus.news.mapper.NewsMapper;
import com.campus.news.service.NewsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {

    @Override
    public Page<News> pageList(int pageNum, int pageSize, String category) {
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<News>()
                .eq(News::getStatus, "published")
                .eq(category != null && !category.isBlank(), News::getCategory, category)
                .orderByDesc(News::getIsTop)
                .orderByDesc(News::getPublishTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public News getDetail(Long id) {
        News news = getById(id);
        if (news == null) throw new BizException(404, "资讯不存在");
        news.setViewCount(news.getViewCount() + 1);
        updateById(news);
        return news;
    }

    @Override
    public void publish(News news, Long authorId, String authorName) {
        news.setAuthorId(authorId);
        news.setAuthorName(authorName);
        news.setStatus("published");
        news.setPublishTime(LocalDateTime.now());
        news.setViewCount(0);
        save(news);
    }

    @Override
    public void updateNews(News news, Long userId, String role) {
        News exist = getById(news.getId());
        if (exist == null) throw new BizException(404, "资讯不存在");
        // 仅作者或管理员可编辑
        if (!"admin".equals(role) && !exist.getAuthorId().equals(userId)) {
            throw new BizException(403, "只能编辑自己发布的资讯");
        }
        exist.setTitle(news.getTitle());
        exist.setContent(news.getContent());
        exist.setSummary(news.getSummary());
        exist.setCategory(news.getCategory());
        exist.setCoverImage(news.getCoverImage());
        updateById(exist);
    }

    @Override
    public void offline(Long id, Long userId, String role) {
        News news = getById(id);
        if (news == null) throw new BizException(404, "资讯不存在");
        // 仅作者或管理员可下架
        if (!"admin".equals(role) && !news.getAuthorId().equals(userId)) {
            throw new BizException(403, "只能下架自己发布的资讯");
        }
        news.setStatus("offline");
        updateById(news);
    }
}
