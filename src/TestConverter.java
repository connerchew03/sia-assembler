public class TestConverter {
    public static void fromInt(int value, Word32 result) {
        if (value < 0) {
            result.setBitN(0, new Bit(true));
            value -= Integer.MIN_VALUE;
        }
        else
            result.setBitN(0, new Bit(false));
        for (int i = 1; i <= 31; i++) {
            if (value >= (int) Math.pow(2, 31 - i)) {
                result.setBitN(i, new Bit(true));
                value -= (int) Math.pow(2, 31 - i);
            }
            else
                result.setBitN(i, new Bit(false));
        }
    }

    public static int toInt(Word32 value) {
        int result = 0;
        Bit sign = new Bit(false);
        value.getBitN(0, sign);
        if (sign.getValue() == Bit.boolValues.TRUE)
            result = Integer.MIN_VALUE;
        for (int i = 1; i <= 31; i++) {
            value.getBitN(i, sign);
            if (sign.getValue() == Bit.boolValues.TRUE)
                result += (int) Math.pow(2, 31 - i);
        }
        return result;
    }
}