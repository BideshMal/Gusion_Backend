package com.bidesh.OJ.Gusion.service;

import com.bidesh.OJ.Gusion.entity.Submission;
import com.bidesh.OJ.Gusion.entity.Verdict;

public interface JudgeService {
    // For "Submit" (Grading)
    Verdict judge(Submission submission);

    // ✅ NEW: For "Run" (Playground)
    // Returns the raw STDOUT string from the container
    String runRaw(Submission submission, String input);
}