package com.shelly.ai.api;

import com.shelly.ai.common.Result;
import org.springframework.ai.document.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Rag相关接口
 * @author shelly
 */
public interface IRagService {
    /**
     * 上传文件并存储文档
     *
     * @param ragTag 用于标识文档的标签
     * @param files 要上传的文件列表
     * @return 上传成功的结果
     */
    Result<Void> uploadFile(String ragTag, List<MultipartFile> files);

    /**
     * 查看指定标签下的文档
     *
     * @param ragTag 用于过滤文档的标签
     * @return 标签对应的文档列表
     */
    Result<List<Document>> viewDocuments(String ragTag);

    /**
     * 删除指定标签下的文档
     *
     * @param ragTag 用于标识需要删除文档的标签
     * @return 删除操作结果
     */
    Result<Void> deleteDocuments(String ragTag);

    /**
     * 获取所有标签列表
     *
     * @return 存储在系统中的所有标签
     */
    Result<List<String>> getTagList();
}
