package com.bidesh.OJ.Gusion.dto.submission;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitResponse {
    private UUID submissionId;
    private String status;
    private String verdict;
    private Long executionTime; // Ensures constructor accepts (UUID, String, String, Long, Integer)
    private Integer memoryUsed; 
}