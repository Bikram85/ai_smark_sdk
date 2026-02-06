package com.market.alphavantage.repository;

import com.market.alphavantage.entity.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SymbolRepository extends JpaRepository<Symbol, String> {

    List<Symbol> findByAssetType(String assetType);
}
