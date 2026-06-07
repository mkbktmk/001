package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.constant.Constants;
import com.campus.common.result.Result;
import com.campus.secondhand.entity.Goods;
import com.campus.secondhand.entity.Message;
import com.campus.secondhand.mapper.GoodsMapper;
import com.campus.secondhand.mapper.MessageMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Tag(name = "私聊接口")
@RestController
@RequestMapping("/second-hand/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageMapper messageMapper;
    private final GoodsMapper goodsMapper;

    @Operation(summary = "发送消息")
    @PostMapping
    public Result<Message> send(
            @RequestBody Message msg,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @RequestHeader(value = Constants.HEADER_NICKNAME, required = false) String nickname,
            @RequestHeader(Constants.HEADER_USERNAME) String username) {
        String displayName = (nickname != null && !nickname.isBlank())
                ? URLDecoder.decode(nickname, StandardCharsets.UTF_8) : username;
        msg.setSenderId(userId);
        msg.setSenderName(displayName);
        msg.setIsRead(0);
        messageMapper.insert(msg);
        return Result.ok(msg);
    }

    @Operation(summary = "对话列表（按商品分组）")
    @GetMapping("/conversations")
    public Result<List<Map<String, Object>>> conversations(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        // 查所有与我相关的消息，按 goods_id 分组取最新一条
        List<Message> all = messageMapper.selectList(
                new LambdaQueryWrapper<Message>()
                        .and(w -> w.eq(Message::getSenderId, userId).or().eq(Message::getReceiverId, userId))
                        .orderByDesc(Message::getCreateTime));
        Map<Long, Message> latest = new LinkedHashMap<>();
        for (Message m : all) {
            latest.putIfAbsent(m.getGoodsId(), m);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Message m : latest.values()) {
            Map<String, Object> conv = new HashMap<>();
            boolean iAmSender = m.getSenderId().equals(userId);
            Goods g = goodsMapper.selectById(m.getGoodsId());
            conv.put("goodsId", m.getGoodsId());
            conv.put("goodsTitle", g != null ? g.getTitle() : "");
            conv.put("lastMessage", m.getContent());
            conv.put("lastTime", m.getCreateTime());
            conv.put("withUser", iAmSender ? m.getReceiverId() : m.getSenderId());
            // 对方名称（解码可能被 URL 编码的旧数据）
            String senderName = m.getSenderName();
            try { senderName = URLDecoder.decode(senderName, StandardCharsets.UTF_8); } catch (Exception ignored) {}
            String otherName = senderName;
            if (iAmSender) {
                // 我是发送方，需要查对方名字。对方要么是卖家（若我不是卖家），要么是买家
                if (g != null && !g.getSellerId().equals(userId)) {
                    otherName = g.getSellerName(); // 我是买家，对方是卖家
                } else {
                    // 我是卖家，对方是买家，查对方发给我的消息中的 senderName
                    Message buyerMsg = messageMapper.selectOne(
                        new LambdaQueryWrapper<Message>()
                            .eq(Message::getGoodsId, m.getGoodsId())
                            .eq(Message::getSenderId, m.getReceiverId())
                            .orderByDesc(Message::getCreateTime)
                            .last("LIMIT 1"));
                    otherName = buyerMsg != null ? buyerMsg.getSenderName() : "买家";
                    try { otherName = URLDecoder.decode(otherName, StandardCharsets.UTF_8); } catch (Exception ignored) {}
                }
            }
            conv.put("withUserName", otherName);
            // 未读数
            long unread = messageMapper.selectCount(
                    new LambdaQueryWrapper<Message>()
                            .eq(Message::getGoodsId, m.getGoodsId())
                            .eq(Message::getReceiverId, userId)
                            .eq(Message::getIsRead, 0));
            conv.put("unread", (int) unread);
            result.add(conv);
        }
        return Result.ok(result);
    }

    @Operation(summary = "聊天记录")
    @GetMapping("/list")
    public Result<List<Message>> list(
            @RequestParam Long goodsId,
            @RequestParam Long withUserId,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        List<Message> msgs = messageMapper.selectList(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getGoodsId, goodsId)
                        .and(w -> w
                                .and(w2 -> w2.eq(Message::getSenderId, userId).eq(Message::getReceiverId, withUserId))
                                .or(w2 -> w2.eq(Message::getSenderId, withUserId).eq(Message::getReceiverId, userId)))
                        .orderByAsc(Message::getCreateTime));
        // 标记已读
        for (Message m : msgs) {
            if (m.getReceiverId().equals(userId) && m.getIsRead() == 0) {
                m.setIsRead(1);
                messageMapper.updateById(m);
            }
        }
        return Result.ok(msgs);
    }

    @Operation(summary = "未读消息总数")
    @GetMapping("/unread")
    public Result<Map<String, Integer>> unread(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        long count = messageMapper.selectCount(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getReceiverId, userId)
                        .eq(Message::getIsRead, 0));
        return Result.ok(Map.of("count", (int) count));
    }
}
