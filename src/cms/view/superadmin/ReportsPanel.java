package cms.view.superadmin;

import cms.model.dao.ReportDAO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

public class ReportsPanel extends JPanel {

    private final ReportDAO reportDAO;

    public ReportsPanel() {
        this.reportDAO = new ReportDAO();
        setLayout(new GridLayout(2, 2, 20, 20)); // A 2x2 grid for reports
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // --- Report 1: Clinic by Patient Volume (Bar Chart) ---
        ChartPanel patientVolumeChart = createPatientVolumeChart();
        patientVolumeChart.setBorder(BorderFactory.createTitledBorder("Patient Count per Clinic"));
        add(patientVolumeChart);

        // --- Report 2: User Role Distribution (Pie Chart) ---
        ChartPanel userRoleChart = createUserRoleChart();
        userRoleChart.setBorder(BorderFactory.createTitledBorder("Staff Distribution by Role"));
        add(userRoleChart);

        // --- Report 3: Patient Growth Over Time (Line Chart) ---
        ChartPanel patientGrowthChart = createPatientGrowthChart();
        patientGrowthChart.setBorder(BorderFactory.createTitledBorder("New Patients per Month"));
        add(patientGrowthChart);
        
        // --- Placeholder for a 4th report ---
        JPanel placeholder = new JPanel();
        placeholder.setBorder(BorderFactory.createTitledBorder("Future Report"));
        placeholder.setBackground(Color.WHITE);
        add(placeholder);
    }

    private ChartPanel createPatientVolumeChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Integer> data = reportDAO.getPatientCountPerClinic();
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            dataset.addValue(entry.getValue(), "Patients", entry.getKey());
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Patient Volume by Clinic",
                "Clinic", "Number of Patients",
                dataset, PlotOrientation.VERTICAL, true, true, false);
        
        styleChart(barChart);
        return new ChartPanel(barChart);
    }

    private ChartPanel createUserRoleChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Integer> data = reportDAO.getUserCountByRole();
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Staff Roles", dataset, true, true, false);

        // Styling for Pie Chart
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setShadowPaint(null);
        pieChart.setBackgroundPaint(Color.WHITE);

        return new ChartPanel(pieChart);
    }
    
    private ChartPanel createPatientGrowthChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Integer> data = reportDAO.getNewPatientsPerMonth();
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            dataset.addValue(entry.getValue(), "New Patients", entry.getKey());
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Monthly Patient Growth",
                "Month", "Number of New Patients",
                dataset, PlotOrientation.VERTICAL, true, true, false);
        
        styleChart(lineChart); // Use the same styling as the bar chart
        return new ChartPanel(lineChart);
    }

    // A helper method for consistent chart styling
    private void styleChart(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(240, 240, 240));
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
    }

    // Call this method when navigating to the reports page to get fresh data
    public void refreshData() {
        // For now, we are creating new charts on load.
        // A more advanced implementation would update the datasets of existing charts.
        this.removeAll();
        // Re-add all components
        ChartPanel patientVolumeChart = createPatientVolumeChart();
        patientVolumeChart.setBorder(BorderFactory.createTitledBorder("Patient Count per Clinic"));
        add(patientVolumeChart);

        ChartPanel userRoleChart = createUserRoleChart();
        userRoleChart.setBorder(BorderFactory.createTitledBorder("Staff Distribution by Role"));
        add(userRoleChart);

        ChartPanel patientGrowthChart = createPatientGrowthChart();
        patientGrowthChart.setBorder(BorderFactory.createTitledBorder("New Patients per Month"));
        add(patientGrowthChart);
        
        JPanel placeholder = new JPanel();
        placeholder.setBorder(BorderFactory.createTitledBorder("Future Report"));
        placeholder.setBackground(Color.WHITE);
        add(placeholder);
        
        this.revalidate();
        this.repaint();
    }
}