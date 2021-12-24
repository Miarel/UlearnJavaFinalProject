import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.ParseException;

public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, ParseException {
        Path path = Paths.get("src\\Переводы.csv");
        CSVParser parser = new CSVParser(path.toString());
        parser.filltransactioninfo();
        DBHelper.ConnectDB();
        DBHelper.CreateDB();
        parser.filltransactioninfo();
        parser.fillTransactions();
        Tasks.GetSumForYear("2020"); // Task 1
        Tasks.getAvgAndCount(); // Task 2
        Tasks.getMaxAndMin("2014"); // Task 3
        Tasks.getMaxAndMin("2016"); // Task 3
        Tasks.getMaxAndMin("2020"); // Task 3
        ChartCreator CC = new ChartCreator();
        CC.CreateChart();
    }
}
