package com.emre.api.walletapp.service;

import com.emre.api.walletapp.dto.wallet.CreateWalletRequest;
import com.emre.api.walletapp.dto.wallet.WalletResponse;
import com.emre.api.walletapp.enums.Currency;
import com.emre.api.walletapp.exception.ResourceNotFoundException;
import com.emre.api.walletapp.exception.UnauthorizedAccessException;
import com.emre.api.walletapp.model.AppUser;
import com.emre.api.walletapp.model.Wallet;
import com.emre.api.walletapp.repository.WalletRepository;
import com.emre.api.walletapp.utils.SecurityUtils;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletServiceTest {

    WalletRepository walletRepository = mock(WalletRepository.class);
    WalletServiceImpl walletService = new WalletServiceImpl(walletRepository);

    MockedStatic<SecurityUtils> securityMock;

    @BeforeEach
    void setup() {
        securityMock = Mockito.mockStatic(SecurityUtils.class);
    }

    @AfterEach
    void tearDown() {
        securityMock.close();
    }

    private CreateWalletRequest buildRequest() {
        CreateWalletRequest req = new CreateWalletRequest();
        req.setWalletName("Main Wallet");
        req.setCurrency(Currency.USD);
        req.setActiveForShopping(true);
        req.setActiveForWithdraw(true);
        return req;
    }

    // -------------------------------------------------------------------------
    // CREATE BY EMPLOYEE
    // -------------------------------------------------------------------------

    @Test
    void createWalletByEmployee_success() {
        AppUser employee = new AppUser();
        employee.setId(10L);
        employee.setRole("EMPLOYEE");

        securityMock.when(SecurityUtils::getCurrentUser).thenReturn(employee);

        Wallet wallet = Wallet.builder()
                .id(1L)
                .customerId(5L)
                .walletName("Main Wallet")
                .currency(Currency.USD)
                .balance(0.0)
                .usableBalance(0.0)
                .activeForShopping(true)
                .activeForWithdraw(true)
                .build();

        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        WalletResponse response = walletService.createWalletByEmployee(buildRequest(), 5L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(5L, response.getCustomerId());
    }

    @Test
    void createWalletByEmployee_notEmployee_throwsUnauthorized() {
        AppUser customer = new AppUser();
        customer.setRole("CUSTOMER");

        securityMock.when(SecurityUtils::getCurrentUser).thenReturn(customer);

        assertThrows(UnauthorizedAccessException.class, () ->
                walletService.createWalletByEmployee(buildRequest(), 5L));
    }

    @Test
    void createWalletByEmployee_repositoryFails_throwsRuntime() {
        AppUser employee = new AppUser();
        employee.setRole("EMPLOYEE");

        securityMock.when(SecurityUtils::getCurrentUser).thenReturn(employee);

        when(walletRepository.save(any())).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () ->
                walletService.createWalletByEmployee(buildRequest(), 5L));
    }

    // -------------------------------------------------------------------------
    // CREATE BY CUSTOMER
    // -------------------------------------------------------------------------

    @Test
    void createWalletByCustomer_success() {
        AppUser customer = new AppUser();
        customer.setId(3L);
        customer.setRole("CUSTOMER");

        securityMock.when(SecurityUtils::getCurrentUser).thenReturn(customer);

        Wallet wallet = Wallet.builder()
                .id(1L)
                .customerId(3L)
                .walletName("Main Wallet")
                .currency(Currency.USD)
                .balance(0.0)
                .usableBalance(0.0)
                .build();

        when(walletRepository.save(any())).thenReturn(wallet);

        WalletResponse response = walletService.createWalletByCustomer(buildRequest());

        assertNotNull(response);
        assertEquals(3L, response.getCustomerId());
    }

    @Test
    void createWalletByCustomer_notCustomer_throwsUnauthorized() {
        AppUser employee = new AppUser();
        employee.setRole("EMPLOYEE");

        securityMock.when(SecurityUtils::getCurrentUser).thenReturn(employee);

        assertThrows(UnauthorizedAccessException.class, () ->
                walletService.createWalletByCustomer(buildRequest()));
    }

    // -------------------------------------------------------------------------
    // LIST BY EMPLOYEE
    // -------------------------------------------------------------------------

    @Test
    void listWalletsByEmployee_success() {
        AppUser employee = new AppUser();
        employee.setRole("EMPLOYEE");

        securityMock.when(SecurityUtils::getCurrentUser).thenReturn(employee);

        Wallet w = Wallet.builder().id(1L).customerId(5L).walletName("W1").currency(Currency.USD).build();
        when(walletRepository.findByCustomerId(5L)).thenReturn(List.of(w));

        List<WalletResponse> responses = walletService.listWalletsByEmployee(5L);

        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getId());
    }

    @Test
    void listWalletsByEmployee_nonEmployee_throwsUnauthorized() {
        AppUser customer = new AppUser();
        customer.setRole("CUSTOMER");

        securityMock.when(SecurityUtils::getCurrentUser).thenReturn(customer);

        assertThrows(UnauthorizedAccessException.class, () ->
                walletService.listWalletsByEmployee(5L));
    }

    @Test
    void listWalletsByEmployee_repositoryError_throwsRuntime() {
        AppUser employee = new AppUser();
        employee.setRole("EMPLOYEE");

        securityMock.when(SecurityUtils::getCurrentUser).thenReturn(employee);

        when(walletRepository.findByCustomerId(1L))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () ->
                walletService.listWalletsByEmployee(1L));
    }

    @Test
    void listWalletsByEmployee_emptyList_throwsNotFound() {
        AppUser employee = new AppUser();
        employee.setRole("EMPLOYEE");

        securityMock.when(SecurityUtils::getCurrentUser).thenReturn(employee);

        when(walletRepository.findByCustomerId(1L)).thenReturn(new ArrayList<>());

        assertThrows(ResourceNotFoundException.class, () ->
                walletService.listWalletsByEmployee(1L));
    }

    // -------------------------------------------------------------------------
    // LIST BY CUSTOMER
    // -------------------------------------------------------------------------

    @Test
    void listWalletsByCustomer_success() {
        AppUser customer = new AppUser();
        customer.setId(7L);
        customer.setRole("CUSTOMER");

        securityMock.when(SecurityUtils::getCurrentUser).thenReturn(customer);

        Wallet w = Wallet.builder().id(1L).customerId(7L).walletName("W").currency(Currency.USD).build();
        when(walletRepository.findByCustomerId(7L)).thenReturn(List.of(w));

        List<WalletResponse> responses = walletService.listWalletsByCustomer();

        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getId());
    }

    @Test
    void listWalletsByCustomer_notCustomer_throwsUnauthorized() {
        AppUser employee = new AppUser();
        employee.setRole("EMPLOYEE");

        securityMock.when(SecurityUtils::getCurrentUser).thenReturn(employee);

        assertThrows(UnauthorizedAccessException.class, () ->
                walletService.listWalletsByCustomer());
    }

    @Test
    void listWalletsByCustomer_repositoryFails_throwsRuntime() {
        AppUser customer = new AppUser();
        customer.setId(7L);
        customer.setRole("CUSTOMER");

        securityMock.when(SecurityUtils::getCurrentUser).thenReturn(customer);

        when(walletRepository.findByCustomerId(7L))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> walletService.listWalletsByCustomer());
    }

    @Test
    void listWalletsByCustomer_emptyList_throwsNotFound() {
        AppUser customer = new AppUser();
        customer.setId(7L);
        customer.setRole("CUSTOMER");

        securityMock.when(SecurityUtils::getCurrentUser).thenReturn(customer);

        when(walletRepository.findByCustomerId(7L)).thenReturn(new ArrayList<>());

        assertThrows(ResourceNotFoundException.class, () -> walletService.listWalletsByCustomer());
    }
}
