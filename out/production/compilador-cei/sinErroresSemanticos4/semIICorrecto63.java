//[SinErrores]
class A {
    int metodo(int x, int y, int z) {
        if (x > 0) {
            if (y > 0) {
                if (z > 0) {
                    return z+y;
                } else {
                    return 2;
                }
            } else {
                return 3;
            }
        } else {
            return x;
        }
    }


}
