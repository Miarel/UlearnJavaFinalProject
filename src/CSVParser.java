import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class CSVParser {
    public ArrayList<Transactions> transactions = new ArrayList<>();
    public ArrayList<TransactionInfo> transactionInfo = new ArrayList<>();

    public CSVParser(String path) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
            String line;
            String[] values;
            int i = 0;
            while ((line = bufferedReader.readLine()) != null) {
                values = line.replace(",", ", ").split(",");
                for (int j = 1; j < values.length; j++) {
                    if (!values[j].equals(" ")) {
                        values[j] = values[j].substring(1);
                    }
                }
                if (i == 0) {
                    i = 1;
                } else {
                    transactions.add(new Transactions(i, values[0], values[1], values[2], values[3],
                            values[4], values[5]));
                    i++;
                    try {
                        TransactionInfo trValue = new TransactionInfo(values[0], Integer.parseInt(values[6]), values[7],
                                values[8], values[9], values[10], values[11], values[12], values[13]);
                        if (transactionInfo.size()==0)
                        {
                            transactionInfo.add(trValue);
                        }
                        else if (!transactionInfo.get(transactionInfo.size()-1).getSeriesReference().equals(values[0]))
                        {
                            transactionInfo.add(trValue);
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void filltransactioninfo() {
        for (TransactionInfo transaction : transactionInfo) {
            try {
                DBHelper.FillTransactionInfo(transaction.toString());
            } catch (Exception ignored) {
            }
        }
    }

    public void fillTransactions()
    {
        for (Transactions transaction: transactions)
            try {
                DBHelper.FillTransactions(transaction.toString());
            } catch (Exception ignored) {
            }
    }

}