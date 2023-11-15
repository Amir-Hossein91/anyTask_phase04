package com.example.phase_04.service.impl;

import com.example.phase_04.entity.*;
import com.example.phase_04.entity.enums.TechnicianStatus;
import com.example.phase_04.exceptions.DeactivatedTechnicianException;
import com.example.phase_04.exceptions.DuplicateTechnicianException;
import com.example.phase_04.exceptions.NotFoundException;
import com.example.phase_04.repository.TechnicianRepository;
import com.example.phase_04.service.TechnicianService;
import com.example.phase_04.utility.Constants;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TechnicianServiceImpl implements TechnicianService {

    private final TechnicianRepository repository;
    private final ManagerServiceImpl managerService;
    private final SubAssistanceServiceImpl subAssistanceService;
    private final AssistanceServiceImpl assistanceService;
    private final OrderServiceImpl orderService;

    public TechnicianServiceImpl(TechnicianRepository repository,
                                 ManagerServiceImpl managerService,
                                 SubAssistanceServiceImpl subAssistanceService,
                                 AssistanceServiceImpl assistanceService,
                                 OrderServiceImpl orderService) {
        super();
        this.repository = repository;
        this.managerService = managerService;
        this.subAssistanceService = subAssistanceService;
        this.assistanceService = assistanceService;
        this.orderService = orderService;
    }

    @Transactional
    public void addTechnicianToSubAssistance(String technicianUsername,
                                             String subAssistanceTitle, String assistanceTitle) {

        Technician technician = findByUsername(technicianUsername);
        Assistance assistance = assistanceService.findAssistance(assistanceTitle);

        if (assistance == null)
            throw new NotFoundException(Constants.ASSISTANCE_NOT_FOUND);

        SubAssistance subAssistance = subAssistanceService.findSubAssistance(subAssistanceTitle, assistance);

        if (technician == null || subAssistance == null)
            throw new NotFoundException(Constants.TECHNICIAN_OR_SUBASSISTANCE_NOT_FOUND);

        if (!technician.isActive() && technician.getTechnicianStatus() == TechnicianStatus.APPROVED)
            throw new DeactivatedTechnicianException(Constants.DEACTIVATED_TECHNICIAN);

        List<Technician> technicians = subAssistance.getTechnicians();
        if (technicians.contains(technician))
            throw new DuplicateTechnicianException(Constants.DUPLICATE_TECHNICIAN_SUBASSISTANCE);

        technicians.add(technician);
        technician.setTechnicianStatus(TechnicianStatus.APPROVED);
        technician.setActive(true);
        subAssistanceService.saveOrUpdate(subAssistance);
    }

    @Transactional
    public void removeTechnicianFromSubAssistance(String technicianName,
                                                  String subAssistanceTitle, String assistanceTitle) {

        Technician technician = findByUsername(technicianName);
        Assistance assistance = assistanceService.findAssistance(assistanceTitle);

        if (assistance == null)
            throw new NotFoundException(Constants.ASSISTANCE_NOT_FOUND);

        SubAssistance subAssistance = subAssistanceService.findSubAssistance(subAssistanceTitle, assistance);

        if (technician == null || subAssistance == null)
            throw new NotFoundException(Constants.TECHNICIAN_OR_SUBASSISTANCE_NOT_FOUND);

        List<Technician> technicians = subAssistance.getTechnicians();
        if (!technicians.contains(technician))
            throw new NotFoundException(Constants.TECHNICIAN_NOT_IN_LIST);

        technicians.remove(technician);

        if(subAssistanceService.findByTechniciansContaining(technician).isEmpty()) {
            technician.setTechnicianStatus(TechnicianStatus.PENDING);
            technician.setActive(false);
        }
        subAssistanceService.saveOrUpdate(subAssistance);
    }

    @Override
    @Transactional
    public Technician saveOrUpdate(Technician t) {
        return repository.save(t);
    }

    @Override
    @Transactional
    public void delete(Technician t) {
        repository.delete(t);
    }

    @Override
    public Technician findById(long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("\nCould not find technician with id = " + id));
    }

    @Override
    public List<Technician> findAll() {
        return repository.findAll();
    }

    @Override
    public Technician findByUsername(String technicianUsername) {
        return repository.findByUsername(technicianUsername).orElse(null);
    }

    @Override
    @Transactional
    public List<Technician> saveOrUpdate(List<Technician> technicians) {
        technicians = repository.saveAll(technicians);
        return technicians;
    }

    @Transactional
    public List<Technician> seeUnapprovedTechnicians() {

        List<Technician> technicians = repository.findUnapproved().orElse(null);
        if (technicians == null || technicians.isEmpty())
            throw new NotFoundException(Constants.NO_UNAPPROVED_TECHNICIANS);

        return technicians;
    }

    public List<Technician> seeDeactivatedTechnicians(String managerUsername) {

        Manager manager = managerService.findByUsername(managerUsername);
        if (manager == null)
            throw new IllegalArgumentException("Only manager can see deactivated technicians");

        List<Technician> technicians = repository.findDeactivated().orElse(null);
        if (technicians == null || technicians.isEmpty())
            throw new NotFoundException(Constants.NO_DEACTIVATED_TECHNICIANS);
        return technicians;
    }

    public List<Order> findRelatedOrders() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Technician technician = findByUsername(username);
        if (technician == null)
            throw new IllegalArgumentException("Only technicians can see their relative orders");

        if (!technician.isActive())
            throw new DeactivatedTechnicianException(Constants.DEACTIVATED_TECHNICIAN);

        return orderService.findRelatedOrders(technician);
    }

    @Transactional
    public void sendTechnicianSuggestion(long orderId, TechnicianSuggestion technicianSuggestion) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Technician technician = findByUsername(username);
        if (technician == null)
            throw new IllegalArgumentException("Only technicians can send suggestions to an order");

        if (!technician.isActive())
            throw new DeactivatedTechnicianException(Constants.DEACTIVATED_TECHNICIAN);

        technicianSuggestion.setTechnician(technician);

        Order order = orderService.findById(orderId);
        if (order == null)
            throw new NotFoundException(Constants.NO_SUCH_ORDER);

        orderService.sendTechnicianSuggestion(technician, order, technicianSuggestion);
    }

    public int seeTechnicianScore (long orderId){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Technician technician = findByUsername(username);
        if (technician == null)
            throw new IllegalArgumentException("No technician has been registered with this username");

        if (!technician.isActive())
            throw new DeactivatedTechnicianException(Constants.DEACTIVATED_TECHNICIAN);

        Order order = orderService.findById(orderId);
        if (order == null)
            throw new NotFoundException(Constants.NO_SUCH_ORDER);

        if(!order.getTechnician().equals(technician))
            throw new NotFoundException(Constants.ORDER_IS_NOT_RELATED);

        if(!order.isTechnicianScored())
            throw new IllegalStateException("Technician has not been scored on this order yet");

        return order.getTechnicianScore();
    }

    public String reportCredit() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Technician technician = findByUsername(username);
        long credit = technician.getCredit();
        return "Your account credit is " + credit + " T";
    }
}
