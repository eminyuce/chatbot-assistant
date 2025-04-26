package com.yuce.chat.assistant.persistence.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "USER_PREFERENCES")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class UserPreferences {
         @Id
         @GeneratedValue(strategy = GenerationType.IDENTITY)
         Integer id;
         String category;
}
