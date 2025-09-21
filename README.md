```
ClinicManagementSystem/
│
├── src/
│   ├── cms/
│   │
│   ├── model/                        // M = Models + DAO
│   │   ├── entities/
│   │   │   ├── SuperAdmin.java
│   │   │   ├── Clinic.java
│   │   │   ├── User.java
│   │   │   ├── Patient.java
│   │   │   ├── Doctor.java
│   │   │   ├── Receptionist.java
│   │   │   ├── Appointment.java
│   │   │   ├── Billing.java
│   │   │   ├── Prescription.java
│   │   │   ├── OTP.java
│   │   │
│   │   ├── dao/
│   │   │   ├── SuperAdminDAO.java
│   │   │   ├── ClinicDAO.java
│   │   │   ├── UserDAO.java
│   │   │   ├── PatientDAO.java
│   │   │   ├── DoctorDAO.java
│   │   │   ├── AppointmentDAO.java
│   │   │   ├── BillingDAO.java
│   │   │   ├── PrescriptionDAO.java
│   │   │   ├── OTPDAO.java
│   │   │
│   │   ├── database/
│   │       ├── DBConnection.java
│   │
│   │   ├── services/                // (optional business logic helpers)
│   │       ├── AuthService.java     // login + OTP handling
│   │       ├── ReportService.java   // encapsulated reporting logic
│
│   ├── view/                         // V = Swing UI
│   │   ├── login/
│   │   │   ├── SuperAdminLoginView.java
│   │   │   ├── ClinicLoginView.java
│   │   │   ├── StaffLoginView.java
│   │   │   ├── OTPVerificationView.java
│   │   │
│   │   ├── superadmin/
│   │   │   ├── SuperAdminDashboardView.java
│   │   │   ├── AddClinicView.java
│   │   │   ├── ManageClinicsView.java
│   │   │
│   │   ├── clinicadmin/
│   │   │   ├── ClinicAdminDashboardView.java
│   │   │   ├── AddUserView.java
│   │   │   ├── ReportsView.java
│   │   │
│   │   ├── doctor/
│   │   │   ├── DoctorDashboardView.java
│   │   │   ├── ViewAppointmentsView.java
│   │   │   ├── WritePrescriptionView.java
│   │   │
│   │   ├── receptionist/
│   │   │   ├── ReceptionistDashboardView.java
│   │   │   ├── AddPatientView.java
│   │   │   ├── BookAppointmentView.java
│   │   │   ├── BillingView.java
│   │   │
│   │   ├── common/
│   │       ├── HomeView.java
│
│   ├── controller/                   // C = Controllers talk between View & Model
│   │   ├── AuthController.java       // handles login, OTP validation
│   │   ├── ClinicController.java     // supervises clinic creation/update
│   │   ├── PatientController.java    // link patient form ↔ patient DAO
│   │   ├── DoctorController.java
│   │   ├── AppointmentController.java
│   │   ├── BillingController.java
│   │   ├── ReportController.java
│
│   ├── utils/                        // Extras
│   │   ├── OTPGenerator.java
│   │   ├── EmailUtil.java
│   │   ├── ValidationUtils.java
│   │   ├── LoggerUtil.java
│
│   └── Main.java                     // Main entry
│
├── resources/
│   ├── db/
│   │   ├── schema.sql
│   │   ├── sample_data.sql
│   ├── icons/
│   │   ├── login.png
│   │   ├── doctor.png
│   │   ├── patient.png
│   └── mail/
│       ├── email_template.html
│
└── nbproject/                 // NetBeans metadata