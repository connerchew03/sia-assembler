public class Bit {
    public enum boolValues { FALSE, TRUE }
    private boolValues bool;

    public Bit(boolean value) {
        if (value)
            this.assign(boolValues.TRUE);
        else
            this.assign(boolValues.FALSE);
    }

    public boolValues getValue() {
        return bool;
    }

    public void assign(boolValues value) {
        bool = value;
    }

    public void and(Bit b2, Bit result) {
        and(this, b2, result);
    }

    public static void and(Bit b1, Bit b2, Bit result) {
        switch (b1.getValue()) {
            case FALSE:
                result.assign(boolValues.FALSE);
                break;
            case TRUE:
                switch (b2.getValue()) {
                    case FALSE:
                        result.assign(boolValues.FALSE);
                        break;
                    case TRUE:
                        result.assign(boolValues.TRUE);
                        break;
                }
        }
    }

    public void or(Bit b2, Bit result) {
        or(this, b2, result);
    }

    public static void or(Bit b1, Bit b2, Bit result) {
        switch (b1.getValue()) {
            case TRUE:
                result.assign(boolValues.TRUE);
                break;
            case FALSE:
                switch (b2.getValue()) {
                    case TRUE:
                        result.assign(boolValues.TRUE);
                        break;
                    case FALSE:
                        result.assign(boolValues.FALSE);
                        break;
                }
        }
    }

    public void xor(Bit b2, Bit result) {
        xor(this, b2, result);
    }

    public static void xor(Bit b1, Bit b2, Bit result) {
        switch (b1.getValue()) {
            case FALSE:
                switch (b2.getValue()) {
                    case FALSE:
                        result.assign(boolValues.FALSE);
                        break;
                    case TRUE:
                        result.assign(boolValues.TRUE);
                }
                break;
            case TRUE:
                switch (b2.getValue()) {
                    case FALSE:
                        result.assign(boolValues.TRUE);
                        break;
                    case TRUE:
                        result.assign(boolValues.FALSE);
                }
        }
    }

    public static void not(Bit b2, Bit result) {
        switch (b2.getValue()) {
            case FALSE:
                result.assign(boolValues.TRUE);
                break;
            case TRUE:
                result.assign(boolValues.FALSE);
        }
    }

    public void not(Bit result) {
        not(this, result);
    }

    public String toString() {
        return switch (bool) {
            case TRUE -> "t";
            default -> "f";
        };
    }
}
