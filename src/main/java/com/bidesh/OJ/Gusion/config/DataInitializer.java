// package com.bidesh.OJ.Gusion.config;

// import org.springframework.boot.CommandLineRunner;
// import org.springframework.context.annotation.Profile;
// import org.springframework.stereotype.Component;

// import com.bidesh.OJ.Gusion.entity.Difficulty;
// import com.bidesh.OJ.Gusion.entity.Problem;
// import com.bidesh.OJ.Gusion.entity.TestCase; // ✅ Make sure this is imported
// import com.bidesh.OJ.Gusion.entity.User;
// import com.bidesh.OJ.Gusion.entity.UserRole;
// import com.bidesh.OJ.Gusion.repository.ProblemRepository;
// import com.bidesh.OJ.Gusion.repository.UserRepository;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Component
// @Profile("!test")
// @RequiredArgsConstructor
// @Slf4j
// public class DataInitializer implements CommandLineRunner {

//     private final UserRepository userRepository;
//     private final ProblemRepository problemRepository;

//     @Override
//     public void run(String... args) {
//         // 1. Initialize Users
//         if (userRepository.count() == 0) {
//             var admin = User.builder().email("admin@gusion.io").role(UserRole.ADMIN).build();
//             var student = User.builder().email("student@gusion.io").role(UserRole.STUDENT).build();
//             userRepository.save(admin);
//             userRepository.save(student);
//             log.info("Initialized users: admin@gusion.io, student@gusion.io");
//         }

//         // 2. Initialize Problem with REAL Test Cases
//         if (problemRepository.count() == 0) {
//             var problem = Problem.builder()
//                 .slug("two-sum")
//                 .title("Two Sum (A + B)") // Renamed for clarity
//                 .description("Given two integers A and B, return their sum. Input is provided as two space-separated integers.") 
//                 .difficulty(Difficulty.EASY)
//                 .cpuLimitMs(1000)
//                 .memoryLimitKb(256000)
//                 .starterCode(
//                     """
//                     import java.util.Scanner;
//                     public class Main {
//                         public static void main(String[] args) {
//                             Scanner s = new Scanner(System.in);
//                             solve(s);
//                         }
//                         public static void solve(Scanner s) {
//                             // TODO: Write your code here
//                         }
//                     }
//                     """
//                 )
//                 .build();

//             // ✅ Add Test Case 1 (1 + 2 = 3)
//             TestCase tc1 = TestCase.builder()
//                 .problem(problem)
//                 .inputUrl("1 2")   // Raw input content
//                 .outputUrl("3")    // Raw expected output
//                 .isHidden(false)
//                 .build();

//             // ✅ Add Test Case 2 (10 + 20 = 30)
//             TestCase tc2 = TestCase.builder()
//                 .problem(problem)
//                 .inputUrl("10 20")
//                 .outputUrl("30")
//                 .isHidden(true) // Hidden test case
//                 .build();

//             problem.getTestCases().add(tc1);
//             problem.getTestCases().add(tc2);

//             problemRepository.save(problem);
//             log.info("Initialized 'Two Sum' with 2 real test cases.");
//         }
//     }
// }
// package com.bidesh.OJ.Gusion.config;

// import org.springframework.boot.CommandLineRunner;
// import org.springframework.context.annotation.Profile;
// import org.springframework.stereotype.Component;

// import com.bidesh.OJ.Gusion.entity.Difficulty;
// import com.bidesh.OJ.Gusion.entity.Problem;
// import com.bidesh.OJ.Gusion.entity.TestCase; // ✅ Make sure this is imported
// import com.bidesh.OJ.Gusion.entity.User;
// import com.bidesh.OJ.Gusion.entity.UserRole;
// import com.bidesh.OJ.Gusion.repository.ProblemRepository;
// import com.bidesh.OJ.Gusion.repository.UserRepository;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Component
// @Profile("!test")
// @RequiredArgsConstructor
// @Slf4j
// public class DataInitializer implements CommandLineRunner {

//     private final UserRepository userRepository;
//     private final ProblemRepository problemRepository;

//     @Override
//     public void run(String... args) {
//         // 1. Initialize Users
//         if (userRepository.count() == 0) {
//             var admin = User.builder().email("admin@gusion.io").role(UserRole.ADMIN).build();
//             var student = User.builder().email("student@gusion.io").role(UserRole.STUDENT).build();
//             userRepository.save(admin);
//             userRepository.save(student);
//             log.info("Initialized users: admin@gusion.io, student@gusion.io");
//         }

//         // 2. Initialize Problem with REAL Test Cases
//         if (problemRepository.count() == 0) {
//             var problem = Problem.builder()
//                 .slug("two-sum")
//                 .title("Two Sum (A + B)") // Renamed for clarity
//                 .description("Given two integers A and B, return their sum. Input is provided as two space-separated integers.")
//                 .difficulty(Difficulty.EASY)
//                 .cpuLimitMs(1000)
//                 .memoryLimitKb(256000)
//                 .starterCode(
//                     """
//                     import java.util.Scanner;
//                     public class Main {
//                         public static void main(String[] args) {
//                             Scanner s = new Scanner(System.in);
//                             solve(s);
//                         }
//                         public static void solve(Scanner s) {
//                             // TODO: Write your code here
//                         }
//                     }
//                     """
//                 )
//                 .build();

//             // ✅ Add Test Case 1 (1 + 2 = 3)
//             TestCase tc1 = TestCase.builder()
//                 .problem(problem)
//                 .inputUrl("1 2")   // Raw input content
//                 .outputUrl("3")    // Raw expected output
//                 .isHidden(false)
//                 .build();

//             // ✅ Add Test Case 2 (10 + 20 = 30)
//             TestCase tc2 = TestCase.builder()
//                 .problem(problem)
//                 .inputUrl("10 20")
//                 .outputUrl("30")
//                 .isHidden(true) // Hidden test case
//                 .build();

//             problem.getTestCases().add(tc1);
//             problem.getTestCases().add(tc2);

//             problemRepository.save(problem);
//             log.info("Initialized 'Two Sum' with 2 real test cases.");
//         }
//     }
// }
