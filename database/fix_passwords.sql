-- Update user passwords with correct BCrypt hashes
-- admin password: admin
-- user password: user@123

UPDATE users SET password = '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy4qUm2' WHERE username = 'admin';
UPDATE users SET password = '$2a$10$5OCqVMzAK6R8/ypDKq9k6uPdEaMDCvX3D0yMrPBBpEXkKVOFpbjMa' WHERE username IN ('user1', 'user2', 'user3');

-- Verify
SELECT username, password, LENGTH(password) as pwd_length FROM users;
