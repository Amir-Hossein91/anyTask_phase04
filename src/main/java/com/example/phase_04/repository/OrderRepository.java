package com.example.phase_04.repository;

import com.example.phase_04.entity.Customer;
import com.example.phase_04.entity.Order;
import com.example.phase_04.entity.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    @Query(value = """
             select o from Technician t
            join t.subAssistances s 
            join s.orders o 
            where t=?1 
            and s = o.subAssistance
            and (o.orderStatus = 'WAITING_FOR_TECHNICIANS_SUGGESTIONS'
            or o.orderStatus = 'CHOOSING_TECHNICIAN')""")
    Optional<List<Order>> findRelatedOrders (Technician technician);
    Optional<List<Order>> findByCustomer(Customer customer);
}
