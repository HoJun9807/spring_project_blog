package me.imhojun.springbootdeveloper.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import me.imhojun.springbootdeveloper.domain.Article;
import me.imhojun.springbootdeveloper.dto.AddArticleRequest;
import me.imhojun.springbootdeveloper.dto.UpdateArticleRequest;
import me.imhojun.springbootdeveloper.repository.BlogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class BlogApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    BlogRepository blogRepository;

    @BeforeEach
    public void MockMvcSetUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .build();
        blogRepository.deleteAll();
    }

    @DisplayName("게시글 작성 테스트")
    @Test
    public void addArticle() throws Exception {
        // given
        final String url = "/api/articles";
        final String title = "제목";
        final String content = "내용";
        final AddArticleRequest userRequest = new AddArticleRequest(title,content);

        final String requestBody = objectMapper.writeValueAsString(userRequest);

        // when
        ResultActions result = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(requestBody));

        // then
        result.andExpect(status().isCreated());

        List<Article> articles = blogRepository.findAll();
        assertThat(articles.size()).isEqualTo(1);
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        assertThat(articles.get(0).getContent()).isEqualTo(content);

    }

    @DisplayName("게시글 조회 테스트")
    @Test
    public void findAllArticles() throws Exception {
        final String url = "/api/articles";
        final String title = "제목";
        final String content = "내용";

        blogRepository.save(Article.builder()
            .title(title)
            .content(content)
            .build());

        final ResultActions resultActions = mockMvc.perform(get(url)
            .accept(MediaType.APPLICATION_JSON));

        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value(title))
            .andExpect(jsonPath("$[0].content").value(content));
    }

    @DisplayName("게시글 수정 테스트")
    @Test
    public void updateArticle() throws Exception{
        final String url = "/api/articles/{id}";
        final String title = "제목";
        final String content = "내용";

        Article savedArticle = blogRepository.save(Article.builder()
            .title(title)
            .content(content)
            .build());

        final String newTitle = "수정된 제목";
        final String newContent = "수정된 내용";

        UpdateArticleRequest request = new UpdateArticleRequest(newTitle, newContent);

        ResultActions result = mockMvc.perform(put(url, savedArticle.getId())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk());

        Article articles = blogRepository.findById(savedArticle.getId()).get();
        assertThat(articles.getTitle()).isEqualTo(newTitle);
        assertThat(articles.getContent()).isEqualTo(newContent);
    }
}