public class Queue<T> {
    private int top = -1;
    private int base = 0;
    private Pixel[] data;

    public Queue(int tamanho) {
        data =  new Pixel[tamanho];
    }

    public void add(T item) {
        if (isFull()) {
            throw new IllegalStateException("A fila está cheia.");
        }
        data[++top] = (Pixel) item;
    }

    public boolean contains(Pixel item) {
        for (int i = base; i <= top; i++) {
            if (data[i].getX() == item.getX() && data[i].getY() == item.getY()){
                return true;
            }
        }
        return false;
    }

    public Pixel remove() {
        if (isEmpty()) {
            throw new IllegalStateException("A fila está vazia.");
        }
        Pixel item = data[base];

        for (int i = 0; i < top; i++) {
            data[i] = data[i + 1];
        }
        data[top] = null;
        top--;
        return item;
    }

    public void clear() {
        for (int i = 0; i <= top; i++) {
            data[i] = null;
        }
        top = -1;
    }

    public boolean isFull() {
        return top == data.length - 1;
    }

    public boolean isEmpty() {
        return top < base;
    }
}