package com._data._data.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "nations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Nation {
    @Id
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "name_ko", nullable = false, length = 100)
    private String nameKo;
}
