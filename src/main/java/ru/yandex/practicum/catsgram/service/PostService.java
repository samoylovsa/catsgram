package ru.yandex.practicum.catsgram.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.dal.PostRepository;
import ru.yandex.practicum.catsgram.dto.post.NewPostRequest;
import ru.yandex.practicum.catsgram.dto.post.PostDto;
import ru.yandex.practicum.catsgram.dto.post.UpdatePostRequest;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.mapper.PostMapper;
import ru.yandex.practicum.catsgram.model.Post;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Collection<PostDto> getPosts(String sort, Integer from, Integer size) {
        List<Post> postsList = postRepository.findAll();

        if (postsList.isEmpty()) {
            return Collections.emptyList();
        }

        if (sort.equalsIgnoreCase("asc")) {
            postsList.sort(Comparator.comparing(Post::getPostDate));
        } else if (sort.equalsIgnoreCase("desc")) {
            postsList.sort(Comparator.comparing(Post::getPostDate).reversed());
        }

        int start = from != null ? Math.max(from, 0) : 0;
        int availableSize = size != null ? Math.max(size, 0) : postsList.size();
        int end = Math.min(start + availableSize, postsList.size());

        if (start >= postsList.size() || start >= end) {
            return Collections.emptyList();
        }

        return postsList.subList(start, end).stream()
                .map(PostMapper::mapToPostDto)
                .collect(Collectors.toList());
    }

    public PostDto getPostById(Long postId) {
        return postRepository.findById(postId)
                .map(PostMapper::mapToPostDto)
                .orElseThrow(() -> new NotFoundException("Не найден пост с postId: " + postId));
    }

    public PostDto create(NewPostRequest request) {
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }
        Post post = PostMapper.mapToPost(request);
        post = postRepository.save(post);

        return PostMapper.mapToPostDto(post);
    }

    public PostDto update(UpdatePostRequest request) {
        if (request.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        Post updatedPost = postRepository.findById(request.getId())
                .map(post -> PostMapper.updatePostFields(post, request))
                .orElseThrow(() -> new NotFoundException("Пост с id = " + request.getId() + " не найден"));

        updatedPost = postRepository.update(updatedPost);

        return PostMapper.mapToPostDto(updatedPost);
    }
}