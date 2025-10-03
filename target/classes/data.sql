-- Sample data for petCONNECT application

-- Insert sample users (using MERGE to avoid duplicates)
MERGE INTO users (id, username, email, password, first_name, last_name, role, phone, address, city, state, zip_code, enabled) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'admin', 'admin@petconnect.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Admin', 'User', 'ADMIN', '555-0001', '123 Admin St', 'Admin City', 'CA', '90210', true),
('550e8400-e29b-41d4-a716-446655440002', 'shelter1', 'shelter1@petconnect.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Sarah', 'Johnson', 'SHELTER_ADMIN', '555-0002', '456 Shelter Ave', 'Pet City', 'CA', '90211', true),
('550e8400-e29b-41d4-a716-446655440003', 'adopter1', 'adopter1@petconnect.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'John', 'Smith', 'ADOPTER', '555-0003', '789 Adopter Rd', 'Love City', 'CA', '90212', true);

-- Insert sample shelter
MERGE INTO shelters (id, name, description, address, city, state, zip_code, phone, email, admin_user_id, license_number, capacity) VALUES
('550e8400-e29b-41d4-a716-446655440004', 'Happy Paws Shelter', 'A loving home for pets in need', '789 Pet Street', 'Pet City', 'CA', '90211', '555-0010', 'info@happypaws.com', '550e8400-e29b-41d4-a716-446655440002', 'SH001', 50);

-- Insert sample pets using INSERT with ON DUPLICATE KEY IGNORE for H2
INSERT INTO pets (id, name, species, breed, age, age_group, size, weight, gender, color, description, adoption_fee, shelter_id, is_available) 
SELECT '550e8400-e29b-41d4-a716-446655440005', 'Buddy', 'Dog', 'Golden Retriever', 3, 'ADULT', 'LARGE', 65.5, 'Male', 'Golden', 'Friendly and energetic dog looking for a loving family', 250.00, '550e8400-e29b-41d4-a716-446655440004', true
WHERE NOT EXISTS (SELECT 1 FROM pets WHERE id = '550e8400-e29b-41d4-a716-446655440005');

INSERT INTO pets (id, name, species, breed, age, age_group, size, weight, gender, color, description, adoption_fee, shelter_id, is_available) 
SELECT '550e8400-e29b-41d4-a716-446655440006', 'Luna', 'Cat', 'Siamese', 2, 'YOUNG', 'MEDIUM', 8.2, 'Female', 'Cream and Brown', 'Gentle and affectionate cat, great with children', 150.00, '550e8400-e29b-41d4-a716-446655440004', true
WHERE NOT EXISTS (SELECT 1 FROM pets WHERE id = '550e8400-e29b-41d4-a716-446655440006');

-- Insert sample lifestyle profile
MERGE INTO lifestyle_profiles (id, user_id, living_situation, yard_size, activity_level, experience_level, time_availability, has_children, has_other_pets, preferred_pet_age, preferred_pet_size, max_adoption_fee) VALUES
('550e8400-e29b-41d4-a716-446655440007', '550e8400-e29b-41d4-a716-446655440003', 'house', 'medium', 'MODERATE', 'some_experience', 4, false, false, 'ADULT', 'MEDIUM', 300.00);

-- Insert sample personality profiles for pets
MERGE INTO personality_profiles (id, pet_id, energy_level, sociability, trainability, independence_level, playfulness_level, affection_level, exercise_needs, grooming_needs, noise_level, adaptability) VALUES
('550e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440005', 'HIGH', 'VERY_SOCIAL', 'EASY', 3, 5, 5, 120, 'moderate', 'moderate', 4),
('550e8400-e29b-41d4-a716-446655440009', '550e8400-e29b-41d4-a716-446655440006', 'MODERATE', 'SOCIAL', 'MODERATE', 4, 3, 4, 30, 'low', 'quiet', 5);

-- Note: Password is 'password' for all sample users (BCrypt encoded)
