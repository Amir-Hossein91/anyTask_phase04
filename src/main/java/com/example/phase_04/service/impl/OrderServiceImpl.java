package com.example.phase_04.service.impl;

import com.example.phase_04.entity.*;
import com.example.phase_04.entity.enums.OrderStatus;
import com.example.phase_04.exceptions.DeactivatedTechnicianException;
import com.example.phase_04.exceptions.NotFoundException;
import com.example.phase_04.repository.OrderRepository;
import com.example.phase_04.service.OrderService;
import com.example.phase_04.utility.Constants;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final CustomerServiceImpl customerService;
    private final AssistanceServiceImpl assistanceService;
    private final SubAssistanceServiceImpl subAssistanceService;
    private final TechnicianServiceImpl technicianService;
    private final PersonServiceImpl personService;

    @PersistenceContext
    private EntityManager em;

    public OrderServiceImpl(OrderRepository repository,
                            @Lazy CustomerServiceImpl customerService,
                            @Lazy AssistanceServiceImpl assistanceService,
                            @Lazy SubAssistanceServiceImpl subAssistanceService,
                            @Lazy TechnicianServiceImpl technicianService,
                            @Lazy PersonServiceImpl personService) {
        super();
        this.repository = repository;
        this.customerService = customerService;
        this.assistanceService = assistanceService;
        this.subAssistanceService = subAssistanceService;
        this.technicianService = technicianService;
        this.personService = personService;
    }

    @Transactional
    public Order makeOrder(String subAssistanceTitle, String assistanceTitle, OrderDescription orderDescription) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = customerService.findByUsername(username);
        if (customer == null)
            throw new IllegalArgumentException("Only a customer can make an order");
        Assistance assistance = assistanceService.findAssistance(assistanceTitle);
        if (assistance == null)
            throw new NotFoundException(Constants.ASSISTANCE_NOT_FOUND);

        SubAssistance subAssistance = subAssistanceService.findSubAssistance(subAssistanceTitle, assistance);
        if (subAssistance == null)
            throw new NotFoundException(Constants.NO_SUCH_SUBASSISTANCE);

        if (orderDescription.getCustomerSuggestedPrice() < subAssistance.getBasePrice())
            throw new IllegalArgumentException(Constants.INVALID_SUGGESTED_PRICE);

        if (orderDescription.getCustomerDesiredDateAndTime().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException(Constants.DATE_BEFORE_NOW);

        Order order = Order.builder()
                .subAssistance(subAssistance)
                .customer(customer)
                .orderRegistrationDateAndTime(LocalDateTime.now())
                .orderDescription(orderDescription)
                .orderStatus(OrderStatus.WAITING_FOR_TECHNICIANS_SUGGESTIONS)
                .technicianScore(1)
                .isTechnicianScored(false)
                .build();

        order = saveOrUpdate(order);

        customer.setOrderCount(customer.getOrderCount() + 1);
        customerService.saveOrUpdate(customer);

        return order;
    }

    @Override
    @Transactional
    public Order saveOrUpdate(Order t) {
        return repository.save(t);
    }

    @Override
    @Transactional
    public void delete(Order t) {
        repository.delete(t);
    }

    @Override
    public Order findById(long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("\nCould not find order with id = " + id));
    }

    @Override
    public List<Order> findAll() {
        return repository.findAll();
    }

    public List<Order> findRelatedOrders(Technician technician) {
        return repository.findRelatedOrders(technician).orElseThrow(
                () -> new NotFoundException(Constants.NO_RELATED_ORDERS)
        );
    }

    @Override
    @Transactional
    public void sendTechnicianSuggestion(Technician technician, Order order, TechnicianSuggestion technicianSuggestion) {
        List<Order> orders = repository.findRelatedOrders(technician).orElseThrow(
                () -> new NotFoundException(Constants.NO_RELATED_ORDERS)
        );
        boolean isFound = false;
        for (Order o : orders) {
            if (o.getId() == order.getId()) {
                isFound = true;
                break;
            }
        }
        if (!isFound)
            throw new NotFoundException(Constants.ORDER_IS_NOT_RELATED);
        if (technicianSuggestion != null) {

            if (technicianSuggestion.getTechSuggestedPrice() < order.getSubAssistance().getBasePrice())
                throw new IllegalArgumentException(Constants.INVALID_SUGGESTED_PRICE);

            if (technicianSuggestion.getTechSuggestedDate().isBefore(order.getOrderDescription().getCustomerDesiredDateAndTime()))
                throw new IllegalArgumentException(Constants.DATE_BEFORE_CUSTOMER_DESIRED);

            order.getTechnicianSuggestions().add(technicianSuggestion);
            saveOrUpdate(order);
        }
    }

    @Override
    public List<Order> findByCustomer(Customer customer) {
        return repository.findByCustomer(customer).orElseThrow(
                () -> new NotFoundException(Constants.NO_ORDERS_FOR_CUSTOMER)
        );
    }

    @Transactional
    public List<Order> managerFilterOrders(long customerId,
                                           long technicianId,
                                           Optional<LocalDateTime> from,
                                           Optional<LocalDateTime> until,
                                           Optional<OrderStatus> status,
                                           Optional<String> assistanceTitle,
                                           Optional<String> subAssistanceTitle) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> orderRoot = cq.from(Order.class);

        List<Predicate> predicates = new ArrayList<>();


        if(customerId != 0){
            Customer fetchedCustomer = customerService.findById(customerId);
            predicates.add(cb.equal(orderRoot.get("customer"),fetchedCustomer));
        }

        if(technicianId != 0){
            Technician fetchedTechnician = technicianService.findById(technicianId);
            predicates.add(cb.equal(orderRoot.get("technician"),fetchedTechnician));
        }

        from.map(f -> predicates.add(cb.greaterThanOrEqualTo(orderRoot.get("orderRegistrationDateAndTime"), f)));
        until.map(u -> predicates.add(cb.lessThanOrEqualTo(orderRoot.get("orderRegistrationDateAndTime"), u)));
        status.map(st -> predicates.add((cb.equal(orderRoot.get("orderStatus"),st))));

        if(assistanceTitle.isPresent()){
            List<Predicate> subAssistancePredicates = new ArrayList<>();
            Assistance assistance = assistanceService.findAssistance(assistanceTitle.get());
            List<SubAssistance> fetchedSubAssistances = subAssistanceService.findSubAssistance(assistance);
            for(SubAssistance s: fetchedSubAssistances)
                subAssistancePredicates.add(cb.equal(orderRoot.get("subAssistance"),s));

            predicates.add(cb.or(subAssistancePredicates.toArray(new Predicate[0])));
        }

        if(subAssistanceTitle.isPresent()){
            List<Predicate> subAssistancePredicates = new ArrayList<>();
            List<SubAssistance> fetchedSubAssistances = subAssistanceService.findSubAssistance(subAssistanceTitle.get());
            for(SubAssistance s: fetchedSubAssistances)
                subAssistancePredicates.add(cb.equal(orderRoot.get("subAssistance"),s));

            predicates.add(cb.or(subAssistancePredicates.toArray(new Predicate[0])));
        }

        cq.select(orderRoot).where(predicates.toArray(new Predicate[0]));
        Query query = em.createQuery(cq);
        return query.getResultList();
    }

    @Transactional
    public List<Order> customerOrTechnicianFilterOrders(Optional<OrderStatus> orderStatus) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Person person = personService.findByUsername(username);
        if (person instanceof Technician && !((Technician) person).isActive())
            throw new DeactivatedTechnicianException(Constants.DEACTIVATED_TECHNICIAN);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> orderRoot = cq.from(Order.class);
        List<Predicate> predicates = new ArrayList<>();

        if(person instanceof Customer)
            predicates.add(cb.equal(orderRoot.get("customer"),person));
        else
            predicates.add(cb.equal(orderRoot.get("technician"),person));

        orderStatus.map(o -> predicates.add(cb.equal(orderRoot.get("orderStatus"),o)));

        cq.select(orderRoot).where(predicates.toArray(new Predicate[0]));
        Query query = em.createQuery(cq);
        return query.getResultList();
    }
}
