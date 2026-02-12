-- ==========================================
-- 1. USERS (Admin & Student)
-- ==========================================
INSERT INTO users (id, email, name, password, role)
VALUES
    ('00000000-0000-0000-0000-000000000000', 'admin@gusion.io', 'System Admin', 'admin123', 'ADMIN'),
    ('8e216c54-3de6-415b-a1ed-ba533ff68f90', 'student@gusion.io', 'Student User', 'student123', 'STUDENT')
    ON CONFLICT (id) DO UPDATE
                            SET email = EXCLUDED.email, name = EXCLUDED.name, password = EXCLUDED.password;

-- ==========================================
-- 2. PROBLEMS (Detailed + Reference Solutions)
-- ==========================================

-- Problem 1: Two Sum (EASY)
INSERT INTO problems (id, title, slug, difficulty, cpu_limit_ms, memory_limit_kb, description, starter_code, reference_solution)
VALUES (1, 'Two Sum', 'two-sum', 'EASY', 1000, 256000,
        'Given an array of integers `nums` and an integer `target`, return indices of the two numbers such that they add up to `target`.

        **Input Format:**
        - First line: The array elements separated by spaces (e.g., `2 7 11 15`).
        - Second line: The target integer (e.g., `9`).

        **Output Format:**
        - Print the result as a list: `[index1, index2]`

        **Example 1:**
        Input:
        2 7 11 15
        9
        Output: [0, 1]',
        'import java.util.*; public class Main { public static void main(String[] args) { Scanner s = new Scanner(System.in); } }',
        'import java.util.*; public class Main { public static void main(String[] args) { Scanner s = new Scanner(System.in); if(!s.hasNextLine()) return; String[] pts = s.nextLine().trim().split("\\\s+"); if(!s.hasNextInt()) return; int t = s.nextInt(); Map<Integer, Integer> m = new HashMap<>(); for(int i=0; i<pts.length; i++){ int n = Integer.parseInt(pts[i]); if(m.containsKey(t-n)){ System.out.println("["+m.get(t-n)+", "+i+"]"); return; } m.put(n, i); } } }')
    ON CONFLICT (id) DO UPDATE SET reference_solution = EXCLUDED.reference_solution, description = EXCLUDED.description;

-- Problem 2: Valid Parentheses (EASY)
INSERT INTO problems (id, title, slug, difficulty, cpu_limit_ms, memory_limit_kb, description, starter_code, reference_solution)
VALUES (2, 'Valid Parentheses', 'valid-parentheses', 'EASY', 1000, 256000,
        'Given a string `s` containing just the characters `(`, `)`, `{`, `}`, `[` and `]`, determine if the input string is valid.

        **Input Format:**
        - A single line containing the string `s`.

        **Output Format:**
        - Print `true` or `false`.

        **Example 1:**
        Input: ()[]{}
        Output: true',
        'import java.util.*; public class Main { public static void main(String[] args) { Scanner s = new Scanner(System.in); } }',
        'import java.util.*; public class Main { public static void main(String[] args) { Scanner s = new Scanner(System.in); if(!s.hasNext()) return; String str = s.next(); Stack<Character> st = new Stack<>(); for(char c : str.toCharArray()){ if(c==''('') st.push('')''); else if(c==''{'') st.push(''}''); else if(c==''['') st.push('']''); else if(st.isEmpty() || st.pop() != c){ System.out.println("false"); return; } } System.out.println(st.isEmpty()); } }')
    ON CONFLICT (id) DO UPDATE SET reference_solution = EXCLUDED.reference_solution, description = EXCLUDED.description;

-- Problem 3: Longest Substring Without Repeating Characters (MEDIUM)
INSERT INTO problems (id, title, slug, difficulty, cpu_limit_ms, memory_limit_kb, description, starter_code, reference_solution)
VALUES (3, 'Longest Substring Without Repeating', 'longest-substring', 'MEDIUM', 2000, 256000,
        'Given a string `s`, find the length of the longest substring without repeating characters.

        **Input Format:**
        - A single line containing the string `s`.

        **Output Format:**
        - Print the integer length.

        **Example:**
        Input: abcabcbb
        Output: 3',
        'import java.util.*; public class Main { public static void main(String[] args) { Scanner s = new Scanner(System.in); } }',
        'import java.util.*; public class Main { public static void main(String[] args) { Scanner s = new Scanner(System.in); String str = s.hasNextLine() ? s.nextLine() : ""; int n = str.length(), res = 0; Map<Character, Integer> m = new HashMap<>(); for(int j=0, i=0; j<n; j++){ if(m.containsKey(str.charAt(j))) i = Math.max(m.get(str.charAt(j)), i); res = Math.max(res, j-i+1); m.put(str.charAt(j), j+1); } System.out.println(res); } }')
    ON CONFLICT (id) DO UPDATE SET reference_solution = EXCLUDED.reference_solution, description = EXCLUDED.description;

-- Problem 4: Maximum Subarray (MEDIUM)
INSERT INTO problems (id, title, slug, difficulty, cpu_limit_ms, memory_limit_kb, description, starter_code, reference_solution)
VALUES (4, 'Maximum Subarray', 'maximum-subarray', 'MEDIUM', 1500, 256000,
        'Given an integer array `nums`, find the subarray with the largest sum, and return its sum.

        **Input Format:**
        - A single line containing integers separated by spaces.

        **Output Format:**
        - Print the maximum sum.

        **Example:**
        Input: -2 1 -3 4 -1 2 1 -5 4
        Output: 6',
        'import java.util.*; public class Main { public static void main(String[] args) { Scanner s = new Scanner(System.in); } }',
        'import java.util.*; public class Main { public static void main(String[] args) { Scanner s = new Scanner(System.in); if(!s.hasNextLine()) return; String[] pts = s.nextLine().trim().split("\\\s+"); int max = Integer.MIN_VALUE, cur = 0; for(String p : pts){ int n = Integer.parseInt(p); cur += n; if(max < cur) max = cur; if(cur < 0) cur = 0; } System.out.println(max); } }')
    ON CONFLICT (id) DO UPDATE SET reference_solution = EXCLUDED.reference_solution, description = EXCLUDED.description;

-- Problem 5: Trapping Rain Water (HARD)
INSERT INTO problems (id, title, slug, difficulty, cpu_limit_ms, memory_limit_kb, description, starter_code, reference_solution)
VALUES (5, 'Trapping Rain Water', 'trapping-rain-water', 'HARD', 2000, 512000,
        'Given `n` non-negative integers representing an elevation map where the width of each bar is 1, compute how much water it can trap after raining.

        **Input Format:**
        - A single line containing integers separated by spaces.

        **Output Format:**
        - Print the total water trapped.

        **Example:**
        Input: 0 1 0 2 1 0 1 3 2 1 2 1
        Output: 6',
        'import java.util.*; public class Main { public static void main(String[] args) { Scanner s = new Scanner(System.in); } }',
        'import java.util.*; public class Main { public static void main(String[] args) { Scanner s = new Scanner(System.in); if(!s.hasNextLine()) return; String[] pts = s.nextLine().trim().split("\\\s+"); int n = pts.length; if(n==0) {System.out.println(0); return;} int[] h = new int[n]; for(int i=0; i<n; i++) h[i] = Integer.parseInt(pts[i]); int l=0, r=n-1, lM=0, rM=0, res=0; while(l<r){ if(h[l]<h[r]){ if(h[l]>=lM) lM=h[l]; else res+=lM-h[l]; l++; } else { if(h[r]>=rM) rM=h[r]; else res+=rM-h[r]; r--; } } System.out.println(res); } }')
    ON CONFLICT (id) DO UPDATE SET reference_solution = EXCLUDED.reference_solution, description = EXCLUDED.description;

-- Problem 6: Median of Two Sorted Arrays (HARD)
INSERT INTO problems (id, title, slug, difficulty, cpu_limit_ms, memory_limit_kb, description, starter_code, reference_solution)
VALUES (6, 'Median of Two Sorted Arrays', 'median-sorted-arrays', 'HARD', 2000, 512000,
        'Given two sorted arrays `nums1` and `nums2`, return the median of the two sorted arrays.

        **Input Format:**
        - First line: Elements of first array (space separated).
        - Second line: Elements of second array (space separated).

        **Output Format:**
        - Print the median value formatted to 5 decimal places (e.g., 2.00000).

        **Example:**
        Input:
        1 3
        2
        Output: 2.00000',
        'import java.util.*; public class Main { public static void main(String[] args) { Scanner s = new Scanner(System.in); } }',
        'import java.util.*; public class Main { public static void main(String[] args) { Scanner s = new Scanner(System.in); List<Integer> l = new ArrayList<>(); while(s.hasNext()){ try { l.add(s.nextInt()); } catch (Exception e) { break; } } Collections.sort(l); int n = l.size(); if (n == 0) { System.out.printf("%.5f\\n", 0.0); return; } double res = (n%2!=0) ? l.get(n/2) : (l.get(n/2-1)+l.get(n/2))/2.0; System.out.printf("%.5f\\n", res); } }')
    ON CONFLICT (id) DO UPDATE SET reference_solution = EXCLUDED.reference_solution, description = EXCLUDED.description;


-- ==========================================
-- 3. TEST CASES (Public & Hidden)
-- ==========================================
INSERT INTO test_cases (problem_id, input, expected_output, is_hidden) VALUES
                                                                           (1, '2 7 11 15\n9', '[0, 1]', false), (1, '3 2 4\n6', '[1, 2]', false), (1, '3 3\n6', '[0, 1]', true), (1, '0 4 3 0\n0', '[0, 3]', true),
                                                                           (2, '()', 'true', false), (2, '()[]{}', 'true', false), (2, '(]', 'false', true), (2, '([)]', 'false', true),
                                                                           (3, 'abcabcbb', '3', false), (3, 'bbbbb', '1', false), (3, 'pwwkew', '3', true), (3, ' ', '1', true),
                                                                           (4, '-2 1 -3 4 -1 2 1 -5 4', '6', false), (4, '1', '1', false), (4, '5 4 -1 7 8', '23', true), (4, '-1', '-1', true),
                                                                           (5, '0 1 0 2 1 0 1 3 2 1 2 1', '6', false), (5, '4 2 0 3 2 5', '9', false), (5, '4 2 3', '1', true), (5, '5', '0', true),
                                                                           (6, '1 3\n2', '2.00000', false), (6, '1 2\n3 4', '2.50000', false), (6, '0 0\n0 0', '0.00000', true), (6, '\n1', '1.00000', true);


-- ==========================================
-- 4. CLEANUP & SEQUENCES
-- ==========================================
UPDATE test_cases SET is_hidden = false WHERE is_hidden IS NULL;
SELECT setval(pg_get_serial_sequence('problems', 'id'), coalesce(max(id),0) + 1, false) FROM problems;
SELECT setval(pg_get_serial_sequence('test_cases', 'id'), coalesce(max(id),0) + 1, false) FROM test_cases;

-- AI Hints Logic
CREATE TABLE IF NOT EXISTS ai_hints (
                                        id UUID PRIMARY KEY,
                                        problem_id BIGINT REFERENCES problems(id),
    user_id UUID REFERENCES users(id),
    level1_text TEXT,
    level2_text TEXT,
    level3_text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );