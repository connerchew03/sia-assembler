import java.util.HashMap;
import java.util.LinkedList;

public class Assembler {
    public static String[] assemble(String[] input) {
        HashMap<String, String> opcodes = new HashMap<>();
        opcodes.put("halt", "fffff");
        opcodes.put("add", "fffft");
        opcodes.put("and", "ffftf");
        opcodes.put("multiply", "ffftt");
        opcodes.put("leftshift", "fftff");
        opcodes.put("subtract", "fftft");
        opcodes.put("or", "ffttf");
        opcodes.put("rightshift", "ffttt");
        opcodes.put("syscall", "ftfff");
        opcodes.put("call", "ftfft");
        opcodes.put("return", "ftftf");
        opcodes.put("compare", "ftftt");
        opcodes.put("ble", "fttff");
        opcodes.put("blt", "fttft");
        opcodes.put("bge", "ftttf");
        opcodes.put("bgt", "ftttt");
        opcodes.put("beq", "tffff");
        opcodes.put("bne", "tffft");
        opcodes.put("load", "tfftf");
        opcodes.put("store", "tfftt");
        opcodes.put("copy", "tftff");

        String[] instructions = new String[input.length];
        for (int i = 0; i < input.length; i++) {
            String[] instruction = input[i].split(" ");
            instructions[i] = opcodes.get(instruction[0]);
            int digit;
            boolean isNegative;
            switch (instruction[0]) {
                case "halt":
                case "return":
                    instructions[i] += "fffffffffff";
                    break;
                case "syscall":
                case "call":
                case "ble":
                case "blt":
                case "bge":
                case "bgt":
                case "beq":
                case "bne":
                    digit = Integer.parseInt(instruction[1]);
                    if (digit < 0) {
                        isNegative = true;
                        digit = -digit - 1;
                    }
                    else
                        isNegative = false;
                    for (int j = 10; j >= 0; j--) {
                        if (digit >= Math.pow(2, j)) {
                            instructions[i] += (isNegative ? 'f' : 't');
                            digit -= (int)Math.pow(2, j);
                        }
                        else
                            instructions[i] += (isNegative ? 't' : 'f');
                    }
                    break;
                default:
                    if (instruction[1].charAt(0) == 'r') {
                        instructions[i] += 'f';
                        digit = Integer.parseInt(instruction[1].substring(1));
                    }
                    else {
                        instructions[i] += 't';
                        digit = Integer.parseInt(instruction[1]);
                    }
                    if (digit < 0) {
                        isNegative = true;
                        digit = -digit - 1;
                    }
                    else
                        isNegative = false;
                    for (int j = 4; j >= 0; j--) {
                        if (digit >= Math.pow(2, j)) {
                            instructions[i] += (isNegative ? 'f' : 't');
                            digit -= (int)Math.pow(2, j);
                        }
                        else
                            instructions[i] += (isNegative ? 't' : 'f');
                    }
                    digit = Integer.parseInt(instruction[2].substring(1));
                    if (digit < 0) {
                        isNegative = true;
                        digit = -digit - 1;
                    }
                    else
                        isNegative = false;
                    for (int j = 4; j >= 0; j--) {
                        if (digit >= Math.pow(2, j)) {
                            instructions[i] += (isNegative ? 'f' : 't');
                            digit -= (int)Math.pow(2, j);
                        }
                        else
                            instructions[i] += (isNegative ? 't' : 'f');
                    }
            }
        }
        return instructions;
    }

    public static String[] finalOutput(String[] input) {
        String[] formattedInstructions;
        if (input.length % 2 == 0)
            formattedInstructions = new String[input.length / 2];
        else
            formattedInstructions = new String[input.length / 2 + 1];
        try {
            for (int i = 0; i < formattedInstructions.length; i++) {
                formattedInstructions[i] = input[2 * i];
                formattedInstructions[i] += input[2 * i + 1];
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            formattedInstructions[formattedInstructions.length - 1] += "ffffffffffffffff";
        }
        return formattedInstructions;
    }
}
