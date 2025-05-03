public class G4E1 {
 
    class Barrier {

        final int N;
        private int counterA = 0;
        private int counterRe = 0;
        private int counterRe2 = 0;

    
        Barrier (int N) {
            this.N = N;
        }

        // Versão simples da barreira que só pode ser utilizada uma vez, espera para as primeiras N threads, depois o código correrá sempre normalmente
        public synchronized void await() throws InterruptedException {

            // Contamos as threads que entram
            counterA++;

            // Enquanto não tivermos satisfeito o número de threads necessárias fazemos com que as threads aguardem
            while (counterA < N) {
                wait();
            }

            // Quando chegar a ultima das primeiras N-Threads ela notificará todas as que estavam bloqueadas prosseguindo assim o resto do código
            if (counterA == N) {
                notifyAll();
            }
        }

        private boolean flagOpen = true;

        // Versão da segunda alínea que deve permitir que a Barreira seja reutilizável
        public synchronized void reWait() throws InterruptedException {

            // Nesta versão utilizaremos uma flag que nos vai indicar se as atuais N-Vagas estão ou não preenchidas, caso estejam preenchidas
            //as novas threads vão aguardar aqui até que seja possível entrar na barreira
            while (!flagOpen) {
                wait();
            }

            // NOTA*: É importante saber que não podemos usar if's para fazer com que a threads esperem devido aos "spurious wake ups"
            //este fenómeno faz como que as threads acordem espontaneamente, ou seja sem terem sido notificados, caso isso aconteça
            //devemos usar um while para que a condição seja novamente testada.

            // No caso do ciclo anterior evitar:
            // if (!flagOpen) {
            //     wait();
            // }

            // Contamos o número de Threads
            counterRe++;

            // Se a barreira estiver aberta e ainda não tivermos atingido as N-Threads necessárias, as threads que forem chegando entram aqui
            while (counterRe != N && flagOpen) {
                wait();
            }

            // Quando chegar a N-ésima Thread e se a barreira estiver aberta, vamos fechar a barreira e ativar todas as Threads
            if (counterRe == N && flagOpen) {
                flagOpen = false;
                notifyAll();
            }

            // Decrementamos o contador de Threads
            counterRe--;

            // Quando o contador estiver em zeros, deu reset, podemos voltar a abrir a barreira e acordar as threads que estavam à espera
            if (counterRe == 0) {
                flagOpen = true;
                notifyAll();
            }
            // Esta versão tem um pequeno erro, que acontece quando mais de n Threads das que estavam à espera á entrada são ativadas ao mesmo tempo
        }   //de modo que algumas threads passarão pela secção sem fazer nada.

        private int grupoNr = 0;

        // Versão do progefessor (muito clean)
        public synchronized void reWait2() throws InterruptedException {

            // Desta vez vamos associar cada N elementos a um grupo especifico, avançando o grupo atual sempre que N Threads se juntam
            final int grupoNr = this.grupoNr;

            counterRe2 ++;

            if (counterRe2 == N) {
                counterRe2 = 0;     // Quando atingimos as N threads necessárias damos reset ao contador
                this.grupoNr += 1;  // Avançamos o grupo
                notifyAll();        // Acordamos todas as Threads
            } else {
                while (this.grupoNr == grupoNr) {wait();}   // Caso ainda não tenhamos preenchido o grupo, esperamos até que o mesmo seja preenchido
            }
        }
    }

}
