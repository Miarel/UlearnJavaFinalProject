# UlearnJava Итоговый проект (4 вариант)

Для реализации поставленной задачи скачиваем и открываем файл "переводы.csv".

![CSV в Excel](https://github.com/Miarel/UlearnJavaFinalProject/blob/main/Screenshots/CSV-file.png)

Видим, что CSV-файл состоит из 14 столбцов: Series_reference, Period, Data_value, Suppressed, STATUS, UNITS, Magnitude, Subject, Group, Series_title_1, Series_title_2, Series_title_3, Series_title_4, Series_title_5.

Создаём новый проект с и подключаем библиотеку JFreeChart и модуль sqlite-jdbc для работы с базой данных.

![Cfg1](https://github.com/Miarel/UlearnJavaFinalProject/blob/main/Screenshots/Cfg1.png)

![Cfg2](https://github.com/Miarel/UlearnJavaFinalProject/blob/main/Screenshots/Cfg2.png)

Изначально у нас таблица в 1 НФ, чтобы привести её к 2 НФ добавим счетчик, который будет первичным ключом в нашей новой таблице. Так как по заданию нужно привести таблицы к 3-ей нормальной форме, разделим таблицу на две. Первая будет содержать в себе идентификатор, референс, дата, перевод (число единицы измерения) и статус. Во второй таблице будет содержаться информация о деталях перевода: референс (первичный ключ), magnitude, субъект, группа и все заголовки. В качестве внешнего ключа у нас будет столбец "Series_reference".

Перед тем как создавать таблицы в базе данных добавим в проект класс `Transactions` со всеми переводами.

```java

import java.util.Date;

public class Transactions {
    public int id;
    public String seriesReference;
    public Date period;
    public String dataValue;
    public String suppressed;
    public String status;
    public String units;

    public Transactions(int id, String seriesReference, Date period, String dataValue,
                        String suppressed, String status, String units) {
        this.id = id;
        this.seriesReference = seriesReference;
        this.period = period;
        this.dataValue = dataValue;
        this.suppressed = suppressed;
        this.status = status;
        this.units = units;
    }
 }
```
И класс `TransactionInfo` с информацией о группе перевода.

```java

public class TransactionInfo {
    public String seriesReference;
    public int magnitude;
    public String subject;
    public String group;
    public String seriesTitle1;
    public String seriesTitle2;
    public String seriesTitle3;
    public String seriesTitle4;
    public String seriesTitle5;

    public TransactionInfo(String seriesReference, int magnitude, String subject, String group, String seriesTitle1,
                           String seriesTitle2, String seriesTitle3, String seriesTitle4, String seriesTitle5) {
        this.seriesReference = seriesReference;
        this.magnitude = magnitude;
        this.subject = subject;
        this.group = group;
        this.seriesTitle1 = seriesTitle1;
        this.seriesTitle2 = seriesTitle2;
        this.seriesTitle3 = seriesTitle3;
        this.seriesTitle4 = seriesTitle4;
        this.seriesTitle5 = seriesTitle5;
    }
    
}
```

## CSVParser
Напишем `парсер` для CSV-файла:

```java
import java.io.BufferedReader;
import java.io.FileReader;

public class CSVParser {
public CSVParser(String path) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

Чтобы сложить значения из CSV-файла в базу данных, будем брать по отдельности каждую строку и разбивать её по запятой, но перед этим посмотрим, что мы получили.

![Parser](https://github.com/Miarel/UlearnJavaFinalProject/blob/main/Screenshots/Parser.png)

Смотрим на строки и видим, что у некоторых столбцов отсутствуют значения и идут по 2-3 запятые подряд. То есть если сплитануть строку прямо сейчас у нас получатся массивы разной длины и их будет труднее складывать в классы, поэтому перед этим заменим все "," на ", ".

![updParser](https://github.com/Miarel/UlearnJavaFinalProject/blob/main/Screenshots/updParser.png)

Стало чуть лучше, но у некоторых полей класса принимаемый тип значений - int и значение " 0" просто так числом не станет. Чтобы удалить незначащий пробел перед значениями столбцов, пройдемся в цикле от 1 до длины массива и удалим пробел если значение не равно пробелу.

```java
for (int j = 1; j < values.length; j++) 
{
    if (!values[j].equals(" ")) 
    {
        values[j] = values[j].substring(1);
    }
}
```

![Correct values](https://github.com/Miarel/UlearnJavaFinalProject/blob/main/Screenshots/CorrectValues.png)

Теперь у нас нет лишних пробелов и все массивы одной длины, значит можно складывать данные в ArrayList. В CSV-файле 18184 строк, то есть 18183 строк со значениями и 1 с названиями столбцов, а у нас как раз 18183 значения.

![Transactions](https://github.com/Miarel/UlearnJavaFinalProject/blob/main/Screenshots/Transactions.png)

![CSVTransactions](https://github.com/Miarel/UlearnJavaFinalProject/blob/main/Screenshots/CSVTransactions.png)

Все строки с информацией:

![TransactionInfo](https://github.com/Miarel/UlearnJavaFinalProject/blob/main/Screenshots/TransactionInfo.png)

## Sqlite3

После того как сохранили информацию о переводах, можно переносить её в БД. Создаём базу данных и подключаем к проекту.

```java
import java.sql.*;

public class conn {
    public static Connection connection;
    public static Statement statement;

    public static void Conn() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:ulearnJava.db");
        Statement statement = connection.createStatement();

        System.out.println("Connected");
    }
}
```

Далее создаем в БД таблицы и устанавливаем связь между ними.

```java
public static void CreateDB() throws SQLException {
        statement = connection.createStatement();

        statement.execute("CREATE TABLE if not exists 'TransactionInfo' ( 'Series_reference' STRING  PRIMARY KEY," +
                "'Magnitude' INTEGER , 'Subject' STRING, 'Group' STRING, 'Series_title_1' STRING," +
                "'Series_title_2' STRING, 'Series_title_3' STRING, 'Series_title_4' STRING, 'Series_title_5' STRING);");
        statement.execute("CREATE TABLE if not exists 'TransactionsTable' ('Id' INTEGER PRIMARY KEY," +
                " 'Series reference' STRING REFERENCES 'TransactionInfo' (Series_reference), 'Period' DATE," +
                "'Data_value' STRING, 'Suppressed' STRING, 'Status' STRING, 'Units' STRING );");
    }
```

Результат SQL запроса:

![Tables](https://github.com/Miarel/UlearnJavaFinalProject/blob/main/Screenshots/TransactionTable.png)

![InfoTables](https://github.com/Miarel/UlearnJavaFinalProject/blob/main/Screenshots/TransactionInfoTable.png)

Создаем SQL `запрос` на добавление значений:

```java
public static void FillTransactionInfo(String queryValue) throws SQLException {
        String query = "INSERT INTO 'TransactionInfo' ('Series_reference', 'Magnitude', 'Subject', 'Group', " +
                "'Series_title_1', 'Series_title_2', 'Series_title_3', 'Series_title_4', 'Series_title_5')";
        query += String.format("VALUES (%s)",queryValue);
```

Вся информация о группах переводов была добавлена в таблицу:
![dbINfo](https://github.com/Miarel/UlearnJavaFinalProject/blob/main/Screenshots/FillTable.png)

Похожий `запрос` для переводов:
```java
public static void FillTransactions(String queryValue) throws SQLException {
    String query = "INSERT INTO 'TransactionsTable' ('Id', 'Series_reference', 'Period', 'Data_value', " +
            "'Suppressed', 'Status', 'Units')";
    query += String.format("VALUES (%s)", queryValue);
    statement.execute(query);
}
```

И 18 тысяч переводов в таблице:

![dbTable](https://github.com/Miarel/UlearnJavaFinalProject/blob/main/Screenshots/FillInfo.png)

## SQL запросы

После того как заполнили таблицы можно написать запросы к ним. Для первого задания создаем `метод` который будет принимать год и проходить от 1 месяца до 12 и выполнять запрос к таблице.

```java
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    }
}
```

![res](https://github.com/Miarel/UlearnJavaFinalProject/blob/main/Screenshots/results.png)

Создаём `запрос`, который выведет количество переводов за месяц. Будем считать за перевод все операции в долларах даже те, где скрыто значение или где его просто нет.

```java
String countQuery = "SELECT COUNT(Data_value) AS 'Count', Period FROM 'TransactionsTable' " +
                "WHERE UNITS = 'Dollars' GROUP BY Period";
```

![Count](https://github.com/Miarel/UlearnJavaFinalProject/blob/main/Screenshots/Count.png)

Для среднего перевода создадим отдельный `запрос`, так как тут будем считать только между переводов, у которых есть числовое значение.

```java
String averageCount = "SELECT AVG(Data_value) AS 'AverageTransaction', Period FROM 'TransactionsTable' " +
                "WHERE UNITS = 'Dollars' AND Data_value!=' ' GROUP BY Period";
```

![Average](https://github.com/Miarel/UlearnJavaFinalProject/blob/main/Screenshots/Average.png)

В последнем задании сначала напишем SQL `запрос` который будет выводить месяца с 1 по 12.

```java
SELECT Period FROM 'TransactionsTable' WHERE Period BETWEEN %s.01 AND %s.12 GROUP BY Period
```

Теперь в этом промежутке ищем максимальное и минимальное значения.

```java
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
```

![Result](https://github.com/Miarel/UlearnJavaFinalProject/blob/main/Screenshots/MaxAndMin.png)

С помощью библиотеки JFreeChart строим гистограмму.

```java
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
            ChartUtilities.saveChartAsJPEG(new File("src\\Chart.jpeg"), chart, 1920, 1080);
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
}
```

![End](https://github.com/Miarel/UlearnJavaFinalProject/blob/main/Screenshots/end.png)
