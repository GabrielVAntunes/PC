class Counter {
    private int c = 0;

    public void increment(){
        c+=1;
    }
    
    public int value() {
        return c;
    }
}

class Incrementer extends Thread {

    final int I;
    private Counter c;

    Incrementer (int I, Counter c) {                       // de modo a evitar que o I e o C seja uma variável global e que seja passada por parametro no método run
        this.I = I;                                        //passamo-la como variável local e registamos os valores utilizando um construtor
        this.c = c;                        
    }

    public void run(){
        for (int i = 1; i <= I; i++){
            c.increment();
        }
    }
}

public class G1E2 {

    public static void main(String[] args) throws InterruptedException{

        final int N = Integer.parseInt(args[0]);
        final int I = Integer.parseInt(args[1]);

        Counter c = new Counter();                  //Neste exercicio criamos o Counter no main para que todas as threads utilizem o mesmo counter
                                                    // Se cada Thread criasse o seu próprio Counter estes não iam partilhar a contagem

        Thread[] arrayT = new Thread[N];            // Criamos uma lista de Threads para conseguir manter as referencias de forma simples, cada posição
        for (int n = 0; n < N; n++){                //do array corresponde a uma thread
            arrayT[n] = new Incrementer(I, c);
            arrayT[n].start();                  // O start inicia a thread
        }
       
        for (int n = 0; n < N; n++){
            arrayT[n].join();                   // o join espera que as threads acabem
        }

        System.out.println(c.value());
        System.out.println("FIM");
    }
}

// javac A.java (para compilar)
// java A N I (para correr)