
CREATE DATABASE IF NOT EXISTS campus_skill_exchange;

USE campus_skill_exchange;

CREATE TABLE IF NOT EXISTS students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    department VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS skills (
    skill_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    skill_name VARCHAR(100) NOT NULL,
    skill_type VARCHAR(20) NOT NULL,
    FOREIGN KEY (student_id)
        REFERENCES students(student_id)
);

CREATE TABLE IF NOT EXISTS exchanges (
    exchange_id INT PRIMARY KEY AUTO_INCREMENT,
    requester_id INT NOT NULL,
    receiver_id INT NOT NULL,
    skill_name VARCHAR(100) NOT NULL,
    status VARCHAR(20) DEFAULT 'Pending',
    FOREIGN KEY (requester_id)
        REFERENCES students(student_id),
    FOREIGN KEY (receiver_id)
        REFERENCES students(student_id)
);

CREATE TABLE IF NOT EXISTS sessions (
    session_id INT PRIMARY KEY AUTO_INCREMENT,
    exchange_id INT NOT NULL,
    session_date DATE NOT NULL,
    session_time TIME NOT NULL,
    location VARCHAR(200) NOT NULL,
    status VARCHAR(20) DEFAULT 'Scheduled',
    FOREIGN KEY (exchange_id)
        REFERENCES exchanges(exchange_id)
);

CREATE TABLE IF NOT EXISTS reviews (
    review_id INT PRIMARY KEY AUTO_INCREMENT,
    exchange_id INT NOT NULL,
    reviewer_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment VARCHAR(500),
    FOREIGN KEY (exchange_id)
        REFERENCES exchanges(exchange_id),
    FOREIGN KEY (reviewer_id)
        REFERENCES students(student_id),
    UNIQUE (exchange_id, reviewer_id)
);
