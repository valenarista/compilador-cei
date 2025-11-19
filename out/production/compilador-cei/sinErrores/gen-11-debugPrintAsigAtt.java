///77&exitosamente
class StorageTest {
    int value;

    void setValue(int v) {
        value = v;
    }

    int getValue() {
        return value;
    }
}

class InitA1 {
    static void main() {
        var st = new StorageTest();
        st.setValue(77);
        debugPrint(st.getValue());
    }
}
