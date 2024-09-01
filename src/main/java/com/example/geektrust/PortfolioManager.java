package com.example.geektrust;

import com.example.geektrust.constants.Command;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class PortfolioManager {
    private static double[] portfolioPercent = new double[3];

    public PortfolioManager() {
    }

    public PortfolioManager(double[] portfolioPercent) {
        this.portfolioPercent = portfolioPercent;
    }

    public void processPortfolio(String[] args) {
        Map<Integer, List<Double>> portfolio = new LinkedHashMap<>();
        List<Double> sip = new LinkedList<>();
        List<Double> updatedInvestment = new LinkedList<>();
        List<Double> investment = new LinkedList<>();
        List<String> months = createMonths();
        int count = 0;

        try (Stream<String> fileLines = Files.lines(new File(args[0]).toPath())) {
            List<String> lines = fileLines.map(String::trim).filter(s -> !s.matches(" ")).collect(Collectors.toList());
            for (String line : lines) {
                String[] instructions = line.trim().split(" ");
                Command command = Command.valueOf(instructions[0]);
                count = executeCommands(portfolio, sip, updatedInvestment, investment, months, count, instructions, command);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> createMonths() {
        return Arrays.asList(
                "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
                "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
        );
    }

    private int executeCommands(Map<Integer, List<Double>> portfolio, List<Double> sip, List<Double> updatedInvestment, List<Double> investment, List<String> months, int count, String[] instructions, Command command) {
        switch (command) {
            case ALLOCATE:
                count = allocateMoney(portfolio, investment, count, instructions);
                break;
            case SIP:
                addSipValues(sip, instructions);
                break;
            case CHANGE:
                count = changeGains(portfolio, sip, instructions, count);
                break;
            case BALANCE:
                printBalance(portfolio, months.indexOf(instructions[1]));
                break;
            case REBALANCE:
                printRebalanceIfPossible(portfolio, updatedInvestment, count);
                break;
        }
        return count;
    }

    private void printRebalanceIfPossible(Map<Integer, List<Double>> portfolio, List<Double> updatedInvestment, int count) {
        int size = portfolio.size() - 1;
        if (size % 6 == 0) {
            printRebalance(portfolio, updatedInvestment, count);
        } else {
            System.out.println("CANNOT_REBALANCE");
        }
    }

    private void addSipValues(List<Double> sip, String[] instructions) {
        for (int i = 1; i < instructions.length; i++) {
            sip.add(Double.parseDouble(instructions[i]));
        }
    }

    public static int changeGains(Map<Integer, List<Double>> portfolio, List<Double> sip, String[] instructions, int count) {
        List<Double> listValues = portfolio.get(count - 1);
        List<Double> updatedInvestment = new LinkedList<>();
        double total = calculateAndUpdateInvestment(sip, instructions, count, listValues, updatedInvestment, 0);
        updatedInvestment.add(total);
        addToPortfolio(portfolio, updatedInvestment, count);
        return count + 1;
    }

    private static double calculateAndUpdateInvestment(List<Double> sip, String[] instructions, int count, List<Double> listValues, List<Double> updatedInvestment, double total) {
        Pattern pattern = Pattern.compile("^-?\\d+\\.?\\d+");
        for (int i = 1; i < instructions.length - 1; i++) {
            Matcher matcher = pattern.matcher(instructions[i]);
            if (matcher.find()) {
                double value = Double.parseDouble(matcher.group());
                double currentValue = listValues.get(i - 1);
                double updatedValue = calculateUpdatedInvestment(currentValue, sip.get(i - 1), value, count);
                updatedInvestment.add(updatedValue);
                total += updatedValue;
            }
        }
        return total;
    }

    private static double calculateUpdatedInvestment(double currentValue, double sipValue, double difference, int count) {
        if (count - 1 > 0) {
            return calculateUpdatedInvestmentWithSIP(currentValue, sipValue, difference);
        } else {
            return calculateUpdatedInvestmentWithoutSIP(currentValue, difference);
        }
    }

    private static double calculateUpdatedInvestmentWithSIP(double currentValue, double sipValue, double difference) {
        return (currentValue + sipValue) * (1 + difference / 100);
    }

    private static double calculateUpdatedInvestmentWithoutSIP(double currentValue, double difference) {
        return currentValue * (1 + difference / 100);
    }

    public static int allocateMoney(Map<Integer, List<Double>> portfolio, List<Double> investment, int count, String[] instructions) {
        double total = calculateTotalAndAddToInvestment(investment, instructions);
        addToPortfolio(portfolio, investment, count);
        calculatePercent(investment, total);
        return count + 1;
    }

    private static double calculateTotalAndAddToInvestment(List<Double> investment, String[] instructions) {
        double total = 0;
        for (int i = 1; i < instructions.length; i++) {
            double amount = Double.parseDouble(instructions[i]);
            total += amount;
            investment.add(amount);
        }
        investment.add(total);
        return total;
    }

    private static void addToPortfolio(Map<Integer, List<Double>> portfolio, List<Double> investment, int count) {
        portfolio.put(count, investment);
    }

    public static void calculatePercent(List<Double> investment, double total) {
        for (int i = 0; i < investment.size() - 1; i++) {
            portfolioPercent[i] = investment.get(i) / total;
        }
    }

    public static void printRebalance(Map<Integer, List<Double>> portfolio, List<Double> updatedInvestment, int count) {
        StringBuilder sb = new StringBuilder();
        List<Double> listValues = portfolio.get(count - 1);
        double total = listValues.get(listValues.size() - 1);
        calculateRebalanceInvestment(updatedInvestment, sb, total);
        updatedInvestment.add(total);
        addToPortfolio(portfolio, updatedInvestment, count - 1);
        System.out.println(sb);
    }

    private static void calculateRebalanceInvestment(List<Double> updatedInvestment, StringBuilder sb, double total) {
        for (double d : portfolioPercent) {
            updatedInvestment.add(d * total);
            double printValue = d * total;
            sb.append((short) printValue).append(" ");
        }
    }

    public static String printBalance(Map<Integer, List<Double>> portfolio, int index) {
        List<Double> monthlyValues = portfolio.get(index + 1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < monthlyValues.size() - 1; i++) {
            sb.append(monthlyValues.get(i).intValue()).append(" ");
        }
        System.out.println(sb);
        return sb.toString().trim();
    }
}
