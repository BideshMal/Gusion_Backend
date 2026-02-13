package com.bidesh.OJ.Gusion.service;

import java.util.List;
import java.util.UUID;

import com.bidesh.OJ.Gusion.dto.submission.HistoryResponse;
import com.bidesh.OJ.Gusion.dto.submission.StatusResponse;
import com.bidesh.OJ.Gusion.dto.submission.SubmitRequest;
import com.bidesh.OJ.Gusion.dto.submission.SubmitResponse;

public interface SubmissionService {
    SubmitResponse submit(UUID userId, SubmitRequest request);
    StatusResponse getStatus(UUID submissionId);
    List<HistoryResponse> getUserHistory(UUID userId);
    String getLastSavedCode(Long problemId, UUID userId);
}
