import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Tasks {
    public static ResultSet resSet;
    public static Statement statement;

    static {
        try {
            statement = DBHelper.connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void GetSumForYear(String year) throws ParseException, SQLException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM");
        Date startDate = dateFormat.parse(year + ".01"),
                endDate = dateFormat.parse(year + ".12");
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);

        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        while (!start.after(end)) {
            System.out.printf("Sum for %s ", dateFormat.format(start.getTime()));
            getSumForMonth(dateFormat.format(start.getTime()));
            start.add(Calendar.MONTH, 1);
        }
    }

    public static void getSumForMonth(String month) throws SQLException {
        //Task 1
        String query = "SELECT SUM(Data_value) AS 'MonthSum' FROM 'TransactionsTable' WHERE Data_value!=' ' " +
                "AND UNITS = 'Dollars' AND Period = ";
        query += String.format("'%s'", month);
        resSet = statement.executeQuery(query);
        System.out.println("is " + resSet.getString("MonthSum"));
        ChartCreator CC = new ChartCreator();
        CC.dataset.addValue(Double.parseDouble(resSet.getString("MonthSum")), month.substring(5,7), "2020");
    }

    public static void getAvgAndCount() throws SQLException {
        //Task 2
        String countQuery = "SELECT COUNT(Data_value) AS 'Count', Period FROM 'TransactionsTable' " +
                "WHERE UNITS = 'Dollars' GROUP BY Period";
        String averageCount = "SELECT AVG(Data_value) AS 'AverageTransaction', Period FROM 'TransactionsTable' " +
                "WHERE UNITS = 'Dollars' AND Data_value!=' ' GROUP BY Period";
        resSet = statement.executeQuery(countQuery);
        while (resSet.next()) {
            System.out.print("Transactions count for " + resSet.getString("Period"));
            System.out.println(" is " + resSet.getString("Count"));
        }
        resSet = statement.executeQuery(averageCount);
        while (resSet.next()) {
            System.out.print("Average transaction for " + resSet.getString("Period"));
            System.out.println(" is " + new DecimalFormat("#0.00").format(resSet.getDouble
                    ("AverageTransaction")) + " $");
        }
    }

    public static void getMaxAndMin(String year) throws SQLException {
        //Task 3
        String query = String.format("SELECT MAX(Data_value) AS 'Max', MIN(Data_value) AS 'Min' " +
                "FROM 'TransactionsTable' WHERE Period IN (SELECT Period FROM 'TransactionsTable' " +
                "WHERE Period BETWEEN %s.01 AND %s.12 GROUP BY Period) AND " +
                "Data_value!=' ' AND UNITS = 'Dollars' ", year, year);
        resSet = statement.executeQuery(query);
        while (resSet.next()) {
            System.out.println(("MaxTransaction" + " for " + year + " is " + resSet.getString("Max")));
            System.out.println("MinTransaction" + " for " + year + " is " + resSet.getString("Min"));
        }
    }
}
