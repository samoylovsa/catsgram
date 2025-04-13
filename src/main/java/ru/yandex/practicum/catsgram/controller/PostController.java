package ru.yandex.practicum.catsgram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;

@RestController
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping({"/posts", "/posts/{id}"})
    public Collection<Post> getPost(@PathVariable(required = false) Long id) {
        return id != null ? postService.findById(id) : postService.findAll();
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