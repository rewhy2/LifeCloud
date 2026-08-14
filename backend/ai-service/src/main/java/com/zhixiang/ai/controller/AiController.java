package com.zhixiang.ai.controller;

import com.zhixiang.ai.AiChatService;
import com.zhixiang.ai.RagKnowledgeService;
import com.zhixiang.ai.entity.KnowledgeDoc;
import com.zhixiang.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final AiChatService aiChatService;
    private final RagKnowledgeService ragKnowledgeService;

    public AiController(AiChatService aiChatService, RagKnowledgeService ragKnowledgeService) {
        this.aiChatService = aiChatService;
        this.ragKnowledgeService = ragKnowledgeService;
    }

    @PostMapping("/chat")
    public Result<Map<String, Object>> chat(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String message = body.get("message");
        if (message == null || message.isBlank()) {
            return Result.error("请输入问题");
        }
        String reply = aiChatService.chat(message);
        return Result.success(Map.of("reply", reply, "role", "assistant"));
    }

    @PostMapping("/clear")
    public Result<Void> clear() {
        aiChatService.clearMemory();
        return Result.success();
    }

    @GetMapping("/knowledge")
    public Result<List<KnowledgeDoc>> knowledge() {
        return Result.success(ragKnowledgeService.all());
    }

    @PostMapping("/knowledge")
    public Result<Void> addKnowledge(@RequestBody KnowledgeDoc doc) {
        ragKnowledgeService.add(doc);
        return Result.success();
    }
}
