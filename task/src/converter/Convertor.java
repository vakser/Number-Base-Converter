package converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Scanner;

public class Convertor {
    private final Scanner scanner = new Scanner(System.in);
    private String[] bases = new String[2];
    private String[] numberParts = new String[2];
    private String number;

    private BigInteger conversionIntToDecimalResult;
    private BigDecimal conversionFractionToDecimalResult;
    private StringBuilder conversionIntDecimalToBaseResult = new StringBuilder();
    private StringBuilder conversionFractionDecimalToBaseResult = new StringBuilder();
    private String input;

    public void useConvertor() {
        askForBases();
        getBases();
        while (!"/exit".equals(input)) {
            askForNumber();
            getIntAndFractionParts();
            while (!"/back".equals(number)) {
                convert();
                askForNumber();
                getIntAndFractionParts();
            }
            askForBases();
            getBases();
            exitIfOrdered();
        }
    }

    void getBases() {
        if (input.contains(" ")) {
            bases = input.split(" ");
        }
    }

    void getIntAndFractionParts() {
        if (number.contains(".")) {
            numberParts = number.split("\\.");
        }
    }

    void exitIfOrdered() {
        if ("/exit".equals(input)) {
            System.exit(0);
        }
    }

    void askForBases() {
        System.out.print("Enter two numbers in format: {source base} {target base} (To quit type /exit) ");
        input = scanner.nextLine();
    }

    void askForNumber() {
        System.out.printf("Enter number in base %s to convert to base %s (To go back type /back) ", bases[0], bases[1]);
        number = scanner.nextLine();
    }

    void convert() {
        if (!number.contains(".")) {
            if ("10".equals(bases[0]) && !"10".equals(bases[1])) {
                conversionIntToDecimalResult = new BigInteger(number);
                convertIntToTarget();
                System.out.println("Conversion result: " + conversionIntDecimalToBaseResult.reverse() + "\n");
            } else if (!"10".equals(bases[0]) && "10".equals(bases[1])) {
                convertIntToDecimal();
                System.out.println("Conversion result: " + conversionIntToDecimalResult + "\n");
            } else {
                convertIntToDecimal();
                convertIntToTarget();
                System.out.println("Conversion result: " + conversionIntDecimalToBaseResult.reverse() + "\n");
            }
        } else {
            if ("0".equals(numberParts[0]) || "".equals(numberParts[0])) {
                if ("10".equals(bases[0]) && !"10".equals(bases[1])) {
                    convertFractionToTarget();
                    System.out.println("Conversion result: 0." + conversionFractionDecimalToBaseResult + "\n");
                } else if (!"10".equals(bases[0]) && "10".equals(bases[1])) {
                    convertFractionToDecimal();
                    System.out.println("Conversion result: 0." + conversionFractionToDecimalResult + "\n") ;
                } else {
                    convertFractionToDecimal();
                    convertFractionToTarget();
                    System.out.println("Conversion result: 0." + conversionFractionDecimalToBaseResult + "\n");
                }
            } else if (numberParts[1].matches("\\d+") && Double.parseDouble("0." + numberParts[1]) == 0) {
                if ("10".equals(bases[0]) && !"10".equals(bases[1])) {
                    conversionIntToDecimalResult = new BigInteger(number);
                    convertIntToTarget();
                    System.out.println("Conversion result: " + conversionIntDecimalToBaseResult.reverse() + ".00000\n");
                } else if (!"10".equals(bases[0]) && "10".equals(bases[1])) {
                    convertIntToDecimal();
                    System.out.println("Conversion result: " + conversionIntToDecimalResult + ".00000\n");
                } else {
                    convertIntToDecimal();
                    convertIntToTarget();
                    System.out.println("Conversion result: " + conversionIntDecimalToBaseResult.reverse() + ".00000\n");
                }
            } else {
                if ("10".equals(bases[0]) && !"10".equals(bases[1])) {
                    conversionIntToDecimalResult = new BigInteger(Objects.requireNonNull(numberParts[0]));
                    convertIntToTarget();
                    convertFractionToTarget();
                    System.out.println("Conversion result: " + conversionIntDecimalToBaseResult.reverse() + "." + conversionFractionDecimalToBaseResult + "\n");
                } else if (!"10".equals(bases[0]) && "10".equals(bases[1])) {
                    convertIntToDecimal();
                    convertFractionToDecimal();
                    System.out.println("Conversion result: " + new BigDecimal(conversionIntToDecimalResult).add(conversionFractionToDecimalResult) + "\n");
                } else {
                    convertIntToDecimal();
                    convertFractionToDecimal();
                    convertIntToTarget();
                    convertFractionToTarget();
                    System.out.println("Conversion result: " + conversionIntDecimalToBaseResult.reverse() + "." + conversionFractionDecimalToBaseResult + "\n");
                }
            }
        }
    }

    void convertFractionToDecimal() {
        BigInteger base = BigInteger.valueOf(Integer.parseInt(bases[0]));
        BigDecimal k = new BigDecimal("-1");
        conversionFractionToDecimalResult = BigDecimal.ZERO;
        for (int i = 0; i < numberParts[1].length(); i++) {
            conversionFractionToDecimalResult = conversionFractionToDecimalResult.add(new BigDecimal(String.valueOf(Character.getNumericValue(numberParts[1].charAt(i)))).multiply(BigDecimal.valueOf(Math.pow(base.doubleValue(), k.doubleValue()))));
            k = k.subtract(BigDecimal.ONE);
        }
        conversionFractionToDecimalResult = conversionFractionToDecimalResult.setScale(5, RoundingMode.HALF_UP);
    }

    void convertFractionToTarget() {
        BigInteger base = BigInteger.valueOf(Integer.parseInt(bases[1]));
        BigDecimal fractionalPart;
        BigDecimal baseBigDecimal = new BigDecimal(base);
        if ("0".equals(numberParts[0]) && "10".equals(bases[0])) {
            fractionalPart = new BigDecimal(number);
        } else if (!"0".equals(numberParts[0]) && "10".equals(bases[0])) {
            fractionalPart = new BigDecimal(number).remainder(new BigDecimal(numberParts[0]));
        } else {
            fractionalPart = conversionFractionToDecimalResult;
        }
        BigDecimal helper = fractionalPart.multiply(baseBigDecimal);
        BigInteger helperIntPart = helper.toBigInteger();
        conversionFractionDecimalToBaseResult = new StringBuilder();
        while (conversionFractionDecimalToBaseResult.length() != 5) {
            conversionFractionDecimalToBaseResult.append(Character.forDigit(helperIntPart.intValueExact(), Integer.parseInt(bases[1])));
            helper = helper.remainder(BigDecimal.ONE).multiply(baseBigDecimal);
            helperIntPart = helper.toBigInteger();
        }
    }

    void convertIntToTarget() {
        BigInteger base = BigInteger.valueOf(Integer.parseInt(bases[1]));
        conversionIntDecimalToBaseResult = new StringBuilder();
        if (base.compareTo(BigInteger.TEN) < 0) {
            while (conversionIntToDecimalResult.compareTo(base) >= 0) {
                conversionIntDecimalToBaseResult.append(conversionIntToDecimalResult.remainder(base));
                conversionIntToDecimalResult = conversionIntToDecimalResult.divide(base);
            }
            conversionIntDecimalToBaseResult.append(conversionIntToDecimalResult);
        } else {
            while (conversionIntToDecimalResult.compareTo(base) >= 0) {
                if (conversionIntToDecimalResult.remainder(base).compareTo(BigInteger.TEN) < 0) {
                    conversionIntDecimalToBaseResult.append(conversionIntToDecimalResult.remainder(base));
                } else {
                    conversionIntDecimalToBaseResult.append((char) Integer.parseInt(conversionIntToDecimalResult.remainder(base).add(new BigInteger("87")).toString()));
                }
                conversionIntToDecimalResult = conversionIntToDecimalResult.divide(base);
            }
            if (conversionIntToDecimalResult.compareTo(BigInteger.TEN) < 0) {
                conversionIntDecimalToBaseResult.append(conversionIntToDecimalResult.remainder(base));
            } else {
                conversionIntDecimalToBaseResult.append((char) Integer.parseInt(conversionIntToDecimalResult.remainder(base).add(new BigInteger("87")).toString()));
            }
        }
    }

    void convertIntToDecimal() {
        while (number.charAt(0) == '0') {
            number = number.substring(1);
        }
        BigInteger base = BigInteger.valueOf(Integer.parseInt(bases[0]));
        conversionIntToDecimalResult = BigInteger.ZERO;
        BigInteger k = BigInteger.ZERO;
        if (number.contains(".")) {
            for (int i = numberParts[0].length() - 1; i >= 0 ; i--) {
                conversionIntToDecimalResult = conversionIntToDecimalResult.add(new BigInteger(String.valueOf(Character.getNumericValue(number.charAt(i)))).multiply(BigDecimal.valueOf(Math.pow(base.doubleValue(), k.doubleValue())).toBigIntegerExact()));
                k = k.add(BigInteger.ONE);
            }
        } else {
            for (int i = number.length() - 1; i >= 0 ; i--) {
                conversionIntToDecimalResult = conversionIntToDecimalResult.add(new BigInteger(String.valueOf(Character.getNumericValue(number.charAt(i)))).multiply(BigDecimal.valueOf(Math.pow(base.doubleValue(), k.doubleValue())).toBigIntegerExact()));
                k = k.add(BigInteger.ONE);
            }
        }
    }
}
