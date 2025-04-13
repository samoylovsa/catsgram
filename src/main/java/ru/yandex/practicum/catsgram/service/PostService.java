package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;
import java.util.*;

@Service
public class PostService {
    private final Map<Long, Post> posts = new HashMap<>();

    public Collection<Post> findAll(String sort, Integer from, Integer size) {
        List<Post> postsList = new ArrayList<>(posts.values());

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

        return new ArrayList<>(postsList.subList(start, end));
    }

    public Post findById(Long id) {
        if (!posts.containsKey(id)) {
            throw new NotFoundException("Не найден пост с id: " + id);
        }
        return posts.get(id);
    }

    public Post create(Post post) {
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}