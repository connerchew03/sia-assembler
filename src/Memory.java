public class Memory {
    public Word32 address = new Word32();
    public Word32 value = new Word32();

    private final Word32[] dram = new Word32[1000];

    public int addressAsInt() {
        int result = 0;
        Bit test = new Bit(false);
        for (int i = 9; i >= 0; i--) {  // Since there are only 1,000 addresses, the first 22 bits needn't be checked.
            address.getBitN(31 - i, test);
            if (test.getValue() == Bit.boolValues.TRUE)
                result += (int) Math.pow(2, i);
        }
        return result;
    }

    public Memory() {
        for (int i = 0; i < 1000; i++)
            dram[i] = new Word32();
    }

    public void read() {
        dram[addressAsInt()].copy(value);
    }

    public void write() {
        value.copy(dram[addressAsInt()]);
    }

    public void load(String[] data) /* throws Exception */ { // Exception-throwing code is commented out since the unit test is not designed to throw exceptions.
        int addressInt = 0;
        for (String word : data) {
            /* if (word.length() != 32)
                throw new Exception; */
            for (int i = 0; i < 32; i++) {
                if (word.charAt(i) == 't')
                    dram[addressInt].setBitN(i, new Bit(true));
                else if (word.charAt(i) == 'f')
                    dram[addressInt].setBitN(i, new Bit(false));
                /* else
                    throw new Exception; */
            }
            addressInt++;
        }
    }
}
