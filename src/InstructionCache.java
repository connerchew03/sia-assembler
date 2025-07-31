public class InstructionCache {
    private Memory memory;
    private Word32 firstAddress = new Word32();
    private Word32[] cache = new Word32[8];
    private Word32 one = new Word32();

    public Word32 address = new Word32();
    public Word32 value = new Word32();

    public InstructionCache(Memory mem) {
        memory = mem;
        for (int i = 0; i < cache.length; i++)
            cache[i] = new Word32();
        one.setBitN(31, new Bit(true));
        reload(address);    // Fill the instruction cache for the first time.
    }

    public int addressAsInt(Word32 address) {    // Similar to Memory's addressAsInt, but since address may be negative, it checks every single bit.
        int result = 0;
        Bit test = new Bit(false);
        for (int i = 30; i >= 0; i--) {
            address.getBitN(31 - i, test);
            if (test.getValue() == Bit.boolValues.TRUE)
                result += (int) Math.pow(2, i);
        }
        address.getBitN(0, test);
        if (test.getValue() == Bit.boolValues.TRUE)
            result += Integer.MIN_VALUE;
        return result;
    }

    public int read() {
        Word32 difference = new Word32();    // difference measures where a memory address would be relative to the instruction cache.
        address.copy(memory.address);    // Ensures that memory's address is always synchronized for when it needs to be accessed by the processor.
        Adder.subtract(address, firstAddress, difference);
        if (addressAsInt(difference) < 0 || addressAsInt(difference) >= 8)    // e.g., if addressAsInt(difference) = 2, it means address corresponds to the third element in cache.
            return reload(address);
        cache[addressAsInt(difference)].copy(value);
        return 10;    // This and reload's return values are used to increment countCycles in Processor.
    }

    public int reload(Word32 addr) {
        for (int i = 0; i < 8; i++) {
            memory.read();
            memory.value.copy(cache[i]);
            Adder.add(memory.address, one, memory.address);
        }
        addr.copy(memory.address);
        addr.copy(firstAddress);    // Whenever cache is reloaded, the first element will always correspond to the address that triggered the cache reload.
        cache[0].copy(value);
        return 350;
    }
}
