import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ChartCreator {

    public static DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    public static void CreateChart() {
        // write your code here

        JFreeChart chart = ChartFactory.createBarChart(
                "Сумма всех переводов по месяцам",
                "Номер месяца",
                "Сумма перевода в долларах",
                dataset
        );
        try
        {
            Path path = Paths.get("src\\Chart.jpeg");
            ChartUtilities.saveChartAsJPEG(new File(path.toString()), chart, 1920, 1080);
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
}

