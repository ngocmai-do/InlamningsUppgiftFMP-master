package com.example.inlamningsuppgiftfmp.controllers;

import com.example.inlamningsuppgiftfmp.dtos.CustomerDto;
import jakarta.validation.Valid;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.PrivateKey;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping(path = "/customer")
public class CustomerController {


    private final RestTemplate restTemplate;

    public CustomerController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/all")
    public String getAll(Model model, RedirectAttributes redirectAttributes) {
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    "http://customerservice:8081/customers/all",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            model.addAttribute("allCustomers", response.getBody());
            model.addAttribute("name", "Name");
            model.addAttribute("email", "Email");
            model.addAttribute("tel", "Tel");
            model.addAttribute("customerTitle", "All Customers");

            return "customer";
        } catch (RestClientException e) {
            redirectAttributes.addFlashAttribute("error", "Customer service is currently unavailable. Please try again later.");
            return "redirect:/booking/all";
        }

    }


    @RequestMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes){
        try {
            restTemplate.exchange(
                    "http://customerservice:8081/customers/" + id,
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );
            redirectAttributes.addFlashAttribute("success", "Customer deleted successfully");
        } catch (HttpClientErrorException.Conflict e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete customer with existing bookings");
        } catch (RestClientException e) {
            redirectAttributes.addFlashAttribute("error", "Customer service is currently unavailable. Please try again later.");
        }

       return "redirect:/customer/all";
    }


    @RequestMapping("/edit/{id}")
    public String createEditCustomerForm(@PathVariable Long id, Model model) {

        try {
            CustomerDto customer = restTemplate.getForObject(
                    "http://customerservice:8081/customers/" + id,
                    CustomerDto.class
            );
        model.addAttribute("customerDto", customer);
        return "editCustomerForm";

        } catch (HttpClientErrorException.NotFound e) {
            model.addAttribute("error", "Customer not found");
            return "redirect:/customer/all";

        } catch (RestClientException e) {
            model.addAttribute("error", "Customer service is currently unavailable. Please try again later.");
            return "redirect:/customer/all";
        }
        
    }

    @PostMapping("/save")
    public String saveCustomer(@Valid CustomerDto customerDto, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            String firstError = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            model.addAttribute("errorMsg", firstError);
            return "editCustomerForm";
        }

        try {
            restTemplate.put(
                    "http://customerservice:8081/customers",
                    customerDto
            );
        } catch (HttpClientErrorException e) {
            model.addAttribute("errorMsg", "Could not update customer: " + e.getStatusCode());
            return "editCustomerForm";
        } catch (RestClientException e) {
            model.addAttribute("errorMsg", "Customer service is currently unavailable. Please try again later.");
            return "editCustomerForm";
        }

        return "redirect:/customer/all";

    }


    @RequestMapping("/new")
    public String createAddCustomerForm() {
        return "addCustomerForm";
    }


    @PostMapping("/update")
    public String updateCustomer(@Valid CustomerDto customerDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            String firstError = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            model.addAttribute("errorMsg", firstError);
            return "addCustomerForm";
        }

        try {
            restTemplate.postForObject(
                    "http://customerservice:8081/customers",
                    customerDto, CustomerDto.class
            );
        } catch (HttpClientErrorException e) {
            model.addAttribute("errorMsg", "Could not update new customer: " + e.getStatusCode());
            return "addCustomerForm";
        } catch (RestClientException e) {
            model.addAttribute("errorMsg", "Customer service is currently unavailable. Please try again later.");
            return "addCustomerForm";
        }

        return "redirect:/customer/all";

    }

}
