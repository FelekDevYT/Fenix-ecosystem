package me.felek.fenix.compiler;

import me.felek.fenix.utils.Exit;

import java.util.*;
import java.util.stream.Collectors;

public class Interpreter {
    public static Map<String, Integer> ops = new HashMap<>();
    public static Map<String, Integer> labels = new HashMap<>();
    public static Map<String, Integer> vars = new HashMap<>();
    private static int lastVarID = 0;
    private static int lastLabelID = 0;
    private static int lastVariableMemory = 0;

    public static int[] compile(List<Token> toks) {
        List<Integer> bytecode = new ArrayList<>();

        for (int i = 0; i < toks.size(); i++) {
            Token current = toks.get(i);

            if (current.getType() == TokenType.OPERATION) {
                switch (current.getLexeme().toUpperCase()) {
                    case "MOV":
                        Token arg1 = toks.get(i+1);
                        Token arg2 = toks.get(i+2);
                        if (arg2.getType() == TokenType.NUMBER) {//MOV REG, VALUE
                            bytecode.add(0x10);
                            bytecode.add(getRegisterNumber(arg1));
                            bytecode.add(parseNumber(arg2.getLexeme()));
                        } else {//MOV REG, REG
                            bytecode.add(0x11);
                            bytecode.add(getRegisterNumber(arg1));
                            bytecode.add(getRegisterNumber(arg2));
                        }
                        i += 2;
                        break;
                    case "ADD":
                        handleMath(bytecode, 0x200, toks.get(i+1), toks.get(i+2));
                        i += 2;
                        break;
                    case "SUB":
                        handleMath(bytecode, 0x210, toks.get(i+1), toks.get(i+2));
                        i += 2;
                        break;
                    case "MUL":
                        handleMath(bytecode, 0x220, toks.get(i+1), toks.get(i+2));
                        i += 2;
                        break;
                    case "DIV":
                        handleMath(bytecode, 0x230, toks.get(i+1), toks.get(i+2));
                        i += 2;
                        break;
                    case "CMP":
                        handleLogic(bytecode, 0x300, toks.get(i+1), toks.get(i+2));
                        i += 2;
                        break;
                    case "GT":
                        handleLogic(bytecode, 0x320, toks.get(i+1), toks.get(i+2));
                        i += 2;
                        break;
                    case "LT":
                        handleLogic(bytecode, 0x330, toks.get(i+1), toks.get(i+2));
                        i += 2;
                        break;
                    case "ZERO":
                        Token arg = toks.get(i+1);
                        if (arg.getType() == TokenType.NUMBER) {
                            bytecode.add(0x310);
                            bytecode.add(parseNumber(arg.getLexeme()));
                        } else {
                            bytecode.add(0x311);
                            bytecode.add(getRegisterNumber(arg));
                        }
                        i += 1;
                        break;
                    case "LABEL":
                        String name = toks.get(i+1).getLexeme();
                        int id = getLabelID(name);
                        bytecode.add(0x40);
                        bytecode.add(id);
                        i += 1;
                        break;
                    case "JMP":
                        bytecode.add(0x41);
                        bytecode.add(getLabelID(toks.get(i+1).getLexeme()));
                        i += 1;
                        break;
                    case "JE":
                        bytecode.add(0x42);
                        bytecode.add(getLabelID(toks.get(i+1).getLexeme()));
                        i += 1;
                        break;
                    case "JD":
                        bytecode.add(0x43);
                        bytecode.add(getLabelID(toks.get(i+1).getLexeme()));
                        i += 1;
                        break;
                    case "JL":
                        bytecode.add(0x44);
                        bytecode.add(getLabelID(toks.get(i+1).getLexeme()));
                        i += 1;
                        break;
                    case "JZ":
                        bytecode.add(0x45);
                        bytecode.add(getLabelID(toks.get(i+1).getLexeme()));
                        i += 1;
                        break;
                    case "JNE":
                        bytecode.add(0x46);
                        bytecode.add(getLabelID(toks.get(i+1).getLexeme()));
                        i += 1;
                        break;
                    case "CALL":
                        bytecode.add(0x47);
                        bytecode.add(getLabelID(toks.get(i+1).getLexeme()));
                        i += 1;
                        break;
                    case "RET":
                        bytecode.add(0x48);
                        break;
                    case "DB":
//                        arg1 = toks.get(i+1);
//                        arg2 = toks.get(i+2);
//                        if (arg2.getType() == TokenType.NUMBER) {//STORE ADDR, VALUE
//                            bytecode.add(0x51);
//                            bytecode.add(parseNumber(arg2.getLexeme()));
//                        } else {//STORE ADDR, REG
//                            bytecode.add(0x50);
//                            bytecode.add(getRegisterNumber(arg2));
//                        }
//                        i += 2;
                        //DB varname, value
                        arg1 = toks.get(i+1);
                        arg2 = toks.get(i+2);
                        int prev = lastVariableMemory;
                        if (arg2.getType() == TokenType.STRING) {
                            String unescapedString = unescapeString(arg2.getLexeme());
                            for (char ch : unescapedString.toCharArray()) {
                                bytecode.add(0x51);
                                bytecode.add(++lastVariableMemory);
                                bytecode.add((int) ch);
                            }
                            bytecode.add(0x51);
                            bytecode.add(++lastVariableMemory);
                            bytecode.add(0);
                        } else {
                            bytecode.add(0x51);
                            bytecode.add(++lastVariableMemory);
                            bytecode.add(parseNumber(arg2.getLexeme()));
                            bytecode.add(0x51);
                            bytecode.add(++lastVariableMemory);
                            bytecode.add(0);
                        }
                        vars.put(arg1.getLexeme(), prev+1);
                        break;
                    case "MEMWRITE":
                        arg1 = toks.get(i+1);
                        arg2 = toks.get(i+2);

                        if (arg2.getType() == TokenType.NUMBER) {
                            bytecode.add(0x51);
                            bytecode.add(parseNumber(arg1.getLexeme()));
                            bytecode.add(parseNumber(arg2.getLexeme()));
                        } else {
                            bytecode.add(0x50);
                            bytecode.add(parseNumber(arg1.getLexeme()));
                            bytecode.add(getRegisterNumber(arg2));
                        }
                        i += 2;
                        break;
                    case "LOAD"://LOAD R5, varname
                        arg1 = toks.get(i+1);
                        arg2 = toks.get(i+2);

                        Integer variableAddress = vars.get(arg2.getLexeme());
                        if (variableAddress == null) {
                            System.err.println("Undefined variable " + arg2.getLexeme());
                            Exit.UNDEFINED_VARIABLE.exit();
                        }

                        bytecode.add(0x10);
                        bytecode.add(getRegisterNumber(arg1));
                        bytecode.add(variableAddress);
                        i += 2;
                        break;
                    case "INT":
                        bytecode.add(0x60);
                        bytecode.add(parseNumber(toks.get(i+1).getLexeme()));
                        i += 1;
                        break;
                    case "EXTERN"://extern "0x10 0x15 0x20"
                        arg = toks.get(i+1);
                        String[] program = arg.getLexeme().replaceAll(" ", "").split(",");
                        int[] numbers = Arrays.stream(program)
                                .mapToInt(Integer::decode)
                                .toArray();
                        bytecode.addAll(Arrays.stream(numbers).boxed().toList());
                        i += 1;
                        break;
                    case "PUSH"://push value
                        bytecode.add(0x70);
                        bytecode.add(parseNumber(toks.get(i+1).getLexeme()));
                        i += 1;
                        break;
                    case "POP"://POP R0
                        bytecode.add(0x71);
                        bytecode.add(getRegisterNumber(toks.get(i+1)));
                        i += 1;
                        break;
                }
            }
        }

        return bytecode.stream().mapToInt(Integer::intValue).toArray();
    }

    private static String unescapeString(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '\\') {
                if (i + 1 < s.length()) {
                    i++;
                    char nextChar = s.charAt(i);
                    switch (nextChar) {
                        case 'n':
                            sb.append('\n');
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                        case 'r':
                            sb.append('\r');
                            break;
                        case 'b':
                            sb.append('\b');
                            break;
                        case '\\':
                            sb.append('\\');
                            break;
                        case '"':
                            sb.append('"');
                            break;
                        default:
                            sb.append('\\').append(nextChar);
                            break;
                    }
                } else {
                    sb.append('\\');
                }
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    private static void handleLogic(List<Integer> bytecode, int opcode, Token arg1, Token arg2) {
        if (arg1.getType() == TokenType.NUMBER) {//OPCODE VALUE, VALUE
            bytecode.add(opcode);
            bytecode.add(parseNumber(arg1.getLexeme()));
            bytecode.add(parseNumber(arg2.getLexeme()));
        } else if (arg2.getType() == TokenType.NUMBER){//OPCODE REG, VALUE
            bytecode.add(opcode + 1);
            bytecode.add(getRegisterNumber(arg1));
            bytecode.add(parseNumber(arg2.getLexeme()));
        } else {//OPCODE REG, REG
            bytecode.add(opcode + 2);
            bytecode.add(getRegisterNumber(arg1));
            bytecode.add(getRegisterNumber(arg2));
        }
    }

    private static void handleMath(List<Integer> bytecode, int opcode, Token arg1, Token arg2) {
        if (arg2.getType() == TokenType.NUMBER){//OPCODE REG, VALUE
            bytecode.add(opcode + 1);
            bytecode.add(getRegisterNumber(arg1));
            bytecode.add(parseNumber(arg2.getLexeme()));
        } else {//OPCODE REG, REG
            bytecode.add(opcode);
            bytecode.add(getRegisterNumber(arg1));
            bytecode.add(getRegisterNumber(arg2));
        }
    }

    private static int getRegisterNumber(Token tok) {
        String lexeme = tok.getLexeme();
        if (!Character.isDigit(lexeme.charAt(0))) {
            return Integer.parseInt(lexeme.substring(1));
        }
        return Integer.parseInt(lexeme);
    }
    
    private static int parseNumber(String text) {
        if (text.startsWith("0x")) {
            return Integer.parseInt(text.substring(2), 16);
        } else {
            return Integer.parseInt(text);
        }
    }

    private static int getLabelID(String name) {
        return labels.computeIfAbsent(name, k -> lastLabelID++);
    }

    private static int getLastVarID(String name) {
        return vars.computeIfAbsent(name, k -> lastVarID++);
    }
}
