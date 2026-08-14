package com.zhixiang.ai.mapper;

import com.zhixiang.ai.entity.KnowledgeDoc;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface KnowledgeDocMapper {
    List<KnowledgeDoc> selectAll();
    int insert(KnowledgeDoc doc);
    @org.apache.ibatis.annotations.Select("SELECT * FROM knowledge_doc WHERE category = #{category}")
    List<KnowledgeDoc> selectByCategory(@Param("category") String category);
}
