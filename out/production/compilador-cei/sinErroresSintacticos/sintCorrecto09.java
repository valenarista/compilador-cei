///[SinErrores]
class WhileTest {
    void vacio() {
        while (true) {
            ;
        }
    }
    void conCondicion() {
        while (i>10) {
            --i;
        }
    }
    void conIf(){
        while (i>10) {
            if (i==15) {
                i = i + 1;
            } else {
                i = i - 1;
            }
        }
    }
}