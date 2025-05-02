public class G2E1 {
    
    // O objetivo deste exercício é mostrar como evitar as anomalias criadas pelas "Race Conditions" que foram explicadas no Ex 3 do Guião 1
    //para isso foram acrescentados locks que impedem que 2 threads acedam a uma secção do código ao mesmo tempo, neste caso onde se incrementa o contador
    public static void main(String[] args) throws InterruptedException{
        
        final int N = Integer.parseInt(args[0]); 
        final int I = Integer.parseInt(args[1]); 
        Counter counter = new Counter();         
        
        
        Thread[] listaT = new Thread[N];
        
        for (int i = 0; i < N; i++){
            listaT[i] = new Incrementer(I, counter);
            listaT[i].start();
        }

        for (int n = 0; n < N; n++){
            listaT[n].join();                   
        }

        System.out.println(counter.value());
    }
}

class Counter {
    
    private int c;

    // Acrescentamos a keyword "synchronized" neste método previne que diferentes threads acedam às zonas de secção crítica, ou seja, garante exclusão mútua 
    // Assim vamos previnir os erros que ocorriam na versão do Guião anterior (Ex 2)
    synchronized public void increment(){
        c++;
    }

    // A keyword "synchronized" utiliza o "lock" íntrinseco ao objeto quando ele opera na secção crítica
    //e retorna o unlock quando sai da mesma.
    public int value(){
        return c;
    }
}

class Incrementer extends Thread {

    final int I;
    private Counter c;

    Incrementer(int I, Counter c) {
        this.I = I;
        this.c = c;
    }

    public void run(){
        for (int i = 0; i < I; i++){
            c.increment();
        }
    }
}