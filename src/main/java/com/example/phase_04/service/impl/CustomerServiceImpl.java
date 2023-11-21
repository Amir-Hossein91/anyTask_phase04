package com.example.phase_04.service.impl;

import com.example.phase_04.dto.response.CurrenUsernameDTO;
import com.example.phase_04.entity.*;
import com.example.phase_04.entity.enums.OrderStatus;
import com.example.phase_04.exceptions.NotEnoughCreditException;
import com.example.phase_04.exceptions.NotFoundException;
import com.example.phase_04.repository.CustomerRepository;
import com.example.phase_04.service.CustomerService;
import com.example.phase_04.utility.Constants;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
    private static String name ="";

    private final CustomerRepository repository;
    private final OrderServiceImpl orderService;
    private final TechnicianSuggestionServiceImpl technicianSuggestionService;
    private final AssistanceServiceImpl assistanceService;
    private final SubAssistanceServiceImpl subAssistanceService;

    public CustomerServiceImpl(CustomerRepository repository,
                               OrderServiceImpl orderService,
                               TechnicianSuggestionServiceImpl technicianSuggestionService,
                               AssistanceServiceImpl assistanceService,
                               SubAssistanceServiceImpl subAssistanceService) {
        super();
        this.repository = repository;
        this.orderService = orderService;
        this.technicianSuggestionService = technicianSuggestionService;
        this.assistanceService = assistanceService;
        this.subAssistanceService = subAssistanceService;
    }

    @Transactional
    public Order makeOrder(String subAssistanceTitle, String assistanceTitle, OrderDescription orderDescription) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = findByUsername(username);
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

        order = orderService.saveOrUpdate(order);

        customer.setOrderCount(customer.getOrderCount() + 1);
        saveOrUpdate(customer);

        return order;
    }

    private void isSuggestionChoosingPossible(Person person, Order order) {
        if (order == null)
            throw new NotFoundException(Constants.NO_SUCH_ORDER);

        if (!order.getCustomer().equals(person))
            throw new NotFoundException(Constants.ORDER_NOT_BELONG_TO_CUSTOMER);

        if (!(order.getOrderStatus() == OrderStatus.WAITING_FOR_TECHNICIANS_SUGGESTIONS
                || order.getOrderStatus() == OrderStatus.CHOOSING_TECHNICIAN))
            throw new NotFoundException(Constants.SUGGESTION_NOT_AVAILABLE_IN_THIS_STATUS);
    }

    public List<TechnicianSuggestion> seeTechnicianSuggestionsOrderedByPrice(long orderId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = findByUsername(username);

        Order order = orderService.findById(orderId);

        isSuggestionChoosingPossible(customer, order);

        List<TechnicianSuggestion> technicianSuggestions = technicianSuggestionService.getSuggestionsOrderedByPrice(order);

        if (order.getOrderStatus() == OrderStatus.WAITING_FOR_TECHNICIANS_SUGGESTIONS) {
            order.setOrderStatus(OrderStatus.CHOOSING_TECHNICIAN);
            orderService.saveOrUpdate(order);
        }
        return technicianSuggestions;

    }

    public List<TechnicianSuggestion> seeTechnicianSuggestionsOrderedByScore(long orderId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = findByUsername(username);

        Order order = orderService.findById(orderId);

        isSuggestionChoosingPossible(customer, order);

        List<TechnicianSuggestion> technicianSuggestions = technicianSuggestionService.getSuggestionsOrderedByScore(order);

        if (order.getOrderStatus() == OrderStatus.WAITING_FOR_TECHNICIANS_SUGGESTIONS) {
            order.setOrderStatus(OrderStatus.CHOOSING_TECHNICIAN);
            orderService.saveOrUpdate(order);
        }
        return technicianSuggestions;
    }

    @Transactional
    public TechnicianSuggestion chooseSuggestion(long orderId, long suggestionId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = findByUsername(username);

        Order order = orderService.findById(orderId);

        isSuggestionChoosingPossible(customer, order);

        List<TechnicianSuggestion> technicianSuggestions = technicianSuggestionService.getSuggestionsOrderedByPrice(order);

        List<Long> suggestionsIds = technicianSuggestions.stream()
                .map(TechnicianSuggestion::getId)
                .toList();

        TechnicianSuggestion suggestion = technicianSuggestionService.findById(suggestionId);

        if (!suggestionsIds.contains(suggestion.getId()))
            throw new NotFoundException(Constants.TECHNICIAN_SUGGESTION_NOT_IN_LIST);

        order.setTechnician(suggestion.getTechnician());
        order.setChosenTechnicianSuggestion(suggestion);
        order.setOrderStatus(OrderStatus.TECHNICIAN_IS_ON_THE_WAY);
        orderService.saveOrUpdate(order);
        return suggestion;
    }

    @Transactional
    public void markOrderAsStarted(long orderId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = findByUsername(username);
        Order order = orderService.findById(orderId);

        if (!order.getCustomer().equals(customer))
            throw new NotFoundException(Constants.ORDER_NOT_BELONG_TO_CUSTOMER);

        if (order.getOrderStatus() != OrderStatus.TECHNICIAN_IS_ON_THE_WAY)
            throw new IllegalStateException(Constants.NO_TECHNICIAN_SELECTED);

        order.setOrderStatus(OrderStatus.STARTED);
        order.setStartedTime(LocalDateTime.now());
        orderService.saveOrUpdate(order);
    }

    @Transactional
    public void markOrderAsFinished(long orderId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = findByUsername(username);
        Order order = orderService.findById(orderId);

        if (!order.getCustomer().equals(customer))
            throw new NotFoundException(Constants.ORDER_NOT_BELONG_TO_CUSTOMER);

        if (order.getOrderStatus() != OrderStatus.STARTED)
            throw new IllegalStateException(Constants.ORDER_NOT_STARTED);

        order.setOrderStatus(OrderStatus.FINISHED);
        Technician technician = order.getTechnician();
        technician.setOrderCount(technician.getOrderCount() + 1);
        order.setFinishedTime(LocalDateTime.now());

        TechnicianSuggestion chosenSuggestion = order.getChosenTechnicianSuggestion();

        LocalDateTime suggestedFinishTime = order.getStartedTime().plusHours(chosenSuggestion.getTaskEstimatedDuration());

        if (order.getFinishedTime().isAfter(suggestedFinishTime)) {
            int negativeScore = (int) suggestedFinishTime.until(order.getFinishedTime(), ChronoUnit.HOURS);
            technician.setScore(technician.getScore() - negativeScore);
            if (technician.getScore() < 0) {
                technician.setActive(false);
            }
        }

        orderService.saveOrUpdate(order);
    }

    @Transactional
    public void payThePriceByCredit(long orderId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = findByUsername(username);

        Order order = orderService.findById(orderId);

        if (!order.getCustomer().equals(customer))
            throw new NotFoundException(Constants.ORDER_NOT_BELONG_TO_CUSTOMER);

        if (order.getOrderStatus() != OrderStatus.FINISHED)
            throw new IllegalStateException(Constants.PAYING_NOT_POSSIBLE_IN_THIS_STATE);

        Technician selectedTechnician = order.getTechnician();
        TechnicianSuggestion selectSuggestion = order.getChosenTechnicianSuggestion();

        if (customer.getCredit() < selectSuggestion.getTechSuggestedPrice())
            throw new NotEnoughCreditException(Constants.NOT_ENOUGH_CREDIT);
        customer.setCredit(customer.getCredit() - selectSuggestion.getTechSuggestedPrice());

        selectedTechnician.setCredit(selectedTechnician.getCredit() + (long) (0.7 * (selectSuggestion.getTechSuggestedPrice())));

        order.setOrderStatus(OrderStatus.FULLY_PAID);
        saveOrUpdate(customer);
        orderService.saveOrUpdate(order);
    }

    @Transactional
    public void payThePriceOnline(String username, long orderId) {

        Customer customer = findByUsername(username);

        Order order = orderService.findById(orderId);

        if (!order.getCustomer().equals(customer))
            throw new NotFoundException(Constants.ORDER_NOT_BELONG_TO_CUSTOMER);

        if (order.getOrderStatus() != OrderStatus.FINISHED)
            throw new IllegalStateException(Constants.PAYING_NOT_POSSIBLE_IN_THIS_STATE);

        Technician selectedTechnician = order.getTechnician();
        TechnicianSuggestion selectSuggestion = order.getChosenTechnicianSuggestion();

        selectedTechnician.setCredit(selectedTechnician.getCredit() + (long) (0.7 * (selectSuggestion.getTechSuggestedPrice())));

        order.setOrderStatus(OrderStatus.FULLY_PAID);
        orderService.saveOrUpdate(order);
    }

    @Transactional
    public void scoreTheTechnician(long orderId, int score, String opinion) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = findByUsername(username);

        Order order = orderService.findById(orderId);

        if (!order.getCustomer().equals(customer))
            throw new NotFoundException(Constants.ORDER_NOT_BELONG_TO_CUSTOMER);

        if (!(order.getOrderStatus() == OrderStatus.FINISHED || order.getOrderStatus() == OrderStatus.FULLY_PAID))
            throw new IllegalStateException(Constants.SCORING_NOT_POSSIBLE_IN_THIS_STATE);

        if(order.isTechnicianScored())
            throw new IllegalStateException("You have already scored the technician");

        Technician selectedTechnician = order.getTechnician();

        selectedTechnician.setScore(selectedTechnician.getScore() + score);

        order.setTechnicianScore(score);
        order.setTechEvaluation(opinion);
        order.setTechnicianScored(true);
        orderService.saveOrUpdate(order);
    }

    @Override
    @Transactional
    public Customer saveOrUpdate(Customer t) {
        return repository.save(t);
    }

    @Override
    @Transactional
    public void delete(Customer t) {
        repository.delete(t);
    }

    @Override
    public Customer findById(long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("\nCould not find customer with id = " + id));
    }

    @Override
    public List<Customer> findAll() {
        return repository.findAll();
    }

    @Override
    public Customer findByUsername(String customerUsername) {
        return repository.findByUsername(customerUsername).orElse(null);
    }

    public String reportCredit() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Customer customer = findByUsername(username);
        long credit = customer.getCredit();
        return "Your account credit is: " + credit + " T";
    }

    public void setName(){
        name = SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public CurrenUsernameDTO getUsername() {
        return new CurrenUsernameDTO(name);
    }
}
