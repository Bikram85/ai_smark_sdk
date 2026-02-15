package polygon.shortinterest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result{
    public String settlement_date;
    public String ticker;
    public int short_interest;
    public int avg_daily_volume;
    public double days_to_cover;
}
