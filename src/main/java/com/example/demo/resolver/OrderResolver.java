package com.example.demo.resolver;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import com.example.demo.dto.OrderInput;
import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import com.example.demo.service.OrderService;

@Controller
public class OrderResolver {

    private final OrderService orderService;

    public OrderResolver(OrderService orderService) {
        this.orderService = orderService;
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<Order> myOrders() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return orderService.getOrdersByUser(username);
    }

    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Order> allOrders() {
        return orderService.getAllOrders();
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public Order order(@Argument Long id) {
        return orderService.getOrderById(id);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Order createOrder(@Argument OrderInput input) {
        return orderService.createOrder(input);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Order updateOrderStatus(@Argument Long id, @Argument OrderStatus status) {
        return orderService.updateOrderStatus(id, status);
    }
}

