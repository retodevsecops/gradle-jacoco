package com.consubanco.postgresql.adapters.loan;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface LoanApplicationDataRepository extends ReactiveCrudRepository<LoanApplicationData, Integer> {
    @Query("SELECT * FROM loan_application WHERE process_id = :processId")
    Flux<LoanApplicationData> findByProcessId(@Param("processId") String processId);
}