package com.fouadev.billingservice;

import com.fouadev.billingservice.entities.Bill;
import com.fouadev.billingservice.entities.ProductItem;
import com.fouadev.billingservice.feign.CustomerRestClient;
import com.fouadev.billingservice.feign.ProductRestClient;
import com.fouadev.billingservice.model.Customer;
import com.fouadev.billingservice.model.Product;
import com.fouadev.billingservice.repositories.BillRepository;
import com.fouadev.billingservice.repositories.ProductItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.Collection;
import java.util.Date;
import java.util.Random;

@SpringBootApplication
@EnableFeignClients
public class BillingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillingServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(BillRepository billRepository,
                                        ProductItemRepository productItemRepository,
                                        CustomerRestClient customerRestClient,
                                        ProductRestClient productRestClient) {
        return args -> {
            Collection<Customer> customers = customerRestClient.findAll().getContent();
            Collection<Product> products = productRestClient.findAll().getContent();

            customers.forEach(customer -> {

                Bill bill = Bill.builder()
                        .billingDate(new Date())
                        .customerId(customer.getId())
                        .build();

                billRepository.save(bill);

                products.forEach(product -> {

                    ProductItem productItem = ProductItem.builder()
                            .productId(product.getId())
                            .price(product.getPrice())
                            .quantity(1+new Random().nextInt(10))
                            .bill(bill)
                            .build();

                    productItemRepository.save(productItem);
                });
            });
        };
    }
}