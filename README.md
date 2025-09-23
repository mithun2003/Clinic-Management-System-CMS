# Clinic Management System

A secure, multi-tenant desktop application designed to streamline the operational management of modern medical clinics. Built with Java Swing and MySQL, this system provides a robust platform for handling patient records, appointments, staff management, and reporting, all from a single, intuitive interface.


## Table of Contents
- [Key Features](#key-features)
- [Architectural Design](#architectural-design)
- [Technology Stack](#technology-stack)
- [Project File Structure](#project-file-structure)
- [Setup and Installation](#setup-and-installation)
- [How to Run the Application](#how-to-run-the-application)
- [Workflow Overview](#workflow-overview)
- [Future Enhancements](#future-enhancements)
- [Author](#author)

## Key Features

- **Multi-Tenant Architecture:** A single application instance can serve multiple independent clinics, with data strictly segregated.
- **Role-Based Access Control (RBAC):**
    - **Super Admin:** Global administrator with the ability to create, manage, and suspend clinics.
    - **Clinic Admin:** Manages staff and views reports for their specific clinic.
    - **Doctor:** Manages patient consultations and prescriptions.
    - **Receptionist:** Handles patient registration, appointment scheduling, and billing.
- **Secure Authentication:** All user passwords are securely hashed using the **jBCrypt** library.
- **Modern User Interface:** A custom, undecorated UI with a sidebar navigation, modern form components, and interactive elements.
- **Comprehensive Modules:**
    - **Clinic Management (CRUD):** Add, update, delete, and view clinic details with pagination.
    - **Staff Management:** Onboard new doctors, receptionists, and other staff.
    - **Patient Records:** Maintain a complete digital record for every patient.
    - **Appointment Scheduling:** An easy-to-use system for booking and managing appointments.
    - **Reporting Dashboard:** Visual charts and key metrics (powered by JFreeChart) for data-driven insights.

## Architectural Design

The application is built following the **Model-View-Controller (MVC)** design pattern to ensure a clean separation of concerns:
- **Model:** Contains the data entities (e.g., `Clinic`, `User`, `Patient`), Data Access Objects (DAOs) for database interaction, and business logic.
- **View:** Consists of all the Java Swing UI components (`JFrame`, `JPanel`), including custom components like `SidebarButton` and `PlaceholderTextField`.
- **Controller:** Acts as the intermediary between the Model and the View, handling user input and application flow.

## Technology Stack

- **Programming Language:** Java (JDK `[Your Java Version, e.g., 17]`)
- **User Interface (UI):** Java Swing
- **Database:** MySQL Server (`[Your DB Version, e.g., 8.0]`)
- **Database Connectivity:** JDBC (Java Database Connectivity)
- **IDE:** Apache NetBeans (`[Your IDE Version, e.g., 12.0]`)
- **Dependencies:**
    - **MySQL Connector/J:** For JDBC connectivity.
    - **jBCrypt:** For secure password hashing.
    - **JFreeChart & JCommon:** For data visualization in the reports module.
- **OS:** Developed on `[Your OS, e.g., Windows 11]`

## Project File Structure

The project is organized into a clean, modular structure:
```
ClinicManagementSystem/
├── src/
│ ├── cms/
│ │ ├── StaffMain.java # Entry point for Clinic Staff
│ │ └── SuperAdminMain.java # Entry point for Super Admin
│ │
│ ├── cms/controller/
│ │ ├── AuthController.java
│ │ └── SuperAdminAuthController.java
│ │
│ ├── cms/model/
│ │ ├── dao/
│ │ │ ├── ClinicDAO.java
│ │ │ ├── ReportDAO.java
│ │ │ └── UserDAO.java
│ │ ├── database/
│ │ │ └── DBConnection.java
│ │ └── entities/
│ │ ├── Clinic.java
│ │ ├── User.java
│ │ └── ... (other entities)
│ │
│ ├── cms/utils/
│ │ ├── PasswordUtils.java
│ │ ├── TitleBarManager.java
│ │ └── PlaceholderTextField.java
│ │
│ ├── cms/view/
│ │ ├── components/
│ │ │ └── SidebarButton.java
│ │ ├── login/
│ │ │ └── SuperAdminLoginView.java
│ │ │ └── StaffLoginView.java
│ │ └── superadmin/
│ │ ├── SuperAdminDashboardView.java
│ │ ├── ClinicPanel.java
│ │ └── ... (other views)
│ │
│ └── resources/
│ └── icons/
│ ├── home.png
│ └── ... (other icons)
│
├── lib/ # Folder for dependency JARs
│ ├── mysql-connector-j-x.x.x.jar
│ ├── jbcrypt-0.4.jar
│ └── jfreechart-x.x.x.jar
│
└── database_schema.sql # SQL script to create all tables
```


## Setup and Installation

1.  **Database Setup:**
    *   Ensure you have MySQL Server installed and running.
    *   Create a new database named `clinicdb`.
    *   Execute the `database_schema.sql` script provided in the root of this project to create all the necessary tables.
    *   **Important:** Manually insert the first Super Admin record into the `super_admins` table. You can generate a hashed password using a simple Java script.
        ```sql
        -- Example (replace with your own hashed password)
        INSERT INTO super_admins (name, username, password) VALUES ('System Owner', 'superadmin', '$2a$12$...your_bcrypt_hash...');
        ```

2.  **IDE Setup (NetBeans):**
    *   Open the project in NetBeans.
    *   Right-click the project, go to `Properties -> Libraries`.
    *   Add all the required JAR files from the `/lib` folder to the project's classpath.
    *   Update the database credentials (`URL`, `USER`, `PASSWORD`) in `src/cms/model/database/DBConnection.java`.

3.  **Build the Project:**
    *   Right-click the project and select "Clean and Build".

## How to Run the Application

The application has two separate entry points:

1.  **For Super Admin:**
    *   Run the `SuperAdminMain.java` file.
    *   This will launch the `SuperAdminLoginView`.
    *   Login with the credentials you manually inserted into the `super_admins` table.

2.  **For Clinic Staff:**
    *   First, use the Super Admin application to create a new clinic and the first Clinic Admin user.
    *   Then, run the `StaffMain.java` file.
    *   This will launch the `StaffLoginView`.
    *   Login using the `Clinic Code`, `Username`, and `Password` of the Clinic Admin you just created.

## Workflow Overview

1.  The **Super Admin** logs in to create and manage clinics.
2.  When creating a clinic, the Super Admin also creates the first **Clinic Admin** user account for that clinic.
3.  The **Clinic Admin** logs in using their clinic's unique code and their credentials. They can then add other staff members like Doctors and Receptionists.
4.  **Doctors and Receptionists** log in similarly to perform their daily tasks like managing patients and appointments.

## Future Enhancements

-   Implement a full billing and invoicing module.
-   Add inventory management for medical supplies.
-   Develop a patient portal for viewing records online.
-   Integrate email/SMS notifications for appointment reminders.

## Author

Hi, I'm **Mithun Thomas** 👋

- 🎓 B.Tech CSE (2024–2028) @ College of Engineering, Kottarakkara
- 💻 Passionate about **problem-solving, DSA, and software development**
- 🌱 Currently exploring **Machine Learning, AI, and System Design**
- 📫 Reach me at: [mithun2003](https://github.com/mithun2003)

---

⭐ If you like this repository, don't forget to **star it** on GitHub!