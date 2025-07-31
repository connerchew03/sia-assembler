public class ALU {
    public Word16 instruction = new Word16();
    public Word32 op1 = new Word32();
    public Word32 op2 = new Word32();
    public Word32 result = new Word32();
    public Bit less = new Bit(false);
    public Bit equal = new Bit(false);

    public void doInstruction() {
        Word32 zero = new Word32(), one = new Word32();
        one.setBitN(31, new Bit(true));
        result = new Word32();
        int opcode = 0;
        Bit test = new Bit(false);
        for (int i = 0; i < 5; i++) {
            instruction.getBitN(i, test);
            if (test.getValue() == Bit.boolValues.TRUE)
                opcode += (int) Math.pow(2, 4 - i);
        }
        switch (opcode) {
            case 1: // ADD
                Adder.add(op1, op2, result);
                break;
            case 2: // AND
                op1.and(op2, result);
                break;
            case 3: // MULTIPLY
                Multiplier.multiply(op1, op2, result);
                break;
            case 4: // LEFTSHIFT
                op1.copy(result);
                while (!op2.equals(zero)) {
                    Shifter.LeftShift(result, 1, result);
                    Adder.subtract(op2, one, op2);
                }
                break;
            case 5: // SUBTRACT
                Adder.subtract(op1, op2, result);
                break;
            case 6: // OR
                op1.or(op2, result);
                break;
            case 7: // RIGHTSHIFT
                op1.copy(result);
                while (!op2.equals(zero)) {
                    Shifter.RightShift(op1, 1, result);
                    result.copy(op1);
                    Adder.subtract(op2, one, op2);
                }
                break;
            case 11: // COMPARE
                equal.assign(Bit.boolValues.TRUE);
                less.assign(Bit.boolValues.FALSE);
                Bit op1bit = new Bit(false), op2bit = new Bit(false);
                op1.getBitN(0, op1bit);
                op2.getBitN(0, op2bit);
                if (op1bit.getValue() == Bit.boolValues.TRUE && op2bit.getValue() == Bit.boolValues.FALSE) { // op1 is negative and op2 is positive
                    equal.assign(Bit.boolValues.FALSE);
                    less.assign(Bit.boolValues.TRUE);
                }
                else if (op1bit.getValue() == Bit.boolValues.FALSE && op2bit.getValue() == Bit.boolValues.TRUE) // op1 is positive and op2 is negative
                    equal.assign(Bit.boolValues.FALSE);
                else { // both have the same sign
                    for (int i = 1; i < 32; i++) {
                        op1.getBitN(i, op1bit);
                        op2.getBitN(i, op2bit);
                        if (op1bit.getValue() == Bit.boolValues.TRUE && op2bit.getValue() == Bit.boolValues.FALSE) {
                            equal.assign(Bit.boolValues.FALSE);
                            break;
                        }
                        else if (op1bit.getValue() == Bit.boolValues.FALSE && op2bit.getValue() == Bit.boolValues.TRUE) {
                            equal.assign(Bit.boolValues.FALSE);
                            less.assign(Bit.boolValues.TRUE);
                            break;
                        }
                    }
                }
        }
    }
}
