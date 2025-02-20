public class Bank {

    private static class Account {

        private int balance;

        Account(int balance) {
            this.balance = balance; 
        }

        int balance() { 
            return balance; 
        }

        boolean deposit(int value) {
            balance += value;
            return true;
        }

        boolean withdraw(int value) {
            if (value > balance)
                return false;
            balance -= value;
            return true;
        }
    }

    // Bank slots and vector of accounts
    private int slots;
    private Account[] av; 

    public Bank(int n) {
        slots=n;
        av=new Account[slots];
        for (int i=0; i<slots; i++) av[i]=new Account(0);
    }

    // Account balance
    public synchronized int balance(int id) {                       // Exercicio 2: Para aplicar exclusão mútua, basta acrescentar a keyword "synchronized"
        if (id < 0 || id >= slots)                                  //nos seus métodos "public" deste modo nenhuma destas operações pode ser efetuada por 
            return 0;                                               //2 threads distintas em simultaneo
        return av[id].balance();
    }

    // Deposit
    public synchronized boolean deposit(int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        return av[id].deposit(value);
    }

    // Withdraw; fails if no such account or insufficient balance
    public synchronized boolean withdraw(int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        return av[id].withdraw(value);
    }

    // ------------------------------------------------ Exercicio 3 -------------------------------------------------------------- //

    // Transfer

    // public boolean transfer(int id1, int id2, int amount){
    //     return withdraw(1, amount) && deposit(id2, amount);       // Esta versão pode resultar num bug em que o banco retira dinheiro de uma conta e não
    // }                                                             //consegue depositar na outra caso o identificador da segunda seja inválido
    
    // public boolean transfer(int id1, int id2, int amount){        // Esta versão corrige o problema dos id's da versão anterior, no entando o "BankTest2"
    //     if(id1 < 0 || id1 >= slots || id2 < 0 || id2 >= slots)    //vai nos mostrar que há momentos em que o saldo do banco sofre anomalias, o que não deveria
    //         return false;
    //     return withdraw(1, amount) && deposit(id2, amount);       //acontecer, uma vez que uma transferencia não altera o saldo total do banco

    // colocar o método "transfer" como synchronized poderia resolver o problema com as anomalias no saldo total do banco, no entanto ia aumentar 
    //demasiado a inefeciencia do programa, uma vez que apesar de termos N threads a efetuar transferencias, estas não poderiam ser efetuadas em 
    //simultaneo

    public synchronized boolean transfer(int id1, int id2, int amount){        
        if(id1 < 0 || id1 >= slots || id2 < 0 || id2 >= slots)    
            return false;
        return withdraw(1, amount) && deposit(id2, amount);
    }

    // ------------------------------------------------ Exercicio 4 -------------------------------------------------------------- //

    // public boolean transfer(int id1, int id2, int amount){        
    //     if(id1 < 0 || id1 >= slots || id2 < 0 || id2 >= slots)
    //         return false;
    //     Account af = av[id1];
    //     Account at = av[id2];

    //     synchronized (af) {                                      // Esta opção em que criamos 2 segmentos "synchronized" pode levar a um deadlock
    //         synchronized (at) {                                  //logo não é uma opção viável no caso de:
    //             if(!af.withdraw(amount))                         // Account 5 -> Account 7    e simultaneamente    Account 7 -> Account 5
    //                 return false;                                // Cada thread pode estar a bloquear a outra então ambas vão estar à espera indeterminadamente
    //             return at.deposit(amount);
    //         }
    //     }
    // }

    // TotalBalance
    public synchronized int totalBalance(){
        int sum = 0;
        for (Account c: av)
            sum += c.balance();
        return sum;
    }
}
