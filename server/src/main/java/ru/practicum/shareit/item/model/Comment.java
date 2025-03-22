package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    private String text;

    private LocalDateTime created;

    @Column(name = "author_name")
    private String authorName;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
}
