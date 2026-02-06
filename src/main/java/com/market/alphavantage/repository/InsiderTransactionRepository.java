package com.market.alphavantage.repository;





import com.market.alphavantage.entity.InsiderTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsiderTransactionRepository
        extends JpaRepository<InsiderTransaction, String> {
}

