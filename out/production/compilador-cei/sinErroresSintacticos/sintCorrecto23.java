///[SinErrores]
class TestTernario{
    int a;
    int b = 5;
    int test(int a, int b, int c){
        return a > b ? a : b > c ? b : c;
    }
    void test2(){
        this.a = this.b > 3 ? 5 : 7;
    }
}