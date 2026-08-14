package com.zhixiang.ai;

import com.zhixiang.common.UserContext;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.ChatMemory;

import java.util.List;

/**
 * 基于 Redis 持久化的 LangChain4j ChatMemory 实现（按当前登录用户隔离）。
 */
public class RedisBackedChatMemory implements ChatMemory {

    private final RedisChatMemoryService redisChatMemoryService;
    private final Long userId;

    public RedisBackedChatMemory(RedisChatMemoryService redisChatMemoryService, Long userId) {
        this.redisChatMemoryService = redisChatMemoryService;
        this.userId = userId;
    }

    @Override
    public Object id() {
        return userId == null ? "anonymous" : userId;
    }

    @Override
    public void add(ChatMessage message) {
        redisChatMemoryService.add(userId, message);
    }

    @Override
    public List<ChatMessage> messages() {
        return redisChatMemoryService.getMessages(userId);
    }

    @Override
    public void clear() {
        redisChatMemoryService.clear(userId);
    }
}
