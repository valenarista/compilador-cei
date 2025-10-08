/// [SinErrores]
abstract class A {
    abstract int m1();
}
abstract class B extends A {
    abstract int m2();
}
class C extends B {
    int m1() { return 1; }
    int m2() { return 2; }
}