package ru.yandex.practicum.catsgram.dto.post;

import lombok.Data;

@Data
public class UpdatePostRequest {
    private Long id;
    private Long authorId;
    private String description;

    public boolean hasAuthorId() {
        return ! (authorId == null);
    }

    public boolean hasDescription() {
        return ! (description == null || description.isBlank());
    }
}