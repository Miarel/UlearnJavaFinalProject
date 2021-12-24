# UlearnJava Итоговый проект (4 вариант)

Для реализации поставленной задачи скачиваем и открываем файл "переводы.csv".

![CSV в Excel](https://github.com/Miarel/UlearnJavaFinalProject/blob/main/Screenshots/CSV-file.png)

Видно что CSV-файл состоит из 14 столбцов: Series_reference, Period, Data_value, Suppressed, STATUS, UNITS, Magnitude, Subject, Group, Series_title_1, Series_title_2, Series_title_3, Series_title_4, Series_title_5.

Создаём новый проект с подключенным модулем sqlite-jdbc для работы с базой данных и библиотекой JFreeChart.

![Cfg1](https://github.com/Miarel/UlearnJavaFinalProject/blob/main/Screenshots/Cfg1.png)

![Cfg2](https://github.com/Miarel/UlearnJavaFinalProject/blob/main/Screenshots/Cfg2.png)

Изначально у нас таблица в 1 НФ, чтобы привести её к 2 НФ добавим счетчик, который будет первичным ключом в нашей новой таблице. Так как по заданию нужно привести таблицы к 3-ей нормальной форме, разделем таблицу на две. Первая будет соержать в себе идентификатор, референс, дата, перевод (число + единицы измерения) и статус. Во второй таблице будет содержаться информация о деталях перевода: референс(первичный ключ), magnitude, субъект, группа и все заголовки. В качестве внешнего ключа у нас будет столбец "Series_reference".

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
Чтобы сложить значения из CSV-файла в базу данных, будем брать по отдельности каждую строку и разбивать её по запятой, но перед этим посмотрим что мы получили.

![Parser](https://github.com/Miarel/UlearnJavaFinalProject/blob/main/Screenshots/Parser.png)

Смотрим на строки и видим, что у некотрых столбцов отсутсвуют значения и идут по 2-3 запятые подряд. То есть если сплитануть строку прямо сейчас у нас получатся массивы разной длины и их будет труднее складывать в классы, поэтому перед этим заменим все "," на ", ".

![updParser](https://github.com/Miarel/UlearnJavaFinalProject/blob/main/Screenshots/updParser.png)

Стало чуть лучше, но у некотрых полей класса принимаемый тип значений - int и значение " 6" просто так числом не станет. Чтобы удалить незначащий пробел перед значениями столбцов пройдемся в цикле от 1 до длины массива и удалим пробел если значение не равно пробелу.

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

Теперь у нас нет лишних пробелов и все массивы одной длины, значит можно складывать данные в ArrayList.
После того как положили все строки в лист посмотрим на них.В CSV-файле 18184 строк, то есть 18183 строк с значениями и 1 с названиями столбцов, а у нас как раз 18183 значения.

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
![dbINfo](https://github.com/Miarel/UlearnJavaFinalProject/blob/main/Screenshots/FillInfo.png)

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
![dbTable](https://github.com/Miarel/UlearnJavaFinalProject/blob/main/Screenshots/FillTable.png)
