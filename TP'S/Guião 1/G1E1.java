class Printer extends Thread {

    final int I;

    Printer (int I) {                       // de modo a evitar que o I seja uma variável global e que seja passada por parametro no método run
        this.I = I;                         //passamo-la como variável local e registamos o valor utilizando um construtor 
    }

    public void run(){
        for (int i = 1; i <= I; i++){
            System.out.println(i);
        }
    }
}

public class G1E1 {

    public static void main(String[] args) throws InterruptedException{

        final int N = Integer.parseInt(args[0]);
        final int I = Integer.parseInt(args[1]);


        Thread[] arrayT = new Thread[N];            // Criamos uma lista de Threads para conseguir manter as referencias de forma simples, cada posição
        for (int n = 0; n < N; n++){                //do array corresponde a uma thread
            arrayT[n] = new Printer(I);
            arrayT[n].start();                  // O start inicia a thread
        }
       
        for (int n = 0; n < N; n++){
            arrayT[n].join();                   // o join espera que as threads acabem
        }

        System.out.println("FIM");
    }
}

// javac A.java (para compilar)
// java A <N> <I> (para correr)