//[Error:a1|10]

class A {
    private int a1;
}
class B extends A{
}
class C extends B{
    void m1(){
        a1 = 3;
    }
}

class Init{
    static void main()
    {

    }


}


