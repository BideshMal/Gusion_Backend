// package com.bidesh.OJ.Gusion.repository;

// import java.util.List;
// import java.util.UUID;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

// import com.bidesh.OJ.Gusion.entity.Submission;

// @Repository
// public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
//     List<Submission> findByUserId(UUID userId);
//     List<Submission> findByProblemId(Long problemId);
// }

package com.bidesh.OJ.Gusion.repository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bidesh.OJ.Gusion.dto.leaderboard.LeaderboardEntry;
import com.bidesh.OJ.Gusion.entity.Submission;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
    
    List<Submission> findByUserId(UUID userId);
    List<Submission> findByProblemId(Long problemId);

    // ✅ NEW: Sorts history by newest first
    List<Submission> findByUserIdOrderBySubmittedAtDesc(UUID userId);

    // ✅ NEW: Calculates Leaderboard (Users with most Unique AC submissions)
    @Query("SELECT new com.bidesh.OJ.Gusion.dto.leaderboard.LeaderboardEntry(u.email, COUNT(DISTINCT s.problem.id)) " +
           "FROM Submission s JOIN s.user u " +
           "WHERE s.verdict = 'AC' " +
           "GROUP BY u.email " +
           "ORDER BY COUNT(DISTINCT s.problem.id) DESC")
    List<LeaderboardEntry> findTopUsers();
    Optional<Submission> findFirstByProblemIdAndUserIdOrderBySubmittedAtDesc(Long problemId, UUID userId);
}
