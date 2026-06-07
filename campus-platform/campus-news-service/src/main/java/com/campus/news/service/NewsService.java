package com.campus.news.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.news.entity.News;

public interface NewsService extends IService<News> {

    Page<News> pageList(int pageNum, int pageSize, String category);

    News getDetail(Long id);

    void publish(News news, Long authorId, String authorName);

    void updateNews(News news, Long userId, String role);

    void offline(Long id, Long userId, String role);
}
