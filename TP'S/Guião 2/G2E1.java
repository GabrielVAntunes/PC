// Este exercício apenas tem o intuito de corrigir o exercício 2 do guião 1 de modo a que não haja erros derivados de condições de corrida

class Counter {
    private int c = 0;

    public synchronized void increment(){       // Acrescentar a keyword "synchronized" nestes métodos previne que diferentes threads acedam às 
        c+=1;                                   //zonas de secção crítica, ou seja, garante exclusão mútua
    }                                           // Assim já não se vai observar os erros gerados pelas race conditions 
                                                //que se podiam observar na versão do guião anterior
    
    public synchronized int value() {           // A keyword "synchronized" utiliza o lock intrínseco ao objeto enquanto ele opera na secção crítica
        return c;                               //e retorna o "unlock" quando sai da mesma
    }
}

class Incrementer extends Thread {

    final int I;
    final Counter c;

    Incrementer (int I, Counter c) {                       
        this.I = I;                                        
        this.c = c;                        
    }

    public void run(){
        for (int i = 1; i <= I; i++){
            c.increment();
        }
    }
}

public class G2E1 {

    public static void main(String[] args) throws InterruptedException{

        final int N = Integer.parseInt(args[0]);
        final int I = Integer.parseInt(args[1]);

        Counter c = new Counter();                   

        Thread[] arrayT = new Thread[N];            
                                                    
        for (int n = 0; n < N; n++){                
            arrayT[n] = new Incrementer(I, c);
            arrayT[n].start();                  
        }
       
        for (int n = 0; n < N; n++){
            arrayT[n].join();                   
        }

        System.out.println(c.value());
        System.out.println("FIM");
    }
}
