package com.example.OC.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name="Mapping")
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserMeetingMappingEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private long meeting_id;

    @Column
    private long user_id;
}
