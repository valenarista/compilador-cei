//[Error:=|5]

class A {
    B a1;
    int a2;
    void m1(B p1) {
        p1.m3().a2 = 4;
    }
}
class B {
    A m3() {
        return new A();
    }
}



class Init{
    static void main()
    {

    }


}


