///1&exitosamente
class GETest {
    int greaterThanOrEqual(int a, int b) {
        if (a >= b) {
            return 1;
        }
        return 0;
    }
}

class InitA6 {
    static void main() {
        var ge = new GETest();
        debugPrint(ge.greaterThanOrEqual(8, 5));
    }
}