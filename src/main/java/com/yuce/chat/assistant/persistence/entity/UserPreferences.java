package com.yuce.chat.assistant.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "USER_PREFERENCES")
@EqualsAndHashCode
@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String category;

    public UserPreferences(String category) {
        this.category = category;
    }
}
