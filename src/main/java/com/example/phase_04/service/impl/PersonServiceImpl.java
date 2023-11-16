package com.example.phase_04.service.impl;

import com.example.phase_04.entity.*;
import com.example.phase_04.entity.enums.Role;
import com.example.phase_04.entity.enums.TechnicianStatus;
import com.example.phase_04.exceptions.DeactivatedTechnicianException;
import com.example.phase_04.exceptions.NotFoundException;
import com.example.phase_04.repository.PersonRepository;
import com.example.phase_04.service.PersonService;
import com.example.phase_04.utility.Constants;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository repository;
    private final ManagerServiceImpl managerService;
    private final CustomerServiceImpl customerService;
    private final TechnicianServiceImpl technicianService;
    private final SubAssistanceServiceImpl subAssistanceService;
    private final BCryptPasswordEncoder passwordEncoder;


    @PersistenceContext
    private EntityManager em;

    public PersonServiceImpl(PersonRepository repository,
                             ManagerServiceImpl managerService,
                             CustomerServiceImpl customerService,
                             TechnicianServiceImpl technicianService,
                             SubAssistanceServiceImpl subAssistanceService,
                             BCryptPasswordEncoder passwordEncoder) {
        super();
        this.repository = repository;
        this.managerService = managerService;
        this.customerService = customerService;
        this.technicianService = technicianService;
        this.subAssistanceService = subAssistanceService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Person fetched = findByUsername(username);
        if (fetched != null) {
            if(fetched instanceof Technician && !((Technician) fetched).isActive())
                    throw new DeactivatedTechnicianException(Constants.DEACTIVATED_TECHNICIAN);
            if (!passwordEncoder.matches(oldPassword, fetched.getPassword()))
                throw new IllegalArgumentException(Constants.INCORRECT_PASSWORD);
            fetched.setPassword(passwordEncoder.encode(newPassword));
            saveOrUpdate(fetched);
        }
    }

    @Override
    @Transactional
    public Person saveOrUpdate(Person t) {
        return repository.save(t);
    }

    @Override
    @Transactional
    public void delete(Person t) {
        repository.delete(t);
    }

    @Override
    public Person findById(long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("\nCould not find person with id = " + id));
    }

    @Override
    public List<Person> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Person findByUsername(String username) {
        return repository.findByUsername(username).orElseThrow(() -> new NotFoundException(Constants.INVALID_USERNAME));
    }

    @Transactional
    public Customer registerCustomer(Customer person) {
        person.setRole(Role.ROLE_CUSTOMER);
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        return customerService.saveOrUpdate(person);
    }

    @Transactional
    public Technician registerTechnician(Technician technician) {
        if (technician == null)
            return null;
        technician.setRole(Role.ROLE_TECHNICIAN);
        technician.setPassword(passwordEncoder.encode(technician.getPassword()));
        return technicianService.saveOrUpdate(technician);
    }

    @Transactional
    public Manager registerManager(Manager manager) {
        if (managerService.doesManagerExist())
            throw new IllegalArgumentException("This organization already has a defined manager");
        manager.setRole(Role.ROLE_MANAGER);
        manager.setPassword(passwordEncoder.encode(manager.getPassword()));
        return managerService.saveOrUpdate(manager);
    }

    public Person login(String username, String password) {

        Person fetched = findByUsername(username);
        if (fetched instanceof Technician && !((Technician) fetched).isActive())
            throw new DeactivatedTechnicianException(Constants.DEACTIVATED_TECHNICIAN);
        if (fetched == null || !passwordEncoder.matches(password, fetched.getPassword()))
            throw new NotFoundException(Constants.INCORRECT_USERNAME_PASSWORD);
        return fetched;
    }

    public List<Person> filter(Optional<String> role,
                               Optional<String> firstName,
                               Optional<String> lastname,
                               Optional<String> email,
                               long subAssistanceId,
                               Optional<String> maxMin,
                               Optional<LocalDateTime> from,
                               Optional<LocalDateTime> until,
                               Optional<Integer> minOrders,
                               Optional<Integer> maxOrders) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Person> cq = cb.createQuery(Person.class);
        Root<Person> personRoot = cq.from(Person.class);


        List<Predicate> finalPredicates = new ArrayList<>();

        List<Predicate> subAssistancePredicateList = new ArrayList<>();

        firstName.map(fn -> finalPredicates.add(cb.like(personRoot.get("firstName"), "%" + fn + "%")));
        lastname.map(ln -> finalPredicates.add(cb.like(personRoot.get("lastName"), "%" + ln + "%")));
        email.map(e -> finalPredicates.add(cb.like(personRoot.get("email"), "%" + e + "%")));
        from.map(f -> finalPredicates.add(cb.greaterThanOrEqualTo(personRoot.get("registrationDate"),f)));
        until.map(u -> finalPredicates.add(cb.lessThanOrEqualTo(personRoot.get("registrationDate"),u)));
        minOrders.map(min -> finalPredicates.add(cb.greaterThanOrEqualTo(personRoot.get("orderCount"),min)));
        maxOrders.map(max -> finalPredicates.add(cb.lessThanOrEqualTo(personRoot.get("orderCount"),max)));

        if (subAssistanceId != 0) {
            SubAssistance subAssistance = subAssistanceService.findById(subAssistanceId);
            List<Technician> technicians = subAssistance.getTechnicians();
            for (Technician t : technicians) {
                subAssistancePredicateList.add(cb.equal(personRoot.get("id"), t.getId()));
            }
            Predicate subAssistancePredicate = cb.or(subAssistancePredicateList.toArray(new Predicate[0]));
            finalPredicates.add(subAssistancePredicate);
        }

        if (!maxMin.isEmpty()) {
            String m = maxMin.get();
            if (m.equalsIgnoreCase("max")) {
                Subquery<Integer> subquery = cq.subquery(Integer.class);
                Root<Person> subqueryRoot = subquery.from(Person.class);
                subquery.select(cb.max(subqueryRoot.get("score")));
                finalPredicates.add(cb.equal(personRoot.get("score"), subquery));
            } else if (m.equalsIgnoreCase("min")) {
                Subquery<Integer> subquery = cq.subquery(Integer.class);
                Root<Person> subqueryRoot = subquery.from(Person.class);
                subquery.select(cb.min(subqueryRoot.get("score")));
                finalPredicates.add(cb.equal(personRoot.get("score"), subquery));
            }
        }

        cq.select(personRoot).where(finalPredicates.toArray(new Predicate[0]));
        TypedQuery typedQuery = em.createQuery(cq);
        List<Person> result = typedQuery.getResultList();

        if (!role.isEmpty()) {
            String r = role.get();
            if (r.equals("customer")) {
                for (int i = 0; i < result.size(); i++) {
                    Person person = result.get(i);
                    if (!(person instanceof Customer)) {
                        result.remove(person);
                        i--;
                    }
                }
            } else if (r.equals("technician")) {
                for (int i = 0; i < result.size(); i++) {
                    Person person = result.get(i);
                    if (!(person instanceof Technician)) {
                        result.remove(person);
                        i--;
                    }
                }
            }
        }

        return result;
    }

    @Transactional
    public void enablePerson(String username) {
        Person person = findByUsername(username);
        if (person instanceof Technician) {
            person.setClickedActivationLink(true);
            ((Technician) person).setTechnicianStatus(TechnicianStatus.PENDING);
        } else
            person.setClickedActivationLink(true);
        repository.save(person);
    }
}
