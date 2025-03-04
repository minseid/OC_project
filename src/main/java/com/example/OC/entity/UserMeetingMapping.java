package com.example.OC.entity;


import jakarta.persistence.*;
import lombok.*;

@Table(name="Mapping")
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UserMeetingMapping {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Meeting meeting;
}
