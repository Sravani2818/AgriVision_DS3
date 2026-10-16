package agrivision;

public class CorpusRecord {
    public final int recordNo;
    public final String sourceId;
    public final String idx;
    public final String crop;
    public final String state;
    public final String district;
    public final String sector;
    public final String queryType;
    public final String season;
    public final String question;
    public final String answer;

    public CorpusRecord(int recordNo, String sourceId, String idx, String crop,
                        String state, String district, String sector,
                        String queryType, String season, String question,
                        String answer) {
        this.recordNo = recordNo;
        this.sourceId = sourceId;
        this.idx = idx;
        this.crop = crop;
        this.state = state;
        this.district = district;
        this.sector = sector;
        this.queryType = queryType;
        this.season = season;
        this.question = question;
        this.answer = answer;
    }
}
