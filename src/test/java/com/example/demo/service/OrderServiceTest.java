package com.example.demo.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.demo.dto.OrderInput;
import com.example.demo.dto.OrderItemInput;
import com.example.demo.model.Order;
import com.example.demo.model.OrderStatus;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ProductRepository productRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, userRepository, productRepository);
    }

    private void setSecurityContext(String username) {
        UsernamePasswordAuthenticationToken auth = 
            new UsernamePasswordAuthenticationToken(username, "password", List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void createOrder_deberiaCrearOrdenExitosamente() {
        // Setup
        setSecurityContext("testuser");
        
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("1500.00"));
        
        OrderItemInput itemInput = new OrderItemInput();
        itemInput.setProductId(1L);
        itemInput.setQuantity(2);
        
        OrderInput orderInput = new OrderInput();
        orderInput.setItems(List.of(itemInput));
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        // Execute
        Order result = orderService.createOrder(orderInput);

        // Verify
        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(3000.0, result.getTotal()); // 1500 * 2
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_deberiaLanzarExcepcionUsuarioNoEncontrado() {
        setSecurityContext("nonexistent");
        
        OrderInput orderInput = new OrderInput();
        
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(orderInput);
        });
    }

    @Test
    void createOrder_deberiaLanzarExcepcionProductoNoEncontrado() {
        setSecurityContext("testuser");
        
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        
        OrderItemInput itemInput = new OrderItemInput();
        itemInput.setProductId(99L);
        itemInput.setQuantity(1);
        
        OrderInput orderInput = new OrderInput();
        orderInput.setItems(List.of(itemInput));
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(orderInput);
        });
    }

    @Test
    void getOrdersByUser_deberiaRetornarOrdenesDelUsuario() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        
        Order order1 = new Order();
        order1.setId(1L);
        order1.setUser(user);
        order1.setTotal(100.0);
        
        Order order2 = new Order();
        order2.setId(2L);
        order2.setUser(user);
        order2.setTotal(200.0);
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(orderRepository.findByUser(user)).thenReturn(Arrays.asList(order1, order2));

        List<Order> result = orderService.getOrdersByUser("testuser");

        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findByUser(user);
    }

    @Test
    void getOrdersByUser_deberiaLanzarExcepcionUsuarioNoEncontrado() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            orderService.getOrdersByUser("nonexistent");
        });
    }

    @Test
    void getAllOrders_deberiaRetornarTodasLasOrdenes() {
        Order order1 = new Order();
        order1.setId(1L);
        order1.setTotal(100.0);
        
        Order order2 = new Order();
        order2.setId(2L);
        order2.setTotal(200.0);
        
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));

        List<Order> result = orderService.getAllOrders();

        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void getOrderById_deberiaRetornarOrdenCuandoExiste() {
        Order order = new Order();
        order.setId(1L);
        order.setTotal(100.0);
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getOrderById_deberiaLanzarExcepcionCuandoNoExiste() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            orderService.getOrderById(1L);
        });
    }

    @Test
    void updateOrderStatus_deberiaActualizarEstado() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);

        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void updateOrderStatus_deberiaLanzarExcepcionOrdenNoEncontrada() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            orderService.updateOrderStatus(1L, OrderStatus.SHIPPED);
        });
    }

    @Test
    void createOrder_conMultiplesItems_deberiaCalcularTotalCorrectamente() {
        setSecurityContext("testuser");
        
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setPrice(new BigDecimal("1000.00"));
        
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Mouse");
        product2.setPrice(new BigDecimal("50.00"));
        
        OrderItemInput item1 = new OrderItemInput();
        item1.setProductId(1L);
        item1.setQuantity(1);
        
        OrderItemInput item2 = new OrderItemInput();
        item2.setProductId(2L);
        item2.setQuantity(2);
        
        OrderInput orderInput = new OrderInput();
        orderInput.setItems(Arrays.asList(item1, item2));
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        Order result = orderService.createOrder(orderInput);

        assertEquals(1100.0, result.getTotal()); // 1000 + (50*2)
    }
}

