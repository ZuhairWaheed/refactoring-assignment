package com.example.geektrust;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PortfolioManagerTest {

    @Test
    public void testCreateMonths() {
        List<String> months = PortfolioManager.createMonths();
        assertNotNull(months);
    }

    @Test
    public void changeGains() {
        Map<Integer, List<Double>> portfolio = new HashMap<>();
        List<Double> initialInvestment = Arrays.asList(1000.0, 2000.0, 3000.0);
        portfolio.put(0, initialInvestment);

        List<Double> sip = Arrays.asList(100.0, 200.0, 300.0);
        String[] instructions = {"", "10.0%", "20.0%", "30.0%"};

        int count = 1;
        count = PortfolioManager.changeGains(portfolio, sip, instructions, count);

        assertEquals(2, count);

        List<Double> expectedInvestment = new ArrayList<>();
        expectedInvestment.add(1100.0);
        expectedInvestment.add(2400.0);
        expectedInvestment.add(3500.0);

        assertEquals(expectedInvestment, portfolio.get(1));
    }


    @Test
    public void calculatePercent() {
        List<Double> investment = Arrays.asList(1.3, 4.5, 2.4, 8.9);
        PortfolioManager.calculatePercent(investment, 100.9);

    }

    @Test
    public void printBalance() {
        List<Double> investment = Arrays.asList(1.3, 4.5, 2.4, 8.9);
        Map<Integer, List<Double>> portfolio = new HashMap<>();
        portfolio.put(1, investment);
        String balance = PortfolioManager.printBalance(portfolio, 0);
        assertEquals("1 4 2", balance);
    }

    @Test
    void testPrintRebalance() {

        double[] initialPortfolioPercent = {10.0, 20.0, 30.0};
        PortfolioManager portfolioManager = new PortfolioManager(initialPortfolioPercent);
        Map<Integer, List<Double>> portfolio = new HashMap<>();
        List<Double> updatedInvestment = new ArrayList<>();
        List<Double> listValues = Arrays.asList(10.0, 20.0, 30.0);
        portfolio.put(1, listValues);
        int count = 2;

        portfolioManager.printRebalance(portfolio, updatedInvestment, count);

        assertEquals(300.0, updatedInvestment.get(0));
        assertEquals(600.0, updatedInvestment.get(1));
        assertEquals(900.0, updatedInvestment.get(2));
    }

    @Test
    void testAllocateMoney() {
        Map<Integer, List<Double>> portfolio = new HashMap<>();
        List<Double> investment = new ArrayList<>();
        String[] instructions = {"ALLOCATE", "100.0", "200.0", "300.0"};
        int count = 1;

        int result = PortfolioManager.allocateMoney(portfolio, investment, count, instructions);

        assertEquals(100.0, investment.get(0));
        assertEquals(200.0, investment.get(1));
        assertEquals(300.0, investment.get(2));

        assertEquals(600.0, investment.get(3));

        assertEquals(investment, portfolio.get(1));
        assertEquals(2, result);
    }
}