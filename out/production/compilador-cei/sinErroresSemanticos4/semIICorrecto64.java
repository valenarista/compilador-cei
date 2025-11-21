//[SinErrores]
class Punto {
    int x;
    int y;
}

class TestReturnObjeto {
    Punto crear() {
        return new Punto();
    }

    Punto obtener(boolean nuevo) {
        if (nuevo) {
            return new Punto();
        } else {
            return null;
        }
    }
}
