package com.example.phase_04.controller;

import com.example.phase_04.controller.requestObjects.*;
import com.example.phase_04.dto.request.OrderRequestDTO;
import com.example.phase_04.dto.request.PaymentRequestDTO;
import com.example.phase_04.dto.response.*;
import com.example.phase_04.entity.Order;
import com.example.phase_04.entity.SubAssistance;
import com.example.phase_04.entity.TechnicianSuggestion;
import com.example.phase_04.entity.enums.OrderStatus;
import com.example.phase_04.mapper.OrderMapper;
import com.example.phase_04.mapper.SubAssistanceMapper;
import com.example.phase_04.mapper.TechnicianSuggestionMapper;
import com.example.phase_04.service.impl.*;
import com.wf.captcha.SpecCaptcha;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/customer")
public class CustomerController {

    private String captchaText = "";

    private final CustomerServiceImpl customerService;
    private final SubAssistanceServiceImpl subAssistanceService;
    private final AssistanceServiceImpl assistanceService;
    private final OrderServiceImpl orderService;

    public CustomerController(CustomerServiceImpl customerService,
                              SubAssistanceServiceImpl subAssistanceService,
                              AssistanceServiceImpl assistanceService,
                              OrderServiceImpl orderService) {
        this.customerService = customerService;
        this.subAssistanceService = subAssistanceService;
        this.assistanceService = assistanceService;
        this.orderService = orderService;
    }

    @GetMapping("/seeSubAssistance/{username}")
    public ResponseEntity<List<SubAssistanceResponseDTO>> seeSubAssistances(@PathVariable String username){
        List<SubAssistance> subAssistances = subAssistanceService.showSubAssistancesToOthers(username);
        List<SubAssistanceResponseDTO> responseDTOS = new ArrayList<>();

        for(SubAssistance s: subAssistances)
            responseDTOS.add(SubAssistanceMapper.INSTANCE.modelToDto(s));

        return new ResponseEntity<>(responseDTOS,HttpStatus.OK);
    }

    @PostMapping("/makeOrder")
    public ResponseEntity<OrderResponseDTO> makeOrder (@RequestBody @Valid OrderRequestDTO requestDTO) {

        Order order = OrderMapper.INSTANCE.dtoToModel(requestDTO);
        order.setTechnicianScore(1);
        order.setTechnicianScored(false);
        order.setSubAssistance(subAssistanceService.findSubAssistance(requestDTO.subAssistanceTitle(), assistanceService.findAssistance(requestDTO.assistanceTitle())));
        order.setCustomer(customerService.findByUsername(requestDTO.customerUsername()));
        order.setOrderRegistrationDateAndTime(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.WAITING_FOR_TECHNICIANS_SUGGESTIONS);

        order = orderService.makeOrder(order.getCustomer().getUsername(),order.getSubAssistance().getTitle()
                    ,order.getSubAssistance().getAssistance().getTitle(),order.getOrderDescription());

        return new ResponseEntity<>(OrderMapper.INSTANCE.modelToDto(order),HttpStatus.CREATED);
    }

    @PostMapping("/seeSuggestions")
    public ResponseEntity<List<TechnicianSuggestionResponseDTO>> seeSuggestions (@RequestBody SeeSuggestions request){
        String username = request.getCustomerUsername();
        long orderId = request.getOrderId();
        String orderingBy = request.getOrderingBy();

        List<TechnicianSuggestion> suggestions;
        switch (orderingBy){
            case "price" -> suggestions = customerService.seeTechnicianSuggestionsOrderedByPrice(username,orderId);
            case "score" -> suggestions = customerService.seeTechnicianSuggestionsOrderedByScore(username,orderId);
            default -> throw new IllegalArgumentException("The 'orderingBy' field can either be 'price' or 'score'");
        }
        List<TechnicianSuggestionResponseDTO> responseDTOS = new ArrayList<>();

        for(TechnicianSuggestion t : suggestions)
            responseDTOS.add(TechnicianSuggestionMapper.INSTANCE.modelToDto(t));

        return new ResponseEntity<>(responseDTOS,HttpStatus.OK);
    }

    @PostMapping("/chooseSuggestion")
    public ResponseEntity<TechnicianSuggestionResponseDTO> chooseSuggestion(@RequestBody ChooseSuggestion request){
        String customerUsername = request.getCustomerUsername();
        long orderId = request.getOrderId();
        long suggestionId = request.getSuggestionId();

        return new ResponseEntity<>(TechnicianSuggestionMapper.INSTANCE
                .modelToDto(customerService.chooseSuggestion(customerUsername,orderId,suggestionId)),
                HttpStatus.CREATED);
    }

    @PostMapping("/markAsStarted")
    public ResponseEntity<String> markAsStarted (@RequestBody MarkAsStartedOrFinished request){

        customerService.markOrderAsStarted(request.getCustomerUsername(), request.getOrderId());
        Order order = orderService.findById(request.getOrderId());
        return new ResponseEntity<>("Technician started its job at " + order.getStartedTime(),HttpStatus.CREATED);
    }

    @PostMapping("/markAsFinished")
    public ResponseEntity<String> markAsFinished (@RequestBody MarkAsStartedOrFinished request){

        customerService.markOrderAsFinished(request.getCustomerUsername(), request.getOrderId());
        Order order = orderService.findById(request.getOrderId());
        return new ResponseEntity<>("Technician finished its job at " + order.getFinishedTime(),HttpStatus.CREATED);
    }

    @PostMapping("payThePrice")
    public void payThePrice (@RequestBody PayThePrice request){

        String howToPay = request.getHowToPay();
        switch(howToPay){
            case "credit" -> customerService.payThePriceByCredit(request.getCustomerUsername(), request.getOrderId());
            case "online" -> {
                File htmlFile = new File("C:\\Users\\AmirHossein\\IdeaProjects\\anyTask\\phase_04\\src\\main\\resources\\static\\PaymentPage.html");
                try {
                    Desktop.getDesktop().browse(htmlFile.toURI());
                } catch (IOException e) {
                    throw new RuntimeException("File not found");
                }
            }
            default -> throw new IllegalArgumentException("'howToPay' field can only be 'credit' or 'online'");
        }
    }

    @PostMapping("/onlinePayment")
    public ResponseEntity<String> onlinePayment (@RequestBody @Valid PaymentRequestDTO requestDTO){

        String checkedCaptcha = captchaText;
        if(!checkedCaptcha.equalsIgnoreCase(requestDTO.captchaValue()))
            throw new IllegalArgumentException("Wrong captcha value");

        customerService.payThePriceOnline(requestDTO.customerUsername(), requestDTO.orderId());
        return new ResponseEntity<>("Payment successful",HttpStatus.OK);
    }

    @GetMapping("/captcha")
    public CaptchaResponseDTO getCaptcha(){
        SpecCaptcha captcha = new SpecCaptcha(130, 48);
        captchaText = captcha.text();
        return new CaptchaResponseDTO(captcha.toBase64());
    }

    @PostMapping("/score")
    public ResponseEntity<String> scoreTechnician (@RequestBody @Valid ScoreTheTechnician request){
        customerService.scoreTheTechnician(request.getCustomerUsername(), request.getOrderId(),
                request.getScore(), request.getOpinion());

        return new ResponseEntity<>("Your score and opinion saved successfully",HttpStatus.OK);
    }

    @PostMapping("/filterOrders")
    public ResponseEntity<List<OrderResponseDTO>> filterOrders (@RequestBody CustomerOrTechnicianFilterOrders request){
        Optional<OrderStatus> orderStatus = Optional.ofNullable(request.getOrderStatus());
        List<Order> orders = orderService.customerOrTechnicianFilterOrders(orderStatus);
        List<OrderResponseDTO> orderDTOs = new ArrayList<>();
        for(Order o : orders)
            orderDTOs.add(OrderMapper.INSTANCE.modelToDto(o));
        return new ResponseEntity<>(orderDTOs,HttpStatus.OK);
    }

    @GetMapping("/seeCredit")
    public ResponseEntity<String> seeCredit (){
        String creditReport = customerService.reportCredit();
        return new ResponseEntity<>(creditReport,HttpStatus.OK);
    }
}
