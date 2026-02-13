package com.bidesh.OJ.Gusion.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.bidesh.OJ.Gusion.entity.Submission;
import com.bidesh.OJ.Gusion.entity.TestCase;
import com.bidesh.OJ.Gusion.entity.Verdict;
import com.bidesh.OJ.Gusion.repository.TestCaseRepository;
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

            List<TestCase> testCases = testCaseRepository.findByProblemId(submission.getProblem().getId());
            if (testCases.isEmpty()) {
                log.warn("No test cases found for problem {}", submission.getProblem().getId());
                return Verdict.SE;
            }

            for (TestCase tc : testCases) {
                String output = executeNative(workDir, submission, tc.getInput());

                if (output.startsWith("Error: Time Limit Exceeded")) return Verdict.TLE;
                if (output.startsWith("Error:")) return Verdict.RE;

                String expected = tc.getExpectedOutput().trim();
                String actual = output.replace("\r\n", "\n").trim();

                if (!actual.equals(expected.replace("\r\n", "\n"))) {
                    log.info("Submission {} failed on test case ID: {}. Verdict: WA", submission.getId(), tc.getId());
                    return Verdict.WA;
                }
            }

            log.info("Submission {} passed all {} test cases.", submission.getId(), testCases.size());
            return Verdict.AC;

        } catch (Exception e) {
            log.error("Internal Judge Error for submission {}: ", submission.getId(), e);
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
            return executeNative(workDir, submission, input);
        } catch (Exception e) {
            log.error("RunRaw Error", e);
            return "System Error: " + e.getMessage();
        } finally {
            cleanup(workDir);
        }
    }

    // --- NATIVE EXECUTION (NO DOCKER!) ---

    private String executeNative(Path workDir, Submission submission, String input) throws IOException, InterruptedException {
        String lang = submission.getLanguage().toString().toUpperCase();

        // 1. Compile if Java
        if (lang.equals("JAVA")) {
            ProcessBuilder compilePb = new ProcessBuilder("javac", "Main.java");
            compilePb.directory(workDir.toFile());
            compilePb.redirectErrorStream(true);
            Process compileProcess = compilePb.start();
            
            if (!compileProcess.waitFor(10, TimeUnit.SECONDS) || compileProcess.exitValue() != 0) {
                String error = new String(compileProcess.getInputStream().readAllBytes());
                return "Error: Compilation Error\n" + error;
            }
        }

        // 2. Build Run Command
        List<String> command = new ArrayList<>();
        if (lang.equals("JAVA")) {
            command.add("java");
            command.add("Main");
        } else if (lang.equals("PYTHON")) {
            command.add("python3");
            command.add("solution.py");
        } else {
            return "Error: Unsupported Language";
        }

        // 3. Execute Code
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(workDir.toFile());
        pb.redirectErrorStream(true);
        Process process = pb.start();

        // 4. Pass Input
        if (input != null && !input.isBlank()) {
            try (var os = process.getOutputStream()) {
                os.write(input.getBytes());
                os.flush();
            } catch (IOException e) { /* Process closed early */ }
        }

        // 5. Read Output with Timeout
        boolean finished = process.waitFor(5000, TimeUnit.MILLISECONDS);
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

        String fileName = "solution.txt";
        if (sub.getLanguage().toString().equalsIgnoreCase("JAVA")) fileName = "Main.java";
        else if (sub.getLanguage().toString().equalsIgnoreCase("PYTHON")) fileName = "solution.py";

        Files.writeString(workDir.resolve(fileName), sub.getCode(), StandardOpenOption.CREATE);
        return workDir;
    }

    private void cleanup(Path workDir) {
        if (workDir != null) {
            try (Stream<Path> walk = Files.walk(workDir)) {
                walk.sorted((a, b) -> b.compareTo(a))
                        .forEach(p -> {
                            try { Files.delete(p); } catch (IOException e) {}
                        });
            } catch (IOException e) {}
        }
    }
}
// package com.bidesh.OJ.Gusion.service.impl;

// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.StandardOpenOption;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.UUID;
// import java.util.concurrent.TimeUnit;
// import java.util.stream.Stream;

// import org.springframework.stereotype.Service;

// import com.bidesh.OJ.Gusion.entity.Submission;
// import com.bidesh.OJ.Gusion.entity.TestCase;
// import com.bidesh.OJ.Gusion.entity.Verdict;
// import com.bidesh.OJ.Gusion.repository.TestCaseRepository;
// import com.bidesh.OJ.Gusion.service.JudgeService;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @Service
// @RequiredArgsConstructor
// public class DockerJudgeService implements JudgeService {

//     private final TestCaseRepository testCaseRepository;

//     /**
//      * Strictly judges a submission against ALL test cases (public + hidden).
//      * Returns AC only if 100% of cases pass.
//      */
//     @Override
//     public Verdict judge(Submission submission) {
//         log.info("Judging submission {} (Lang: {})", submission.getId(), submission.getLanguage());
//         Path workDir = null;
//         try {
//             workDir = setupWorkDir(submission);

//             // 1. Fetch ALL Test Cases (Public & Hidden)
//             List<TestCase> testCases = testCaseRepository.findByProblemId(submission.getProblem().getId());
//             if (testCases.isEmpty()) {
//                 log.warn("No test cases found for problem {}", submission.getProblem().getId());
//                 return Verdict.SE; // System Error if no test cases exist
//             }

//             // 2. Iterate and Execute
//             for (TestCase tc : testCases) {
//                 String output = executeDocker(workDir, submission, tc.getInput());

//                 // --- Failure Checks ---
//                 if (output.startsWith("Error: Time Limit Exceeded")) return Verdict.TLE;
//                 if (output.startsWith("Error:")) return Verdict.RE;

//                 // --- Strict Comparison ---
//                 String expected = tc.getExpectedOutput().trim();
//                 String actual = output.replace("\r\n", "\n").trim();

//                 if (!actual.equals(expected.replace("\r\n", "\n"))) {
//                     log.info("Submission {} failed on test case ID: {}. Verdict: WA", submission.getId(), tc.getId());
//                     return Verdict.WA; // Fail fast on first error
//                 }
//             }

//             // 3. Only reached if ALL cases passed
//             log.info("Submission {} passed all {} test cases.", submission.getId(), testCases.size());
//             return Verdict.AC;

//         } catch (Exception e) {
//             log.error("Internal Judge Error for submission {}: ", submission.getId(), e);
//             return Verdict.RE;
//         } finally {
//             cleanup(workDir);
//         }
//     }

//     @Override
//     public String runRaw(Submission submission, String input) {
//         log.info("Running raw execution for submission {}", submission.getId());
//         Path workDir = null;
//         try {
//             workDir = setupWorkDir(submission);
//             return executeDocker(workDir, submission, input);
//         } catch (Exception e) {
//             log.error("RunRaw Error", e);
//             return "System Error: " + e.getMessage();
//         } finally {
//             cleanup(workDir);
//         }
//     }

//     // --- SHARED CORE (Preserved from your previous version) ---

//     private String executeDocker(Path workDir, Submission submission, String input) throws IOException, InterruptedException {
//         List<String> command = buildDockerCommand(workDir, submission);

//         ProcessBuilder pb = new ProcessBuilder(command);
//         pb.redirectErrorStream(true);
//         Process process = pb.start();

//         if (input != null && !input.isBlank()) {
//             try (var os = process.getOutputStream()) {
//                 os.write(input.getBytes());
//                 os.flush();
//             } catch (IOException e) { /* Container closed early */ }
//         }

//         // Standard competitive programming limits (15s for Docker overhead + execution)
//         boolean finished = process.waitFor(15000, TimeUnit.MILLISECONDS);
//         if (!finished) {
//             process.destroyForcibly();
//             return "Error: Time Limit Exceeded";
//         }

//         String output = new String(process.getInputStream().readAllBytes());
//         if (process.exitValue() != 0) {
//             return "Error: Runtime Error (Exit Code " + process.exitValue() + ")\n" + output;
//         }

//         return output;
//     }

//     private Path setupWorkDir(Submission sub) throws IOException {
//         Path projectDir = Path.of(System.getProperty("user.dir"));
//         Path tempRoot = projectDir.resolve("temp_judge");
//         if (!Files.exists(tempRoot)) Files.createDirectories(tempRoot);

//         Path workDir = Files.createTempDirectory(tempRoot, "sub_" + sub.getId() + "_");

//         String fileName = "solution.txt";
//         if (sub.getLanguage().toString().equalsIgnoreCase("JAVA")) fileName = "Main.java";
//         else if (sub.getLanguage().toString().equalsIgnoreCase("PYTHON")) fileName = "solution.py";

//         Files.writeString(workDir.resolve(fileName), sub.getCode(), StandardOpenOption.CREATE);
//         return workDir;
//     }

//     private List<String> buildDockerCommand(Path workDir, Submission sub) {
//         String image = "eclipse-temurin:17-jdk-alpine";
//         if (sub.getLanguage().toString().equalsIgnoreCase("PYTHON")) image = "python:3.9-alpine";

//         String runCmd = "echo 'Unsupported'";
//         if (sub.getLanguage().toString().equalsIgnoreCase("JAVA")) {
//             runCmd = "javac Main.java && java Main";
//         } else if (sub.getLanguage().toString().equalsIgnoreCase("PYTHON")) {
//             runCmd = "python3 solution.py";
//         }

//         String hostPath = workDir.toAbsolutePath().toString().replace("\\", "/");
//         if (hostPath.startsWith("C:")) {
//             hostPath = "/" + hostPath.toLowerCase().replace(":", "");
//         }

//         List<String> cmd = new ArrayList<>();
//         cmd.add("docker");
//         cmd.add("run");
//         cmd.add("--rm");
//         cmd.add("-i");
//         cmd.add("--network");
//         cmd.add("none"); // Security: Disable network
//         cmd.add("--memory");
//         cmd.add("512m"); // Limit memory
//         cmd.add("-v");
//         cmd.add(hostPath + ":/app");
//         cmd.add("-w");
//         cmd.add("/app");
//         cmd.add(image);
//         cmd.add("sh");
//         cmd.add("-c");
//         cmd.add(runCmd);

//         return cmd;
//     }

//     private void cleanup(Path workDir) {
//         if (workDir != null) {
//             try (Stream<Path> walk = Files.walk(workDir)) {
//                 walk.sorted((a, b) -> b.compareTo(a))
//                         .forEach(p -> {
//                             try { Files.delete(p); } catch (IOException e) {}
//                         });
//             } catch (IOException e) {}
//         }
//     }
// }
