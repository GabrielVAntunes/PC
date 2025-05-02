public class Bank {

    private static class Account {
        private int balance;
        Account(int balance) { this.balance = balance; }
        int balance() { return balance; }
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

    // --------------------------------------------------------- Ex 2 -------------------------------------------------------//

    // Neste exercício bastou utilizar a "keywrod" synchronized para que não seja possivel que 2 threads acedam a estes métodos 
    //em simultaneo. Neste exemplo as race conditions podiam provocar algumas anomalias, por exemplo

    // - numa conta com saldo = 50, 2 contas podiam em simultaneo tentar levantar 30, o que resultaria num saldo final de -10
    // Thread 1 confirma que saldo >= 30
    // Thread 2 confirma que saldo >= 30
    // Thread 1 levanta 30
    // Thread 2 levanta 30

    // Account balance
    synchronized public int balance(int id) {
        if (id < 0 || id >= slots)
            return 0;
        return av[id].balance();
    }

    // Deposit
    synchronized public boolean deposit(int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        return av[id].deposit(value);
    }

    // Withdraw; fails if no such account or insufficient balance
    synchronized public boolean withdraw(int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        return av[id].withdraw(value);
    }

    // ----------------------------------------------------------------------------------------------------------------------//

    // --------------------------------------------------------- Ex 3 -------------------------------------------------------//

    // Agora queremos implementar os métodos "transfer" que irá transferir o dinheiros de uma conta para outra e o método "totalBalance" 
    //que consulta o saldo total do banco

    public int totalBalance(){
        int sum = 0;
        for (Account c: av)
            sum += c.balance();
        return sum;
    }

    // synchronized boolean transfer(int idFrom, int idTo, int amount){   // Esta versão que seria a mais simples não está 100% correta pois pode ocorrer
    //     return withdraw(idFrom, amount) && deposit(idTo, amount);      //um bug em que é levantado dinheiro de uma conta mas a segunda tem um id
    // }                                                                  //inválido (logo é impossivel depositar nessa segunda conta) apesar da função
                                                                          //retornar falso, foi levantado dinheiro da primeira e não foi depositado na segunda
    
    public boolean validateId (int id){
        return id > 0 && id <= slots;
    }

    // Para tentar corrigir esta versão vamos verificar os id's antes de efetuar qualquer operação
    // public boolean transfer(int idFrom, int idTo, int amount){      // Ao utilizar o BankTest tudo aparenta funcionar bem, o resultado final é o esperado
    //     if (!validateId(idFrom) || !validateId(idTo))               //no fim de todas as transferências o valor total no banco continua o mesmo que no inicio
    //         return false;                                           // No entanto ao utilizar o BankTest2 que contém um observador que vai verificando o saldo
    //     return withdraw(idFrom, amount) && deposit(idTo, amount);   //do banco, durante a execução das transferẽncias haverá momentos em que o saldo total do banco
    // }                                                               // é inconstante

    // public synchronized boolean transfer(int idFrom, int idTo, int amount){     // Desta vez colocar a keyword "synchronized" no método não resolve o       
    //     if (!validateId(idFrom) || !validateId(idTo))                           //problema uma vez que o Observador observa em tempo real e ainda que só seja
    //         return false;                                                       //possível realizar uma transferencia de cada vez, caso o observador verifique
    //     return withdraw(idFrom, amount) && deposit(idTo, amount);               //o saldo total do banco durante uma transferência iremos verificar na mesma uma anomalia
    //}   

    // Mesmo que resultasse esta não seria uma solução adequada ao problema uma vez que não nos iria servir de nada ter muitas threads se nenhuma pudesse
    //operar em simultâneo com as restantes

    // ----------------------------------------------------------------------------------------------------------------------//

    // --------------------------------------------------------- Ex 4 -------------------------------------------------------//

    // Para tentar resolver o problema anterior em vez de tentar utilizar concorrência ao nível dos métodos, vamos tentar usar blocos 
    //de sincronização ao nível dos objetos

    // public boolean transfer(int idFrom, int idTo, int amount){   // Esta versão faz isso mesmo mas também apresenta um pequeno bug numa
    //     if (!validateId(idFrom) || !validateId(idTo))            //no caso de 2 threads estarem a fazer uma transferẽncia pode se dar um deadlock           
    //         return false;                                        //da seguinte forma
    //     Account aFrom = av[idFrom];
    //     Account aTo = av[idTo];                                  // Thread 1 - Vai fazer a transferência 7 -> 5
    //                                                              // Thread 2 - Vai fazer a transferência 5 -> 7
    //     synchronized (aFrom) {                                   
    //         synchronized (aTo) {                                 // Thread 1: Adquire lock da conta 7
    //             if (!aFrom.withdraw(amount))                     // Thread 2: Adquire lock da conta 5
    //                 return false;                                // Thread 1: Fica à espera que a conta 5 seja libertada
    //             return aTo.deposit(amount);                      // Thread 2: Fica à espera que a conta 7 seja liberdade 
    //         }                                                    // Como ambas as threads vão ficar infinitamente à espera uma da outra
    //     }                                                        //dá se um deadLock.
    // }

    // Esta versão bloqueia os objetos por ordem crescente de Id, o que evita o deadlock descrito na versão anterior, apesar de tudo nesta versão 
    //ainda ocorrem as tais anomalias, mas com as soluções que temos até este guião é bastante complicado resolver a não ser que bloqueassemos todo 
    //o banco mas isso seria bastante ineficiente.
    public boolean transfer(int idFrom, int idTo, int amount){   
        if (!validateId(idFrom) || !validateId(idTo))               
            return false;                                       
        Account aFrom = av[idFrom];
        Account aTo = av[idTo];                                  
                                                                
        if (idFrom < idTo) {
            synchronized (aFrom) {                                   
                synchronized (aTo) {                                 
                    if (!aFrom.withdraw(amount))                     
                        return false;                                
                    return aTo.deposit(amount);                      
                }                                                   
            }   
        } else {
            synchronized (aTo) {                                   
                synchronized (aFrom) {                                 
                    if (!aFrom.withdraw(amount))                     
                        return false;                                
                    return aTo.deposit(amount);                      
                }                                                   
            }   
        }                                                    
    }

}                                                                         


