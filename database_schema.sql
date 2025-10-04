-- =================================================================
-- Schema for Clinic Management System
-- Database: MySQL
-- Author: Mithun Thomas
-- =================================================================
USE clinicdb;


-- Drop tables if they exist to start fresh (useful for development)
DROP TABLE IF EXISTS specialization;
DROP TABLE IF EXISTS billing;
DROP TABLE IF EXISTS prescriptions;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS doctors;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS clinics;
DROP TABLE IF EXISTS super_admins;

-- -----------------------------------------------------
-- Table `super_admins`
-- Stores the global administrators who can manage the entire system.
-- -----------------------------------------------------
CREATE TABLE super_admins (
  super_admin_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  username VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL COMMENT 'Stores hashed passwords (e.g., BCrypt)'
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `clinics`
-- Stores information about each individual clinic (tenant).
-- -----------------------------------------------------
CREATE TABLE clinics (
  clinic_id INT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(20) UNIQUE NOT NULL COMMENT 'A short, unique code for clinic login',
  name VARCHAR(100) NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  phone VARCHAR(20),
  address VARCHAR(255),
  status ENUM('Active', 'Suspended') NOT NULL DEFAULT 'Active',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created_by INT,
  FOREIGN KEY (created_by) REFERENCES super_admins(super_admin_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `users`
-- Stores all staff members for all clinics (Admins, Doctors, Receptionists, etc.).
-- -----------------------------------------------------
CREATE TABLE users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  clinic_id INT NOT NULL,
  name VARCHAR(100) NOT NULL,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(255) NOT NULL COMMENT 'Stores hashed passwords (e.g., BCrypt)',
  role ENUM('ADMIN', 'DOCTOR', 'RECEPTIONIST') NOT NULL,
  status ENUM('Active', 'Suspended') NOT NULL DEFAULT 'Active',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (clinic_id, username), -- A username must be unique within a specific clinic
  FOREIGN KEY (clinic_id) REFERENCES clinics(clinic_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `doctors`
-- Stores additional information specific to users with the 'DOCTOR' role.
-- -----------------------------------------------------
CREATE TABLE doctors (
  doctor_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL UNIQUE,
  specialization VARCHAR(100),
  consultation_fee DECIMAL(10, 2) DEFAULT 0.00,
  schedule TEXT COMMENT 'Stores schedule info, e.g., "Mon-Fri 9am-5pm"',
  status ENUM('Active', 'Inactive') NOT NULL DEFAULT 'Active',
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `patients`
-- Stores patient records, linked to a specific clinic.
-- -----------------------------------------------------
CREATE TABLE patients (
  patient_id INT AUTO_INCREMENT PRIMARY KEY,
  clinic_id INT NOT NULL,
  name VARCHAR(100) NOT NULL,
  dob DATE,
  gender ENUM('Male', 'Female', 'Other'),
  phone VARCHAR(20),
  email VARCHAR(100),
  address VARCHAR(255),
  blood_group ENUM('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'),
  allergies TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (clinic_id) REFERENCES clinics(clinic_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `appointments`
-- The central table linking patients to doctors for a scheduled visit.
-- -----------------------------------------------------
CREATE TABLE appointments (
  appointment_id INT AUTO_INCREMENT PRIMARY KEY,
  patient_id INT NOT NULL,
  doctor_id INT NOT NULL,
  clinic_id INT NOT NULL,
  appointment_date DATETIME NOT NULL,
  status ENUM('Scheduled', 'Completed', 'Cancelled') NOT NULL DEFAULT 'Scheduled',
  notes TEXT COMMENT 'Notes from the receptionist or initial complaint',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
  FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE,
  FOREIGN KEY (clinic_id) REFERENCES clinics(clinic_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `prescriptions`
-- Stores prescription details for a completed appointment.
-- -----------------------------------------------------
CREATE TABLE prescriptions (
    prescription_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT NOT NULL UNIQUE, -- One prescription per appointment
    doctor_notes TEXT COMMENT 'Diagnosis and notes from the doctor',
    medicine_list TEXT NOT NULL COMMENT 'List of prescribed medicines and dosage',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `specializations`
-- Stores a list of medical specializations available at each clinic.
-- This allows each clinic to define its own set of roles.
-- -----------------------------------------------------
CREATE TABLE specializations (
  specialization_id INT AUTO_INCREMENT PRIMARY KEY,
  clinic_id INT NOT NULL,
  name VARCHAR(100) NOT NULL,
  UNIQUE (clinic_id, name), -- A specialization name must be unique within a clinic
  status ENUM('Active', 'Inactive') NOT NULL DEFAULT 'Active',
  FOREIGN KEY (clinic_id) REFERENCES clinics(clinic_id) ON DELETE CASCADE
) ENGINE=InnoDB;


-- -----------------------------------------------------
-- Table `billing`
-- Stores financial transactions related to appointments or other services.
-- -----------------------------------------------------
CREATE TABLE billing (
  bill_id INT AUTO_INCREMENT PRIMARY KEY,
  appointment_id INT NOT NULL,
  patient_id INT NOT NULL,
  amount DECIMAL(10, 2) NOT NULL,
  status ENUM('Paid', 'Unpaid') NOT NULL DEFAULT 'Unpaid',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id) ON DELETE CASCADE,
  FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- =================================================================
-- Sample Data Insertion (Optional, for testing)
-- =================================================================

-- 1. Insert the first Super Admin (replace with your own hashed password)
-- The hash below is for the password "super123"
INSERT INTO super_admins (name, username, password) VALUES
('System Owner', 'superadmin', '$2a$12$7qoR95kEwX9Zr5h1bYfIpuKOWzPTFk.CtKu7qE4pKmO3XE9Zhy0y6');

-- You can add more sample data for clinics, users, etc. here for quick setup.
-- For example:
-- INSERT INTO clinics (code, name, email, phone, address, created_by) VALUES
-- ('SUN01', 'Sunrise Health Clinic', 'contact@sunrise.com', '555-0101', '123 Health St, Medville', 1);

-- The first Clinic Admin would then be created via the Super Admin application.

-- End of script