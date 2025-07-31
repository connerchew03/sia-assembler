public class Shifter {
    public static void LeftShift(Word32 source, int amount, Word32 result) {
        Bit digit = new Bit(false);
        for (int i = amount; i < 32; i++) {
            source.getBitN(i, digit);
            result.setBitN(i - amount, digit);
        }
    }

    public static void RightShift(Word32 source, int amount, Word32 result) {
        Bit digit = new Bit(false);
        for (int i = amount; i < 32; i++) {
            source.getBitN(i - amount, digit);
            result.setBitN(i, digit);
        }
    }
}
