public class Adder {
    public static void subtract(Word32 a, Word32 b, Word32 result) {
        Bit temp = new Bit(false);
        Word32 bNegative = new Word32();
        b.copy(bNegative);
        for (int i = 0; i < 32; i++) {
            b.getBitN(i, temp);
            temp.not(temp);
            bNegative.setBitN(i, temp);
        }
        Word32 one = new Word32();
        one.setBitN(31, new Bit(true));
        add(bNegative, one, bNegative);
        add(a, bNegative, result);
    }

    public static void add(Word32 a, Word32 b, Word32 result) {
        Word32 carry = new Word32();
        Bit aBit = new Bit(false);
        Bit bBit = new Bit(false);
        Bit carryBit = new Bit(false);
        Bit resultBit = new Bit(false);
        Bit test1Bit = new Bit(false); // aBit XOR bBit
        Bit test2Bit = new Bit(false); // aBit AND bBit
        Bit test3Bit = new Bit(false); // test1Bit AND carryBit
        Bit carryNextBit = new Bit(false); // test2Bit OR test3Bit
        for (int i = 31; i >= 0; i--) {
            a.getBitN(i, aBit);
            b.getBitN(i, bBit);
            carry.getBitN(i, carryBit);
            aBit.xor(bBit, test1Bit);
            test1Bit.xor(carryBit, resultBit);
            result.setBitN(i, resultBit);
            if (i != 0) {
                aBit.and(bBit, test2Bit);
                test1Bit.and(carryBit, test3Bit);
                test2Bit.or(test3Bit, carryNextBit);
                carry.setBitN(i - 1, carryNextBit);
            }
        }
    }
}
