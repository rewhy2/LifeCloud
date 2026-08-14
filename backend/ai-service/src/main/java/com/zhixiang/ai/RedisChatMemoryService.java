package com.zhixiang.ai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.data.message.UserMessage;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 基于 Redis 的多轮对话记忆（按 userId 维度存储），支持快速查询与隔离。
 */
@Service
public class RedisChatMemoryService {

    private static final int MAX_MESSAGES = 20;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisChatMemoryService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String key(Long userId) {
        return "chat:memory:" + (userId == null ? "anonymous" : userId);
    }

    public List<ChatMessage> getMessages(Long userId) {
        String raw = redisTemplate.opsForValue().get(key(userId));
        if (raw == null) return new ArrayList<>();
        try {
            List<String> jsonList = objectMapper.readValue(raw, new TypeReference<List<String>>() {});
            List<ChatMessage> messages = new ArrayList<>();
            for (String json : jsonList) {
                messages.add(ChatMessageDeserializer.messageFromJson(json));
            }
            return messages;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void add(Long userId, ChatMessage message) {
        List<ChatMessage> messages = getMessages(userId);
        messages.add(message);
        if (messages.size() > MAX_MESSAGES) {
            messages = new LinkedList<>(messages).subList(messages.size() - MAX_MESSAGES, messages.size());
        }
        try {
            List<String> jsonList = new ArrayList<>();
            for (ChatMessage m : messages) {
                jsonList.add(ChatMessageSerializer.messageToJson(m));
            }
            redisTemplate.opsForValue().set(key(userId), objectMapper.writeValueAsString(jsonList));
        } catch (Exception ignored) {
        }
    }

    public void clear(Long userId) {
        redisTemplate.delete(key(userId));
    }
}
