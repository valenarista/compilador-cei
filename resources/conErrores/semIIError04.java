///[Error:m2|10]
// Tipos incompatibles en el pasaje de parametros: B no conforma con String - ln: 10
class A {
     int a1;
     int v1;
     B b1;

     void m1(int p1) 
    {
        m2(p1+(v1-10), b1, "hola!");
    }
    //TODO CAMBIAR b1 por new B()
     void m2(int p1, String x, B p2)
    {}
         
   

}

class B{
}


class Init{
    static void main()
    { }
}


