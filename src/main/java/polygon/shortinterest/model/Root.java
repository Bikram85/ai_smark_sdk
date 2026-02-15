package polygon.shortinterest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Root{
    public String status;
    public String request_id;
    public ArrayList<Result> results;
    public String next_url;
}

