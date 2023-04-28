package org.example;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static List<MathSymbol> MathAnalyze(String inpText, Table input) {
        ArrayList<MathSymbol> mathSymbols = new ArrayList<>();
        String st = "ABCD";
        int pos = 0;
        while (pos < inpText.length()) {
            char s = inpText.charAt(pos);
            switch (s) {
                case '(':
                    mathSymbols.add(new MathSymbol(Math.LEFT_BRACE, s));
                    pos++;
                    continue;
                case ')':
                    mathSymbols.add(new MathSymbol(Math.RIGHT_BRACE, s));
                    pos++;
                    continue;
                case '+':
                    mathSymbols.add(new MathSymbol(Math.OP_PLUS, s));
                    pos++;
                    continue;
                case '-':
                    mathSymbols.add(new MathSymbol(Math.OP_MINUS, s));
                    pos++;
                    continue;
                case '*':
                    mathSymbols.add(new MathSymbol(Math.OP_MUL, s));
                    pos++;
                    continue;
                case '/':
                    mathSymbols.add(new MathSymbol(Math.OP_DIV, s));
                    pos++;
                    continue;
                default:
                    if (s <= '9' && s >= '0') {
                        StringBuilder sb = new StringBuilder();
                        do {
                            sb.append(s);
                            pos++;
                            if (pos >= inpText.length()) {
                                break;
                            }
                            s = inpText.charAt(pos);
                        } while (s <= '9' && s >= '0');
                        mathSymbols.add(new MathSymbol(Math.NUMBER, sb.toString()));
                    } else if (st.contains(String.valueOf(s))) {
                        StringBuilder sb = new StringBuilder();
                        do {
                            sb.append(s);
                            pos++;
                            if (pos >= inpText.length()) {
                                break;
                            }
                            s = inpText.charAt(pos);
                        } while (s <= '9' && s >= '0');
                        String cor = sb.toString();
                        List<MathSymbol> mathSymbols1 = MathAnalyze(input.getData(cor.charAt(0), cor.charAt(1)), input);
                        for (int k = 0; k < mathSymbols1.size(); k++) {
                            mathSymbols.add(mathSymbols1.get(k));
                        }
                        } else {
                            if (s != ' ') {
                                throw new RuntimeException("Unexpected character: " + s);
                            }
                            pos++;
                        }
            }
        }
        mathSymbols.add(new MathSymbol(Math.EOF, ""));
        return mathSymbols;
    }
    public static Table calculate(Table input) {
        String str;
        char c;
        for (int i = 0; i < input.getRows(); i++) {
            for (int j = 0; j < input.getColumns(); j++) {
                str = input.getData(i, j);
                if (str.charAt(0) != '=') {
                    continue;
                } else {
                    List<MathSymbol> mathSymbols = MathAnalyze(str.substring(1), input);
                    MathBuffer mathBuffer =  new MathBuffer(mathSymbols);
                    System.out.println(expr(mathBuffer));
                }
            }
        }
        return input;
    }

    public static void compare(Table result, Table expected) {
        boolean isEqual = true;
        for (int i = 0; i < result.getRows(); i++) {
            for (int j = 0; j < result.getColumns(); j++) {
                if (result.getData(i, j).equals(expected.getData(i, j))) {
                    isEqual = true;
                } else {
                    isEqual = false;
                    break;
                }
            }
        }
        if (isEqual == true) {
            System.out.println("Нормас");
        } else {
            System.out.println("Не нормас");
        }
    }

    public static void main(String[] args) {
        Table tables[] = ReadJson.readJson("/home/kairat/java/test_work_QR/src/L1-basic-calc.json");
        Table inputTable = tables[0];
        Table expectedTable = tables[1];
        Table resultTable = calculate(inputTable);
//        compare(resultTable, expectedTable);

    }
    public static class MathSymbol {
        Math type;
        String value;

        public MathSymbol(Math type, String value) {
            this.type = type;
            this.value = value;
        }

        public MathSymbol(Math type, Character value) {
            this.type = type;
            this.value = value.toString();
        }

        @Override
        public String toString() {
            return "MathSymbol{" +
                    "type=" + type +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public static class MathBuffer {
        private int pos;
        public List<MathSymbol> mathSymbols;
        public MathBuffer (List<MathSymbol> mathSymbols) {
            this.mathSymbols = mathSymbols;
        }
        public MathSymbol next() {
            return mathSymbols.get(pos++);
        }

        public void back() {
            pos--;
        }

        public int getPos() {
            return pos;
        }
    }
    public static int plusminus (MathBuffer mathSymbols) {
        int value = multdiv(mathSymbols);
        while (true) {
            MathSymbol mathSymbol = mathSymbols.next();
            switch (mathSymbol.type) {
                case OP_PLUS:
                    value += multdiv(mathSymbols);
                    break;
                case OP_MINUS:
                    value -= multdiv(mathSymbols);
                default:
                    mathSymbols.back();
                    return value;
            }
        }
    }
    public static int expr (MathBuffer mathSymbols) {
        MathSymbol mathSymbol = mathSymbols.next();
        if (mathSymbol.type == Math.EOF) {
            return 0;
        } else {
            mathSymbols.back();
            return plusminus(mathSymbols);
        }
    }
    public static int multdiv (MathBuffer mathSymbols) {
        int value = factor(mathSymbols);
        while (true) {
            MathSymbol mathSymbol = mathSymbols.next();
            switch (mathSymbol.type) {
                case OP_MUL:
                    value *= factor(mathSymbols);
                    break;
                case OP_DIV:
                    value /= factor(mathSymbols);
                default:
                    mathSymbols.back();
                    return value;
            }
        }
    }
    public static int factor (MathBuffer mathSymbols) {
        MathSymbol mathSymbol = mathSymbols.next();
        switch (mathSymbol.type) {
            case  NUMBER:
                return Integer.parseInt(mathSymbol.value);
            case LEFT_BRACE:
                int value = expr(mathSymbols);
                mathSymbol = mathSymbols.next();
                if (mathSymbol.type != Math.RIGHT_BRACE) {
                    throw new RuntimeException("Unexpected token: " + mathSymbol.value);
                }
                return value;
            default:
                throw new RuntimeException("Unexpected token: " + mathSymbol.value);
        }
    }
}

