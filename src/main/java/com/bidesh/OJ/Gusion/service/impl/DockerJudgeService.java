package com.bidesh.OJ.Gusion.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.bidesh.OJ.Gusion.entity.Submission;
import com.bidesh.OJ.Gusion.entity.TestCase;
import com.bidesh.OJ.Gusion.entity.Verdict;
import com.bidesh.OJ.Gusion.repository.TestCaseRepository; // ✅ Ensure this exists
import com.bidesh.OJ.Gusion.service.JudgeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DockerJudgeService implements JudgeService {

    private final TestCaseRepository testCaseRepository;

    @Override
    public Verdict judge(Submission submission) {
        log.info("Judging submission {} (Lang: {})", submission.getId(), submission.getLanguage());
        Path workDir = null;
        try {
            workDir = setupWorkDir(submission);
            
            // Fetch Test Cases
            List<TestCase> testCases = testCaseRepository.findByProblemId(submission.getProblem().getId());
            if (testCases.isEmpty()) {
                log.warn("No test cases found for problem {}", submission.getProblem().getId());
                return Verdict.AC; // Or Error, depending on your logic
            }

            for (TestCase tc : testCases) {
                // Execute code with Test Case Input
                String output = executeDocker(workDir, submission, tc.getInput());
                
                // Check for Runtime Errors
                if (output.startsWith("Error:")) {
                    return Verdict.RE; 
                }
                
                // Compare Output
                String expected = tc.getExpectedOutput().trim();
                String actual = output.replace("\r\n", "\n").trim();
                
                if (!actual.equals(expected.replace("\r\n", "\n"))) {
                    return Verdict.WA;
                }
            }
            return Verdict.AC;
        } catch (Exception e) {
            log.error("Judge Error", e);
            return Verdict.RE;
        } finally {
            cleanup(workDir);
        }
    }

    @Override
    public String runRaw(Submission submission, String input) {
        log.info("Running raw execution for submission {}", submission.getId());
        Path workDir = null;
        try {
            workDir = setupWorkDir(submission);
            return executeDocker(workDir, submission, input);
        } catch (Exception e) {
            log.error("RunRaw Error", e);
            return "System Error: " + e.getMessage();
        } finally {
            cleanup(workDir);
        }
    }

    // --- SHARED CORE ---

    private String executeDocker(Path workDir, Submission submission, String input) throws IOException, InterruptedException {
        List<String> command = buildDockerCommand(workDir, submission);
        
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true); // Merge stderr into stdout to capture errors
        Process process = pb.start();

        // Write Input to Docker (stdin)
        if (input != null && !input.isBlank()) {
            try (var os = process.getOutputStream()) {
                os.write(input.getBytes());
                os.flush();
            } catch (IOException e) { 
                // Ignore if container closed stream early
            }
        }

        // Wait for execution (5 seconds max)
        boolean finished = process.waitFor(15000, TimeUnit.MILLISECONDS);
        if (!finished) {
            process.destroyForcibly();
            return "Error: Time Limit Exceeded";
        }

        String output = new String(process.getInputStream().readAllBytes());
        if (process.exitValue() != 0) {
            return "Error: Runtime Error (Exit Code " + process.exitValue() + ")\n" + output;
        }

        return output;
    }

    private Path setupWorkDir(Submission sub) throws IOException {
        Path projectDir = Path.of(System.getProperty("user.dir")); 
        Path tempRoot = projectDir.resolve("temp_judge");
        if (!Files.exists(tempRoot)) Files.createDirectories(tempRoot);
        
        Path workDir = Files.createTempDirectory(tempRoot, "sub_" + sub.getId() + "_");
        
        // Define Filename based on Language
        String fileName = "solution.txt";
        if (sub.getLanguage().toString().equalsIgnoreCase("JAVA")) fileName = "Main.java";
        else if (sub.getLanguage().toString().equalsIgnoreCase("PYTHON")) fileName = "solution.py";

        Files.writeString(workDir.resolve(fileName), sub.getCode(), StandardOpenOption.CREATE);
        return workDir;
    }

    private List<String> buildDockerCommand(Path workDir, Submission sub) {
        // ✅ CRITICAL FIX: Match the image you actually pulled!
        String image = "eclipse-temurin:17-jdk-alpine"; 
        
        if (sub.getLanguage().toString().equalsIgnoreCase("PYTHON")) {
            image = "python:3.9-alpine";
        }

        String runCmd = "echo 'Unsupported'";
        if (sub.getLanguage().toString().equalsIgnoreCase("JAVA")) {
            // Compile then Run (Single file source code mode)
            runCmd = "javac Main.java && java Main"; 
        } else if (sub.getLanguage().toString().equalsIgnoreCase("PYTHON")) {
            runCmd = "python3 solution.py";
        }

        // Fix Windows Paths for Docker Volume
        String hostPath = workDir.toAbsolutePath().toString().replace("\\", "/"); 
        if (hostPath.startsWith("C:")) { // Docker on Windows formatting
             hostPath = "/" + hostPath.toLowerCase().replace(":", "");
        }

        // Construct Docker Command
        List<String> cmd = new ArrayList<>();
        cmd.add("docker");
        cmd.add("run");
        cmd.add("--rm");
        cmd.add("-i"); // Interactive mode to accept input
        cmd.add("--network");
        cmd.add("none");
        cmd.add("--memory");
        cmd.add("512m");
        cmd.add("-v");
        cmd.add(hostPath + ":/app"); // Mount host folder to /app container folder
        cmd.add("-w");
        cmd.add("/app"); // Set working directory inside container
        cmd.add(image);
        cmd.add("sh");
        cmd.add("-c");
        cmd.add(runCmd);
        
        return cmd;
    }

    private void cleanup(Path workDir) {
        if (workDir != null) {
            try (Stream<Path> walk = Files.walk(workDir)) {
                walk.sorted((a, b) -> b.compareTo(a)) // Delete files first, then dir
                    .forEach(p -> {
                        try { Files.delete(p); } catch (IOException e) {}
                    });
            } catch (IOException e) {}
        }
    }
}