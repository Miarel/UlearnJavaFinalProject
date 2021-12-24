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

    public String getSeriesReference()
    {
        return seriesReference;
    }
    public String toString() {
        return String.format("'%s',%s,'%s','%s','%s','%s','%s','%s','%s'", seriesReference, magnitude, subject,
                group, seriesTitle1, seriesTitle2, seriesTitle3, seriesTitle4, seriesTitle5);
    }
}
