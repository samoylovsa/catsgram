package ru.yandex.practicum.catsgram.dto.post;

import lombok.Data;

import java.time.Instant;

@Data
public class NewPostRequest {
    private Long authorId;
    private String description;
    private Instant postDate;
}