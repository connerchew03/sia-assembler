import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Processor {
    private Memory mem;
    public List<String> output = new LinkedList<>();

    private boolean needNewLine = true;
    private boolean halted = false;
    private Bit test = new Bit(false);
    private Word16 instruction = new Word16();
    private int opcode;
    private Word32 op1 = new Word32();
    private Word32 op2 = new Word32();
    private boolean isImmediate = false;
    private ALU alu = new ALU();
    private Word32 memoryToFetch = new Word32();
    private Stack<Word32> stack = new Stack<>();
    private Word32 addressToReturn = new Word32();
    private Word32[] registers = new Word32[32];
    private int index1 = 0;
    private int index2 = 0;
    private Word32 one = new Word32();

    private InstructionCache inCache;
    private int clockCycles = 340;    // Since the first word is erroneously considered to be in the instruction cache from the start, the clock cycle starts out at 340 to balance it out.

    public Processor(Memory m) {
        mem = m;
        for (int i = 0; i < 32; i++)
            registers[i] = new Word32();
        one.setBitN(31, new Bit(true));
        inCache = new InstructionCache(mem);
    }

    public void run() {
        while (!halted) {
            fetch();
            decode();
            execute();
            store();
        }
        System.out.println("Final clock cycle: " + clockCycles);
    }

    private void fetch() {
        if (needNewLine) {
            clockCycles += inCache.read();
            inCache.value.getTopHalf(instruction);
        }
        else
            inCache.value.getBottomHalf(instruction);
        needNewLine = !needNewLine;
    }

    private void decode() {
        opcode = 0;
        for (int i = 0; i < 5; i++) { // Determine the opcode
            instruction.getBitN(i, test);
            if (test.getValue() == Bit.boolValues.TRUE)
                opcode += (int) Math.pow(2, 4 - i);
        }
        switch (opcode) {
            case 8:
            case 9:
            case 10:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
                op1 = new Word32();
                for (int i = 5; i < 16; i++) {
                    instruction.getBitN(i, test);
                    op1.setBitN(16 + i, test);
                }
                break;
            default:
                op1 = new Word32();
                op2 = new Word32();
                instruction.getBitN(5, test);
                if (test.getValue() == Bit.boolValues.TRUE) {
                    isImmediate = true;
                    instruction.getBitN(6, test);
                    if (test.getValue() == Bit.boolValues.TRUE) {
                        for (int i = 0; i <= 27; i++)
                            op1.setBitN(i, new Bit(true));
                    }
                    for (int i = 7; i <= 10; i++) {
                        instruction.getBitN(i, test);
                        op1.setBitN(21 + i, test);
                    }
                }
                else {
                    isImmediate = false;
                    index1 = 0;
                    for (int i = 6; i <= 10; i++) {
                        instruction.getBitN(i, test);
                        if (test.getValue() == Bit.boolValues.TRUE)
                            index1 += (int) Math.pow(2, 10 - i);
                    }
                    registers[index1].copy(op1);
                }
                index2 = 0;
                for (int i = 11; i <= 15; i++) {
                    instruction.getBitN(i, test);
                    if (test.getValue() == Bit.boolValues.TRUE)
                        index2 += (int) Math.pow(2, 15 - i);
                }
                registers[index2].copy(op2);
        }
    }

    private void execute() {
        switch (opcode) {
            case 0:
                halted = true;
                break;
            case 1:
            case 2:
            case 3:
            case 6:
            case 11:
                if (opcode == 3)
                    clockCycles += 10;
                else
                    clockCycles += 2;
                instruction.copy(alu.instruction);
                op1.copy(alu.op1);
                op2.copy(alu.op2);
                alu.doInstruction();
                break;
            case 4:
            case 5:
            case 7:
                clockCycles += 2;
                instruction.copy(alu.instruction);
                op1.copy(alu.op2);
                op2.copy(alu.op1);
                alu.doInstruction();
                break;
            case 8:
                op1.getBitN(31, test);
                stack.push(new Word32());
                mem.address.copy(stack.peek());
                stack.push(new Word32());
                mem.value.copy(stack.peek());
                if (test.getValue() == Bit.boolValues.FALSE)
                    printReg();
                else
                    printMem();
                stack.pop().copy(mem.value);
                stack.pop().copy(mem.address);
                break;
            case 18:
                if (isImmediate)
                    Adder.add(op1, op2, memoryToFetch);
                else
                    op1.copy(memoryToFetch);
        }
    }

    private void printReg() {
        for (int i = 0; i < 32; i++) {
            var line = "r" + i + ":" + registers[i].toString(); // Add the register value here...
            output.add(line);
            System.out.println(line);
        }
    }

    private void printMem() {
        for (int i = 0; i < 1000; i++) {
            Word32 addr = new Word32();
            Word32 value = new Word32();
            // Convert i to Word32 here...
            int test = i;
            for (int j = 9; j >= 0; j--) {
                if (test >= (int) Math.pow(2, j)) {
                    addr.setBitN(31 - j, new Bit(true));
                    test -= (int) Math.pow(2, j);
                }
            }
            addr.copy(mem.address);
            mem.read();
            mem.value.copy(value);
            // var line = i + ":" + value + "(" + TestConverter.toInt(value) + ")";
            var line = i + ":" + value; // The TestConverter.toInt(value) part of the above line causes the unit test to fail.
            output.add(line);
            System.out.println(line);
        }
    }

    private void store() {
        switch (opcode) {
            case 0:
                return;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                alu.result.copy(registers[index2]);
                break;
            case 9:
                Adder.add(inCache.address, one, addressToReturn);
                stack.push(new Word32());
                addressToReturn.copy(stack.peek());
                Adder.add(inCache.address, op1, inCache.address);
                return;
            case 10:
                stack.pop().copy(inCache.address);
                needNewLine = true;
                return;
            case 12:
                if (alu.less.getValue() == Bit.boolValues.TRUE || alu.equal.getValue() == Bit.boolValues.TRUE) {
                    Adder.add(inCache.address, op1, inCache.address);
                    return;
                }
                break;
            case 13:
                if (alu.less.getValue() == Bit.boolValues.TRUE) {
                    Adder.add(inCache.address, op1, inCache.address);
                    return;
                }
                break;
            case 14:
                if (alu.less.getValue() == Bit.boolValues.FALSE) {
                    Adder.add(inCache.address, op1, inCache.address);
                    return;
                }
                break;
            case 15:
                if (alu.less.getValue() == Bit.boolValues.FALSE && alu.equal.getValue() == Bit.boolValues.FALSE) {
                    Adder.add(inCache.address, op1, inCache.address);
                    return;
                }
                break;
            case 16:
                if (alu.equal.getValue() == Bit.boolValues.TRUE) {
                    Adder.add(inCache.address, op1, inCache.address);
                    return;
                }
                break;
            case 17:
                if (alu.equal.getValue() == Bit.boolValues.FALSE) {
                    Adder.add(inCache.address, op1, inCache.address);
                    return;
                }
                break;
            case 18:
                stack.push(new Word32());
                mem.address.copy(stack.peek());
                stack.push(new Word32());
                mem.value.copy(stack.peek());
                memoryToFetch.copy(mem.address);
                mem.read();
                clockCycles += 300;
                mem.value.copy(registers[index2]);
                stack.pop().copy(mem.value);
                stack.pop().copy(mem.address);
                break;
            case 19:
                stack.push(new Word32());
                mem.address.copy(stack.peek());
                stack.push(new Word32());
                mem.value.copy(stack.peek());
                op2.copy(mem.address);
                op1.copy(mem.value);
                mem.write();
                clockCycles += 300;
                stack.pop().copy(mem.value);
                stack.pop().copy(mem.address);
                break;
            case 20:
                op1.copy(registers[index2]);
        }
        if (needNewLine)
            Adder.add(inCache.address, one, inCache.address);
    }
}