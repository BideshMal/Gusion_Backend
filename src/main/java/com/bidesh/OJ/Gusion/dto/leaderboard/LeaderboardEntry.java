package com.bidesh.OJ.Gusion.dto.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeaderboardEntry {
    private String email;
    private Long problemsSolved;
}