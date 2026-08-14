package com.zhixiang.ai;

import com.zhixiang.ai.tools.BusinessTools;
import com.zhixiang.ai.tools.OperationsTools;
import com.zhixiang.common.UserContext;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import org.springframework.stereotype.Service;

/**
 * AI 对话服务：基于 LangChain4j AiServices 自动装配系统提示、工具箱与对话记忆。
 */
@Service
public class AiChatService {

    private final ChatLanguageModel chatLanguageModel;
    private final RedisChatMemoryService chatMemoryService;
    private final RagKnowledgeService ragKnowledgeService;
    private final BusinessTools businessTools;
    private final OperationsTools operationsTools;

    public AiChatService(ChatLanguageModel chatLanguageModel,
                         RedisChatMemoryService chatMemoryService,
                         RagKnowledgeService ragKnowledgeService,
                         BusinessTools businessTools,
                         OperationsTools operationsTools) {
        this.chatLanguageModel = chatLanguageModel;
        this.chatMemoryService = chatMemoryService;
        this.ragKnowledgeService = ragKnowledgeService;
        this.businessTools = businessTools;
        this.operationsTools = operationsTools;
    }

    public interface Assistant {
        @SystemMessage("""
                你是「智享餐饮」连锁餐饮管理平台的 AI 智能运营助手，服务于店长与运营人员。
                你可以调用工具查询当日营收、分类销售、库存预警、会员、排班等，并基于知识库给出可执行建议。
                回答要简洁专业、用中文，必要时给出具体数字与下一步行动建议。
                """)
        String chat(String userMessage);
    }

    public String chat(String message) {
        Long userId = UserContext.get() == null ? null : UserContext.get().getUserId();
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel)
                .tools(businessTools, operationsTools)
                .chatMemory(new RedisBackedChatMemory(chatMemoryService, userId))
                .build();
        // 注入 RAG 知识库上下文：拼接在用户问题前
        String rag = ragKnowledgeService.buildContext();
        String fullMessage = (rag != null && !rag.isBlank()) ? (rag + "\n\n用户问题：" + message) : message;
        return assistant.chat(fullMessage);
    }

    public void clearMemory() {
        Long userId = UserContext.get() == null ? null : UserContext.get().getUserId();
        chatMemoryService.clear(userId);
    }
}
