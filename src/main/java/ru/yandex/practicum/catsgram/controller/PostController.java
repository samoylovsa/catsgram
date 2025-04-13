package ru.yandex.practicum.catsgram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.model.Post;
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
    public Collection<Post> getPost(
            @PathVariable(required = false) Long id,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size
    ) {
        if (id != null) {
            return Collections.singletonList(postService.findById(id));
        }

        String actualSort = (sort == null) ? "desc" : sort;
        int actualFrom = (from == null) ? 0 : from;
        int actualSize = (size == null) ? 10 : size;

        if (!"asc".equalsIgnoreCase(actualSort) && !"desc".equalsIgnoreCase(actualSort)) {
            throw new ConditionsNotMetException("Параметр sort должен быть 'asc' или 'desc'");
        }

        if (actualFrom < 0) {
            throw new ConditionsNotMetException("Параметр from не может быть отрицательным");
        }

        if (actualSize <= 0) {
            throw new ConditionsNotMetException("Параметр size должен быть больше нуля");
        }

        return postService.findAll(actualSort, actualFrom, actualSize);
    }

    @PostMapping("/posts")
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping("/posts")
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }
}