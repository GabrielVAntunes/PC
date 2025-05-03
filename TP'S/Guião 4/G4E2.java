public class G4E2 {

    class Agreement {

        int N;
        int guessNr = 0;
        int counter = 0;
        int max = 0; // Assumindo que só se pode escolher inteiros positivos
        int maxAux;

        Agreement (int N) {
            this.N = N;
        }

        public synchronized int propose(int choice) throws InterruptedException {

            // Primeiro fixamos na Thread o número da tentativa em que estamos
            final int guessNr = this.guessNr;

            // guardamos como max o maior número entre o máximo atual e a nova guess
            max = Math.max(choice, max);
            counter++;      // contamos mais uma thread que netrou

            // Quando atingirmos N Threads vamos: 
            if (counter == N) {
                maxAux = max;       // Guardar o valor máximo na variável "maxAux"
                max = 0;            // Dar reset ao max para que outras rondas não sejam influenciadas
                counter = 0;        // Dar reset ao counter para começar a contar as Threads do inicio na próxima ronda
                this.guessNr++;     // Avançar o número da Ronda
                notifyAll();        // Ativar as Threads que estavam á espera
            } else {
                while (this.guessNr == guessNr) {wait();}  // Enquanto ainda não tiver sido atualizada uma nova ronda 
            }                                              //(a atual ainda não acabou) as threads esperam

            return maxAux;
        }
    }
}
