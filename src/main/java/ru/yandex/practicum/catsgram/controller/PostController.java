package ru.yandex.practicum.catsgram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.dto.post.NewPostRequest;
import ru.yandex.practicum.catsgram.dto.post.PostDto;
import ru.yandex.practicum.catsgram.dto.post.UpdatePostRequest;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;
import java.util.Collections;

@RestController
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping({"/posts", "/posts/{id}"})
    public Collection<PostDto> getPost(
            @PathVariable(required = false) Long id,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size
    ) {
        if (id != null) {
            return Collections.singletonList(postService.getPostById(id));
        }

        String actualSort = (sort == null) ? "desc" : sort;
        int actualFrom = (from == null) ? 0 : from;
        int actualSize = (size == null) ? 10 : size;

        if (!"asc".equalsIgnoreCase(actualSort) && !"desc".equalsIgnoreCase(actualSort)) {
            throw new ParameterNotValidException(sort,
                    "Параметр sort должен быть 'asc' или 'desc'");
        }

        if (actualFrom < 0) {
            throw new ParameterNotValidException(String.valueOf(from),
                    "Параметр from не может быть отрицательным");
        }

        if (actualSize <= 0) {
            throw new ParameterNotValidException(String.valueOf(size),
                    "Некорректный размер выборки. Размер должен быть больше нуля");
        }

        return postService.getPosts(actualSort, actualFrom, actualSize);
    }

    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public PostDto create(@RequestBody NewPostRequest request) {
        return postService.create(request);
    }

    @PutMapping("/posts")
    public PostDto update(@RequestBody UpdatePostRequest request) {
        return postService.update(request);
    }
}