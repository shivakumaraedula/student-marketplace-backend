-- Seed data for CampusMarket
-- Password for all demo accounts: password123

-- Clear any existing items
DELETE FROM item_images;
DELETE FROM item_tags;
DELETE FROM items;

-- Seed demo users (password: password123)
-- BCrypt of "password123" = $2a$10$EblZqNptyYvcLm/VwDptlOtas1kpDdRgbO5B1SWzYkrFX4xD1s0lK
MERGE INTO users (id, name, email, password, university, verified, rating, role, total_sales) KEY (id)
VALUES (1, 'Admin User', 'admin@unimarket.com',
  '$2a$10$EblZqNptyYvcLm/VwDptlOtas1kpDdRgbO5B1SWzYkrFX4xD1s0lK',
  'Anurag University', TRUE, 5.00, 'ADMIN', 0);

MERGE INTO users (id, name, email, password, university, verified, rating, role, total_sales) KEY (id)
VALUES (2, 'Alex Rivers', 'alex@anurag.edu.in',
  '$2a$10$EblZqNptyYvcLm/VwDptlOtas1kpDdRgbO5B1SWzYkrFX4xD1s0lK',
  'Anurag University', TRUE, 4.8, 'USER', 5);

MERGE INTO users (id, name, email, password, university, verified, rating, role, total_sales) KEY (id)
VALUES (3, 'Sarah Chen', 'sarah@anurag.edu.in',
  '$2a$10$EblZqNptyYvcLm/VwDptlOtas1kpDdRgbO5B1SWzYkrFX4xD1s0lK',
  'Anurag University', TRUE, 4.9, 'USER', 3);
