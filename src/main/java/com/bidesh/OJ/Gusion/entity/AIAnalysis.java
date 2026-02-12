package com.bidesh.OJ.Gusion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ai_analysis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    @Column(name = "semantic_feedback", columnDefinition = "TEXT")
    private String semanticFeedback;

    @Column(name = "complexity")
    private String complexity;

    @Column(name = "suggestion", columnDefinition = "TEXT")
    private String suggestion;

    // 🟢 ADD THESE THREE FIELDS TO FIX THE ERRORS IN AnalysisServiceImpl
    @Column(columnDefinition = "TEXT")
    private String hintLevel1;

    @Column(columnDefinition = "TEXT")
    private String hintLevel2;

    @Column(columnDefinition = "TEXT")
    private String hintLevel3;
}
