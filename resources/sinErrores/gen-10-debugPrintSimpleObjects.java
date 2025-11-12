///5&exitosamente

class Punto {
    int x;
    int y;
    Punto(int px, int py) {
        x = px;
        y = py;
    }
}
class Test {
    static void main() {
        var p = new Punto(5, 10);
        debugPrint(p.x);
    }
}

