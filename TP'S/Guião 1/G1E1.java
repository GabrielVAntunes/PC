public class G1E1 {
    
    public static void main(String[] args) throws InterruptedException{
        
        final int N = Integer.parseInt(args[0]); // número de threads
        final int I = Integer.parseInt(args[1]); // até que número cada thread vai imprimir
        
        // Criamos um array de threads
        Thread[] listaT = new Thread[N];
        
        // Este ciclo inicializa as threads
        for (int i = 0; i < N; i++){
            listaT[i] = new Printer(I);
            listaT[i].start();
        }

        // Este ciclo aguarda que as threads acabem e se juntem (fazemos isto para que o programa depois siga normalmente,
        //neste caso não é muito relevante porque depois disso não há mais código)
        for (int n = 0; n < N; n++){
            listaT[n].join();                   // o join espera que as threads acabem
        }
    }
}

class Printer extends Thread {

    // até que número cada Thread vai imprimir
    final int I;

    Printer(int I) {
        this.I = I;
    }

    // método run (obrigatório), quando a thread for ativada irá executar esta função
    public void run(){
        for (int i = 0; i < I; i++){
            System.out.println(i);
        }
    }
}
