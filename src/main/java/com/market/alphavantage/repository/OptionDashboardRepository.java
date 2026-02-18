package com.market.alphavantage.repository;

import com.market.alphavantage.entity.OptionDashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OptionDashboardRepository extends JpaRepository<OptionDashboard, Long> {

    // Find all entries by symbol
    Optional<OptionDashboard> findBySymbol(String symbol);

    Optional<OptionDashboard> findTopBySymbolOrderByIdDesc(String symbol);
    Optional<OptionDashboard> findBySymbolAndContractIdAndDate(String symbol, String contractId, LocalDate date);
    Optional<OptionDashboard> findBySymbolAndContractId(String symbol, String contractId);

    List<OptionDashboard> findBySymbolOrderByDateAsc(String symbol);

    Optional<OptionDashboard>
    findFirstBySymbolAndDateOrderByIdDesc(
            String symbol,
            LocalDate date
    );

    // Dropdown expiration list
    @Query("""
        SELECT DISTINCT o.date
        FROM OptionDashboard o
        WHERE o.symbol = :symbol
        ORDER BY o.date
    """)
    List<LocalDate> findDistinctDatesBySymbol(String symbol);

}
