package com.zhixiang.ai;

import com.zhixiang.ai.config.AiProperties;
import com.zhixiang.ai.entity.KnowledgeDoc;
import com.zhixiang.ai.mapper.KnowledgeDocMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 轻量级 RAG 知识库：从 knowledge_doc 表加载 SOP/客诉处理规则，按分类拼装上下文。
 */
@Service
public class RagKnowledgeService {

    private final KnowledgeDocMapper knowledgeDocMapper;
    private final AiProperties aiProperties;

    public RagKnowledgeService(KnowledgeDocMapper knowledgeDocMapper, AiProperties aiProperties) {
        this.knowledgeDocMapper = knowledgeDocMapper;
        this.aiProperties = aiProperties;
    }

    public String buildContext() {
        String mode = aiProperties.getKnowledgeMode();
        List<KnowledgeDoc> docs;
        if ("ALL".equalsIgnoreCase(mode)) {
            docs = knowledgeDocMapper.selectAll();
        } else {
            docs = knowledgeDocMapper.selectByCategory(mode);
        }
        if (docs.isEmpty()) return "";
        StringBuilder sb = new StringBuilder("【知识库参考】\n");
        for (KnowledgeDoc d : docs) {
            sb.append("## ").append(d.getTitle()).append("\n").append(d.getContent()).append("\n\n");
        }
        return sb.toString();
    }

    public List<KnowledgeDoc> all() {
        return knowledgeDocMapper.selectAll();
    }

    public void add(KnowledgeDoc doc) {
        knowledgeDocMapper.insert(doc);
    }
}
