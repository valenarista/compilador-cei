///-5&exitosamente
class UnaryTest {
    int negate(int x) {
        return -x;
    }
}

class InitA3 {
    static void main() {
        var ut = new UnaryTest();
        debugPrint(ut.negate(5));
    }
}