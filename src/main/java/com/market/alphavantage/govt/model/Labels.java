package com.market.alphavantage.govt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Labels {
    public String record_date;
    public String src_line_nbr;
    public String record_fiscal_year;
    public String record_fiscal_quarter;
    public String record_calendar_year;
    public String record_calendar_quarter;
    public String record_calendar_month;
    public String record_calendar_day;

    public String debt_held_public_amt;
    public String intragov_hold_amt;
    public String tot_pub_debt_out_amt;

//Table 1

    public String parent_id;
    public String classification_id;
    public String classification_desc;
    public String current_month_gross_rcpt_amt;
    public String current_month_gross_outly_amt;
    public String current_month_dfct_sur_amt;
    public String table_nbr;
    public String print_order_nbr;
    public String line_code_nbr;
    public String data_type_cd;
    public String record_type_cd;
    public String sequence_level_nbr;
    public String sequence_number_cd;

//Table 2
    public String current_month_budget_amt;
    public String current_fytd_budget_amt;
    public String prior_fytd_budget_amt;
    public String current_year_budget_est_amt;
    public String next_year_budget_est_amt;


}
