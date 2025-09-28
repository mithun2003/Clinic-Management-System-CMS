package cms.view.superadmin;

import cms.model.dao.ReportDAO;
import cms.utils.FontUtils;
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
    private boolean isDataLoaded = false;

    /**
     * Constructor: Sets up the initial layout and DAO. Does NOT load data.
     */
    public ReportsPanel() {
        this.reportDAO = new ReportDAO();

        // Set up the basic panel properties
        setLayout(new GridLayout(2, 2, 20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // Display a loading message initially
        showLoadingState();
    }

    /**
     * Displays a simple "Loading..." message.
     */
    private void showLoadingState() {
        this.removeAll(); // Clear any existing components
        setLayout(new BorderLayout());
        JLabel loadingLabel = new JLabel("Click 'Reports' to load data...", SwingConstants.CENTER);
        loadingLabel.setFont(FontUtils.getUiFont(Font.ITALIC, 18));
        loadingLabel.setForeground(Color.GRAY);
        add(loadingLabel, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    /**
     * Public method to be called when the panel is shown. It fetches data and
     * builds the charts.
     */
    public void loadReportData() {
        // Optional: If data is already loaded, don't reload unless necessary.
        // For simplicity, we'll reload every time for now. A more advanced
        // implementation could check a timestamp.

        // Use a background thread for database calls to keep the UI responsive
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private ChartPanel patientVolumeChart;
            private ChartPanel userRoleChart;
            private ChartPanel patientGrowthChart;

            @Override
            protected Void doInBackground() throws Exception {
                // This happens on a background thread
                patientVolumeChart = createPatientVolumeChart();
                userRoleChart = createUserRoleChart();
                patientGrowthChart = createPatientGrowthChart();
                return null;
            }

            @Override
            protected void done() {
                // This happens on the Event Dispatch Thread (EDT) after doInBackground is finished
                try {
                    // This is where you update the UI
                    removeAll();
                    setLayout(new GridLayout(2, 2, 20, 20));

                    patientVolumeChart.setBorder(BorderFactory.createTitledBorder("Patient Count per Clinic"));
                    add(patientVolumeChart);

                    userRoleChart.setBorder(BorderFactory.createTitledBorder("Staff Distribution by Role"));
                    add(userRoleChart);

                    patientGrowthChart.setBorder(BorderFactory.createTitledBorder("New Patients per Month"));
                    add(patientGrowthChart);

                    JPanel placeholder = new JPanel();
                    placeholder.setBorder(BorderFactory.createTitledBorder("Future Report"));
                    placeholder.setBackground(Color.WHITE);
                    add(placeholder);

                    revalidate();
                    repaint();
                    isDataLoaded = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    // Show an error message on the panel if something went wrong
                    removeAll();
                    setLayout(new BorderLayout());
                    JLabel errorLabel = new JLabel("Error loading reports. Please try again.", SwingConstants.CENTER);
                    errorLabel.setForeground(Color.RED);
                    add(errorLabel, BorderLayout.CENTER);
                    revalidate();
                    repaint();
                }
            }
        };
        worker.execute(); // Start the background task
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
        // 1. Specify <String> as the generic type for the keys.
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

// 2. Your DAO method already returns a correctly typed Map.
        Map<String, Integer> data = reportDAO.getUserCountByRole();

// 3. The rest of your code now works perfectly and is type-safe.
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            // The compiler knows that entry.getKey() is a String and entry.getValue() is a Number.
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
}
