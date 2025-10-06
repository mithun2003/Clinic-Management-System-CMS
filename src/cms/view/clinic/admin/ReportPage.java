package cms.view.clinic.admin;

import cms.model.dao.ReportDAO;
import cms.model.entities.User;
import cms.utils.FontUtils;
import java.awt.*;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class ReportPage extends JPanel {

    private final ReportDAO reportDAO;
    private final User loggedInAdmin;

    public ReportPage(User admin) {
        this.loggedInAdmin = admin;
        this.reportDAO = new ReportDAO();

        setLayout(new GridLayout(2, 2, 20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        showPlaceholder();
    }

    private void showPlaceholder() {
        this.removeAll();
        setLayout(new BorderLayout());
        JLabel placeholder = new JLabel("Click 'View Reports' to load data visualizations.", SwingConstants.CENTER);
        placeholder.setFont(FontUtils.getUiFont(Font.ITALIC, 18));
        placeholder.setForeground(Color.GRAY);
        add(placeholder, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    public void loadReportData() {
        // Show a loading message while data is being fetched
        this.removeAll();
        setLayout(new BorderLayout());
        add(new JLabel("Generating reports, please wait...", SwingConstants.CENTER), BorderLayout.CENTER);
        this.revalidate();
        this.repaint();

        SwingWorker<JPanel, Void> worker = new SwingWorker<>() {
            @Override
            protected JPanel doInBackground() throws Exception {
                // This happens on a background thread
                JPanel chartContainer = new JPanel(new GridLayout(2, 2, 20, 20));
                chartContainer.setBackground(Color.WHITE);

                // --- Chart 1: Appointments This Week (Line Chart) ---
                chartContainer.add(createAppointmentsChart());

                // --- Chart 2: Staff Distribution (Pie Chart) ---
                chartContainer.add(createStaffDistributionChart());

                // --- Chart 3: New Patients This Month (Bar Chart) ---
                chartContainer.add(createNewPatientsChart());

                // --- Chart 4: Doctor Performance (Bar Chart)
                chartContainer.add(createDoctorPerformanceChart());

                return chartContainer;
            }

            @Override
            protected void done() {
                try {
                    JPanel resultPanel = get();
                    removeAll();
                    setLayout(new BorderLayout());
                    add(resultPanel, BorderLayout.CENTER);
                    revalidate();
                    repaint();
                } catch (Exception e) {
                    // Handle exceptions
                }
            }
        };
        worker.execute();
    }

    // --- New Chart Creation Methods ---
    private ChartPanel createAppointmentsChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Integer> data = reportDAO.getAppointmentsLast7Days(loggedInAdmin.getClinicId());
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            dataset.addValue(entry.getValue(), "Appointments", entry.getKey());
        }
        JFreeChart lineChart = ChartFactory.createLineChart(
                "Appointments This Week", "Date", "Number of Appointments",
                dataset, PlotOrientation.VERTICAL, false, true, false);
        styleChart(lineChart);
        return new ChartPanel(lineChart);
    }

    private ChartPanel createStaffDistributionChart() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        Map<String, Integer> data = reportDAO.getStaffCountByRoleForClinic(loggedInAdmin.getClinicId());
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Staff Distribution by Role", dataset, true, true, false);
        stylePieChart(pieChart);
        return new ChartPanel(pieChart);
    }

    private ChartPanel createNewPatientsChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Integer> data = reportDAO.getNewPatientsLast30Days(loggedInAdmin.getClinicId());
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            dataset.addValue(entry.getValue(), "New Patients", entry.getKey());
        }
        JFreeChart barChart = ChartFactory.createBarChart(
                "New Patients (Last 30 Days)", "Date", "Number of New Patients",
                dataset, PlotOrientation.VERTICAL, false, true, false);
        styleBarChart(barChart);
        return new ChartPanel(barChart);
    }

    private ChartPanel createDoctorPerformanceChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // You'll need to add the getDoctorPerformance method to your ReportDAO
        Map<String, Integer> data = reportDAO.getDoctorPerformance(loggedInAdmin.getClinicId());
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            dataset.addValue(entry.getValue(), "Completed Appointments", entry.getKey());
        }
        // A horizontal bar chart is great for rankings
        JFreeChart barChart = ChartFactory.createBarChart(
                "Doctor Performance (Last 30 Days)", "Doctor", "Completed Appointments",
                dataset, PlotOrientation.VERTICAL, false, true, false);
        styleChart(barChart);
        return new ChartPanel(barChart);
    }

    // --- Styling Helpers ---
    private void styleChart(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setFont(FontUtils.getUiFont(Font.BOLD, 18));

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(248, 249, 250));
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.setOutlinePaint(null); // Remove the plot border

        // Make axis labels smaller
        plot.getDomainAxis().setLabelFont(FontUtils.getUiFont(Font.PLAIN, 12));
        plot.getDomainAxis().setTickLabelFont(FontUtils.getUiFont(Font.PLAIN, 10));
        plot.getRangeAxis().setLabelFont(FontUtils.getUiFont(Font.PLAIN, 12));
    }

    /**
     * Applies specific styling to a Bar Chart to remove gradients and set colors.
     */
    private void styleBarChart(JFreeChart chart) {
        // First, apply the common styles
        styleChart(chart);

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        // 👇 THIS IS THE KEY FIX: Disable the ugly gradient effect
        renderer.setBarPainter(new StandardBarPainter());

        // Set a nice, solid color for the bars
        renderer.setSeriesPaint(0, new Color(23, 162, 184)); // A nice blue/teal
        renderer.setShadowVisible(false); // Remove shadows
    }

    private void stylePieChart(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        chart.getTitle().setFont(FontUtils.getUiFont(Font.BOLD, 16));
        plot.setLabelFont(FontUtils.getUiFont(Font.PLAIN, 12));
    }
}
