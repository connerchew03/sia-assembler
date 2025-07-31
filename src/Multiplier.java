public class Multiplier {
    public static void multiply(Word32 a, Word32 b, Word32 result) {
        Bit digit = new Bit(false);
        Word32 multiple;
        for (int i = 31; i >= 0; i--) {
            b.getBitN(i, digit);
            if (digit.getValue() == Bit.boolValues.TRUE) {
                multiple = new Word32();
                int shift = 0;
                for (int j = 31 - i; j <= 31; j++) {
                    a.getBitN(j, digit);
                    multiple.setBitN(shift++, digit);
                }
                Adder.add(result, multiple, result);
            }
        }
    }
}
