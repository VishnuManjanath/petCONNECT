-- petCONNECT Database Schema for H2 (Testing)
-- Advanced Pet Adoption Platform

-- Drop tables if they exist (reverse order due to foreign keys)
DROP TABLE IF EXISTS community_posts;
DROP TABLE IF EXISTS adoption_applications;
DROP TABLE IF EXISTS personality_profiles;
DROP TABLE IF EXISTS lifestyle_profiles;
DROP TABLE IF EXISTS pets;
DROP TABLE IF EXISTS shelters;
DROP TABLE IF EXISTS users;

-- Users table
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(50),
    zip_code VARCHAR(10),
    role VARCHAR(20) NOT NULL DEFAULT 'ADOPTER' CHECK (role IN ('ADOPTER', 'SHELTER_ADMIN', 'ADMIN')),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Shelters table
CREATE TABLE shelters (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    address TEXT NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(50) NOT NULL,
    zip_code VARCHAR(10) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    website VARCHAR(255),
    admin_user_id VARCHAR(36) NOT NULL,
    license_number VARCHAR(100),
    capacity INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Pets table
CREATE TABLE pets (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    species VARCHAR(50) NOT NULL,
    breed VARCHAR(100),
    age INTEGER NOT NULL,
    age_group VARCHAR(20) NOT NULL CHECK (age_group IN ('PUPPY', 'YOUNG', 'ADULT', 'SENIOR')),
    size VARCHAR(20) NOT NULL CHECK (size IN ('SMALL', 'MEDIUM', 'LARGE', 'EXTRA_LARGE')),
    weight DECIMAL(5,2),
    gender VARCHAR(10) NOT NULL,
    color VARCHAR(100),
    description TEXT,
    medical_history TEXT,
    special_needs TEXT,
    house_trained BOOLEAN DEFAULT FALSE,
    good_with_kids BOOLEAN DEFAULT FALSE,
    good_with_dogs BOOLEAN DEFAULT FALSE,
    good_with_cats BOOLEAN DEFAULT FALSE,
    adoption_fee DECIMAL(8,2) DEFAULT 0.00,
    available BOOLEAN DEFAULT TRUE,
    shelter_id VARCHAR(36) NOT NULL,
    image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (shelter_id) REFERENCES shelters(id) ON DELETE CASCADE
);

-- Lifestyle Profiles table (for users)
CREATE TABLE lifestyle_profiles (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) UNIQUE NOT NULL,
    living_situation VARCHAR(100) NOT NULL, -- apartment, house, farm, etc.
    yard_size VARCHAR(50), -- none, small, medium, large
    activity_level VARCHAR(20) NOT NULL CHECK (activity_level IN ('LOW', 'MODERATE', 'HIGH', 'VERY_HIGH')),
    experience_level VARCHAR(50) NOT NULL, -- first_time, some_experience, very_experienced
    time_availability INTEGER NOT NULL, -- hours per day
    has_children BOOLEAN DEFAULT FALSE,
    children_ages VARCHAR(100), -- age ranges if has_children is true
    has_other_pets BOOLEAN DEFAULT FALSE,
    other_pets_description TEXT,
    preferred_pet_age VARCHAR(20) CHECK (preferred_pet_age IN ('PUPPY', 'YOUNG', 'ADULT', 'SENIOR')),
    preferred_pet_size VARCHAR(20) CHECK (preferred_pet_size IN ('SMALL', 'MEDIUM', 'LARGE', 'EXTRA_LARGE')),
    max_adoption_fee DECIMAL(8,2),
    special_requirements TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Personality Profiles table (for pets)
CREATE TABLE personality_profiles (
    id VARCHAR(36) PRIMARY KEY,
    pet_id VARCHAR(36) UNIQUE NOT NULL,
    energy_level VARCHAR(20) NOT NULL CHECK (energy_level IN ('LOW', 'MODERATE', 'HIGH', 'VERY_HIGH')),
    sociability VARCHAR(20) NOT NULL CHECK (sociability IN ('SHY', 'MODERATE', 'SOCIAL', 'VERY_SOCIAL')),
    trainability VARCHAR(20) NOT NULL CHECK (trainability IN ('EASY', 'MODERATE', 'CHALLENGING', 'EXPERT_ONLY')),
    independence_level INTEGER CHECK (independence_level >= 1 AND independence_level <= 5), -- 1-5 scale
    playfulness_level INTEGER CHECK (playfulness_level >= 1 AND playfulness_level <= 5), -- 1-5 scale
    affection_level INTEGER CHECK (affection_level >= 1 AND affection_level <= 5), -- 1-5 scale
    exercise_needs INTEGER NOT NULL, -- minutes per day
    grooming_needs VARCHAR(50) NOT NULL, -- low, moderate, high
    noise_level VARCHAR(50) NOT NULL, -- quiet, moderate, vocal
    adaptability INTEGER CHECK (adaptability >= 1 AND adaptability <= 5), -- 1-5 scale
    special_traits TEXT, -- any unique personality traits
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE
);

-- Adoption Applications table
CREATE TABLE adoption_applications (
    id VARCHAR(36) PRIMARY KEY,
    applicant_id VARCHAR(36) NOT NULL,
    pet_id VARCHAR(36) NOT NULL,
    shelter_id VARCHAR(36) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'WITHDRAWN')),
    application_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    review_date TIMESTAMP NULL,
    reviewer_notes TEXT,
    applicant_message TEXT,
    home_visit_required BOOLEAN DEFAULT FALSE,
    home_visit_completed BOOLEAN DEFAULT FALSE,
    home_visit_date TIMESTAMP NULL,
    references_checked BOOLEAN DEFAULT FALSE,
    background_check_completed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (applicant_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
    FOREIGN KEY (shelter_id) REFERENCES shelters(id) ON DELETE CASCADE,
    UNIQUE(applicant_id, pet_id) -- Prevent duplicate applications
);

-- Community Posts table
CREATE TABLE community_posts (
    id VARCHAR(36) PRIMARY KEY,
    author_id VARCHAR(36) NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    post_type VARCHAR(50) NOT NULL, -- success_story, advice, question, event
    pet_id VARCHAR(36), -- optional, for posts about specific pets
    image_url VARCHAR(500),
    likes_count INTEGER DEFAULT 0,
    comments_count INTEGER DEFAULT 0,
    is_featured BOOLEAN DEFAULT FALSE,
    is_published BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE SET NULL
);

-- Create indexes for better performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_pets_shelter_id ON pets(shelter_id);
CREATE INDEX idx_pets_available ON pets(available);
CREATE INDEX idx_pets_species ON pets(species);
CREATE INDEX idx_pets_size ON pets(size);
CREATE INDEX idx_pets_age_group ON pets(age_group);
CREATE INDEX idx_adoption_applications_applicant ON adoption_applications(applicant_id);
CREATE INDEX idx_adoption_applications_pet ON adoption_applications(pet_id);
CREATE INDEX idx_adoption_applications_shelter ON adoption_applications(shelter_id);
CREATE INDEX idx_adoption_applications_status ON adoption_applications(status);
CREATE INDEX idx_community_posts_author ON community_posts(author_id);
CREATE INDEX idx_community_posts_type ON community_posts(post_type);
CREATE INDEX idx_community_posts_published ON community_posts(is_published);

-- Insert sample data for development (with manual UUIDs for H2)
INSERT INTO users (id, username, email, password, first_name, last_name, role, phone, address, city, state, zip_code) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'admin', 'admin@petconnect.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Admin', 'User', 'ADMIN', '555-0001', '123 Admin St', 'Admin City', 'CA', '90210'),
('550e8400-e29b-41d4-a716-446655440002', 'shelter1', 'shelter1@petconnect.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Sarah', 'Johnson', 'SHELTER_ADMIN', '555-0002', '456 Shelter Ave', 'Pet City', 'CA', '90211'),
('550e8400-e29b-41d4-a716-446655440003', 'adopter1', 'adopter1@petconnect.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'John', 'Smith', 'ADOPTER', '555-0003', '789 Adopter Rd', 'Love City', 'CA', '90212');

-- Insert sample shelter
INSERT INTO shelters (id, name, description, address, city, state, zip_code, phone, email, admin_user_id, license_number, capacity) VALUES
('550e8400-e29b-41d4-a716-446655440004', 'Happy Paws Shelter', 'A loving home for pets in need', '789 Pet Street', 'Pet City', 'CA', '90211', '555-0010', 'info@happypaws.com', '550e8400-e29b-41d4-a716-446655440002', 'SH001', 50);

-- Insert sample pets
INSERT INTO pets (id, name, species, breed, age, age_group, size, weight, gender, color, description, adoption_fee, shelter_id, available) VALUES
('550e8400-e29b-41d4-a716-446655440005', 'Buddy', 'Dog', 'Golden Retriever', 3, 'ADULT', 'LARGE', 65.5, 'Male', 'Golden', 'Friendly and energetic dog looking for a loving family', 250.00, '550e8400-e29b-41d4-a716-446655440004', TRUE),
('550e8400-e29b-41d4-a716-446655440006', 'Luna', 'Cat', 'Siamese', 2, 'YOUNG', 'MEDIUM', 8.2, 'Female', 'Cream and Brown', 'Gentle and affectionate cat, great with children', 150.00, '550e8400-e29b-41d4-a716-446655440004', TRUE);

-- Note: Password is 'password' for all sample users (BCrypt encoded)