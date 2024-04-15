package me.imhojun.springbootdeveloper.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.imhojun.springbootdeveloper.domain.Article;
import me.imhojun.springbootdeveloper.dto.AddArticleRequest;
import me.imhojun.springbootdeveloper.dto.UpdateArticleRequest;
import me.imhojun.springbootdeveloper.repository.BlogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BlogService {

    private final BlogRepository blogRepository;

    public Article save(AddArticleRequest request) {
        return blogRepository.save(request.toEntity());
    }

    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    public Article findById(long id) {
        return blogRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    public void delete(Long id){
        blogRepository.deleteById(id);
    }

    @Transactional
    public Article update(Long id, UpdateArticleRequest request){
        Article article = blogRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 게시글이 없습니다. id=" + id));

        article.update(request.getTitle(), request.getContent());

        return article;
    }


}
