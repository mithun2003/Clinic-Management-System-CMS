package cms.model.dao;

import cms.model.database.DBConnection;
import cms.model.entities.Bill; // You will need to create this entity
import cms.model.entities.Enums;
import cms.utils.LoggerUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BillingDAO {

    /**
     * Fetches a list of all bills with a specific status for a given clinic.
     * 
     * @param clinicId The ID of the clinic.
     * @param status   The status to filter by (e.g., "Unpaid").
     * @return A list of Bill objects.
     */
    public List<Bill> getBillsByStatus(int clinicId, String status) {
        List<Bill> billList = new ArrayList<>();
        // This query joins multiple tables to get all the necessary display information
        String sql = "SELECT b.bill_id, b.amount, b.created_at, b.status, " +
                "p.name as patient_name, u.name as doctor_name, a.appointment_date " +
                "FROM billing b " +
                "JOIN appointments a ON b.appointment_id = a.appointment_id " +
                "JOIN patients p ON a.patient_id = p.patient_id " +
                "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                "JOIN users u ON d.user_id = u.user_id " +
                "WHERE a.clinic_id = ? AND b.status = ? AND a.status = 'Completed' " +
                "ORDER BY b.created_at DESC";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, clinicId);
            pst.setString(2, status);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Bill bill = new Bill();
                bill.setBillId(rs.getInt("bill_id"));
                bill.setAmount(rs.getDouble("amount"));
                bill.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                bill.setPatientName(rs.getString("patient_name"));
                bill.setDoctorName(rs.getString("doctor_name"));
                bill.setAppointmentDate(rs.getTimestamp("appointment_date").toLocalDateTime());
                bill.setStatus(Enums.BillingStatus.valueOf(rs.getString("status")));
                billList.add(bill);
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to fetch bills with status: " + status, e);
        }
        return billList;
    }

    /**
     * Updates the status of a bill (e.g., from 'Unpaid' to 'Paid').
     * 
     * @param billId    The ID of the bill to update.
     * @param newStatus The new status.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateBillStatus(int billId, Enums.BillingStatus newStatus) {
        String sql = "UPDATE billing SET status = ? WHERE bill_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, newStatus.name());
            pst.setInt(2, billId);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            LoggerUtil.logError("Failed to update status for bill ID: " + billId, e);
            return false;
        }
    }

    /**
 * Creates a new bill for a completed appointment.
 * @param appointmentId The ID of the completed appointment.
 * @param amount The amount to be billed.
 * @return true if the bill was created successfully, false otherwise.
 */
public boolean createBillForAppointment(int appointmentId, double amount) {
    // This query uses a SELECT to get the patient_id from the appointments table
    String sql = "INSERT INTO billing (appointment_id, patient_id, amount, status) " +
                 "SELECT ?, patient_id, ?, 'Unpaid' FROM appointments WHERE appointment_id = ?";
    
    // Optional: First check if a bill already exists to prevent duplicates
    // ...
    
    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setInt(1, appointmentId);
        pst.setDouble(2, amount);
        pst.setInt(3, appointmentId);
        return pst.executeUpdate() > 0;
    } catch (Exception e) {
        LoggerUtil.logError("Failed to create bill for appointment ID: " + appointmentId, e);
        return false;
    }
}

}