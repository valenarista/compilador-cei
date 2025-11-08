//[Error:=|7]

class A {
    B a1;
    int a2;
    void m1(B p1) {
        a1.a2.x() = 5;
    }
    int x(){
        return 3;
    }
}
class B{
    A a2;
    A m3() {
        return new A();
    }
}




class Init{
    static void main()
    {

    }


}


