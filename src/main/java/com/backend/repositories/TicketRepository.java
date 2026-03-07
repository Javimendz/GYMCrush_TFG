package com.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.domain.Ticket;
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>{
    
}
