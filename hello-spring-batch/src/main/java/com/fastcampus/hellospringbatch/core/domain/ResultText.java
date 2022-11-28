package com.fastcampus.hellospringbatch.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "result_text")
@AllArgsConstructor
@NoArgsConstructor
public class ResultText {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false,name = "text")
    private String text;
}
