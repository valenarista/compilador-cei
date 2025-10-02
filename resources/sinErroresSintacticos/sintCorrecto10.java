///[SinErrores]
class IfElseTest {
    void test1() {
        if (i == 15) {
            i = i + 1;
        }
    }
    void test2() {
        if (i == 15) {
            i = i + 1;
        } else {
            i = i - 1;
        }
    }
    void test3() {
        if (i == 15) {
            i = i + 1;
        } else if (i == 14) {
            i = i + 2;
        } else {
            i = i - 1;
        }
    }
    void test4(){
        if (i == 15) {
            if (i == 14) {
                i = i + 2;
            } else {
                i = i - 1;
            }
        } else {
            i = i - 1;
        }
    }
}