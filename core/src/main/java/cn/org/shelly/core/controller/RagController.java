package cn.org.shelly.core.controller;

import cn.org.shelly.core.common.Result;
import cn.org.shelly.core.model.dto.InfoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Rag模块
 * @author shelly
 */
@RestController
@RequestMapping("/rag")
@Slf4j
@CrossOrigin("*")
@Tag(name = "Rag模块")
public class RagController {
    @Resource
    private TokenTextSplitter tokenTextSplitter;
    @Resource
    private PgVectorStore pgVectorStore;
    @Resource
    private org.redisson.api.RedissonClient redissonClient;
    @PostMapping(value = "file/upload", headers = "content-type=multipart/form-data")
    @Operation(summary = "上传知识库")
    public Result<Void> uploadFile(String ragTag, List<MultipartFile> files) {
        log.info("上传知识库开始 {}", ragTag);
        for (MultipartFile file : files) {
            TikaDocumentReader documentReader = new TikaDocumentReader(file.getResource());
            List<Document> documents = documentReader.get();
            List<Document> documentSplitterList = tokenTextSplitter.apply(documents);

            documents.forEach(doc -> doc.getMetadata().put("knowledge", ragTag));
            documentSplitterList.forEach(doc -> doc.getMetadata().put("knowledge", ragTag));

            pgVectorStore.accept(documentSplitterList);

            RList<String> elements = redissonClient.getList("ragTag");
            if (!elements.contains(ragTag)){
                elements.add(ragTag);
            }
        }

        log.info("上传知识库完成 {}", ragTag);
        return Result.success();
    }
    @PostMapping("/upload")
    @Operation(summary = "单个上传知识库")
    public Result<Void> upload(@RequestBody InfoDTO infoDTO) {
        log.info("上传知识库开始 {}", infoDTO.getName());
        Document document = Document.builder()
                .text(infoDTO.getValue())
                .metadata(Map.of("knowledge", "shelly", "name", infoDTO.getName()))
                .build();
        List<Document> documentSplitterList = tokenTextSplitter.split(document);
        pgVectorStore.accept(documentSplitterList);
        log.info("上传知识库完成 {}", infoDTO.getName());
        return Result.success();
    }
    @GetMapping
    @Operation(summary = "查看知识库")
    public Result<List<Document>> viewDocuments(@RequestParam String ragTag) {
        SearchRequest request = SearchRequest.builder().query("*")
                .topK(10)
                .filterExpression("knowledge == '" + ragTag + "'")
                .build();
        List<Document> documents = pgVectorStore.similaritySearch(request);
        return Result.success(documents);
    }
    @DeleteMapping
    @Operation(summary = "删除知识库")
    public Result<Void> deleteDocuments(@RequestParam String ragTag) {
        log.info("删除知识库开始 {}", ragTag);
        pgVectorStore.delete("knowledge == '" + ragTag + "'");
        // 删除对应的RagTag
        RList<String> elements = redissonClient.getList("ragTag");
        elements.remove(ragTag);
        log.info("删除知识库完成 {}", ragTag);
        return Result.success();
    }

    @GetMapping("/tagList")
    @Operation(summary = "获取RagTag列表")
    public Result<List<String>> getTagList(){
        RList<String> elements = redissonClient.getList("ragTag");
        return Result.success(elements);
    }
}
