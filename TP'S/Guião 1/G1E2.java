public class G1E2 {
    
    public static void main(String[] args) throws InterruptedException{
        
        final int N = Integer.parseInt(args[0]); // número de threads
        final int I = Integer.parseInt(args[1]); // até que número cada thread vai imprimir
        Counter counter = new Counter();         // Criamos um objeto que será um contador Global da função Toda
        
        // Criamos um array de threads
        Thread[] listaT = new Thread[N];
        
        // Este ciclo inicializa as threads
        for (int i = 0; i < N; i++){
            listaT[i] = new Incrementer(I, counter);
            listaT[i].start();
        }

        // Este ciclo aguarda que as threads acabem e se juntem, caso não o fizessemos alguma Thread poderia 
        //imprimir o valor final antes de todas as threads incrementarem o contador.
        for (int n = 0; n < N; n++){
            listaT[n].join();                   // o join espera que as threads acabem
        }

        System.out.println(counter.value());
    }
}

class Counter {
    
    private int c;

    public void increment(){
        c++;
    }

    public int value(){
        return c;
    }
}

class Incrementer extends Thread {

    // até que número cada Thread vai imprimir
    final int I;
    private Counter c;

    Incrementer(int I, Counter c) {
        this.I = I;
        this.c = c;
    }

    // método run (obrigatório), quando a thread for ativada irá executar esta função
    public void run(){
        for (int i = 0; i < I; i++){
            c.increment();
        }
    }
}
