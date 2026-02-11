-- 1. Insert Problem 1 (Two Sum)
INSERT INTO problems (id, title, slug, description, difficulty, cpu_limit_ms, memory_limit_kb, starter_code)
VALUES (1, 
        'Two Sum', 
        'two-sum', 
        'Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target.', 
        'EASY', 
        1000, 
        256, 
        'class Solution { public int[] twoSum(int[] nums, int target) { return new int[]{}; } }')
ON CONFLICT (id) DO NOTHING;

-- 2. Insert Test Cases for Problem 1 (CRITICAL FIX!)
INSERT INTO test_cases (id, problem_id, input, expected_output)
VALUES 
(1, 1, '2 7 11 15\n9', '[0, 1]'),  -- Example 1
(2, 1, '3 2 4\n6', '[1, 2]'),      -- Example 2
(3, 1, '3 3\n6', '[0, 1]')         -- Example 3
ON CONFLICT (id) DO NOTHING;

-- 3. Insert Admin User
INSERT INTO users (id, email, role)
VALUES ('00000000-0000-0000-0000-000000000000', 'admin@gusion.app', 'ADMIN')
ON CONFLICT (id) DO NOTHING;