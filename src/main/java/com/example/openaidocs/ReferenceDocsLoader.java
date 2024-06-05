package com.example.openaidocs;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class ReferenceDocsLoader {
    private static final Logger logger = LoggerFactory.getLogger(ReferenceDocsLoader.class);
    private final JdbcClient jdbcClientl;
    private final VectorStore vectorStore;
    @Value("classpath:/docs/spring-boot-reference.pdf")
    private Resource pdfResource;
    @Autowired
    public ReferenceDocsLoader(JdbcClient jdbcClientl, VectorStore vectorStore) {
        this.jdbcClientl = jdbcClientl;
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    public void init() {
        Integer count = jdbcClientl.sql("select count(*) from vector_store").query(Integer.class).single();

        logger.info("Current count of the vector store: {}", count);
        if (count == 0) {
            logger.info("Loading Spring Boot Reference PDF into vector Store");
            var config = PdfDocumentReaderConfig.builder()
                    .withPageExtractedTextFormatter(
                            new ExtractedTextFormatter.Builder()
                                    .withNumberOfBottomTextLinesToDelete(0)
                                    .withNumberOfTopTextLinesToDelete(0)
                                    .build()
                    ).withPagesPerDocument(1).build();
            var pdfReader = new PagePdfDocumentReader(pdfResource, config);
            var textSplitter = new TokenTextSplitter();
            vectorStore.accept(textSplitter.apply(pdfReader.get()));

            logger.info("Application is ready");
        }
    }
}
