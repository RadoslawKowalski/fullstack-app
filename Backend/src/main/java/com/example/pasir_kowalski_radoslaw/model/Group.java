package com.example.pasir_kowalski_radoslaw.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "`groups`") // 'group' to słowo kluczowe w SQL!
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Nazwa grupy

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner; // Właściciel grupy (może zapraszać, usuwać)

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Membership> memberships; // Lista członkostw w grupie
} 