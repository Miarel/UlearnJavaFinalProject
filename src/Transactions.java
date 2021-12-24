public class Transactions {
    public int id;
    public String seriesReference;
    public String period;
    public String dataValue;
    public String suppressed;
    public String status;
    public String units;

    public Transactions(int id, String seriesReference, String period, String dataValue,
                        String suppressed, String status, String units) {
        this.id = id;
        this.seriesReference = seriesReference;
        this.period = period;
        this.dataValue = dataValue;
        this.suppressed = suppressed;
        this.status = status;
        this.units = units;
    }

    public String toString() {
        return String.format("'%s','%s','%s','%s','%s','%s','%s'", id, seriesReference, period,
                dataValue, suppressed, status, units);
    }

}
