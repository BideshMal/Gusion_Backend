package com.bidesh.OJ.Gusion.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "problems")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @Builder.Default
    @Column(name = "cpu_limit_ms")
    private Integer cpuLimitMs = 1000;

    @Builder.Default
    @Column(name = "memory_limit_kb")
    private Integer memoryLimitKb = 256000;

    @Column(name = "starter_code", columnDefinition = "TEXT")
    private String starterCode;

    // 🟢 FIXED: This will now map correctly to your DB
    @Column(name = "reference_solution", columnDefinition = "TEXT")
    private String referenceSolution;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @JsonManagedReference
    private List<TestCase> testCases = new ArrayList<>();
}
//public class Problem {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, unique = true)
//    private String title;
//
//    @Column(nullable = false, unique = true)
//    private String slug;
//
//    @Column(columnDefinition = "TEXT", nullable = false)
//    private String description;
//
//    // 🛑 FIX 1: Use the actual Difficulty Enum (not String)
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private Difficulty difficulty;
//
//    // 🛑 FIX 2: Add @Builder.Default to stop the "initializing expression" warnings
//    @Builder.Default
//    @Column(name = "cpu_limit_ms")
//    private Integer cpuLimitMs = 1000;
//
//    // 🛑 FIX 2: Add @Builder.Default to stop the "initializing expression" warnings
//    @Builder.Default
//    @Column(name = "memory_limit_kb")
//    private Integer memoryLimitKb = 256000;
//
//    @Column(name = "starter_code", columnDefinition = "TEXT")
//    private String starterCode;
//
//    // 🛑 FIX 3: Keep the Infinite Loop protections (Exclude + JsonIgnore)
//    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
//    @Builder.Default
//    @ToString.Exclude
//    @com.fasterxml.jackson.annotation.JsonManagedReference // 🟢 ADD THIS
//    private List<TestCase> testCases = new ArrayList<>();
//
//    @Column(columnDefinition = "TEXT")
//    private String referenceSolution;
//}
