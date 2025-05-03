import java.util.*;
import java.util.concurrent.locks.*;

// Esta versão do código já corresponde ao proposto no exercício 3, da primeira vez que fiz já tive em consideração o que foi dado
//na prática então já minimizei os tempos de bloqueio o máximo possível (penso eu)
class Bank_G3E3 {

    private static class Account {
        private int balance;
        private Lock lock = new ReentrantLock();

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

    private Map<Integer, Account> map = new HashMap<Integer, Account>();
    private int nextId = 0;
    // Neste guião vamos utilizar locks de Biblioteca (explícitos)
    // Ao contrário de quando utilizamos um lock implicito com o "synchronized", os locks de biblioteca não têm de estar associados a um
    //método ou a um objeto. Podemos utilizar estes apenas em secções críticas dando nos uma maior flexibilidade para trabalhar no código
    private Lock lock = new ReentrantLock();

    // create account and return account id
    public int createAccount(int balance) {
        Account c = new Account(balance);
        int id;

        lock.lock();
        // Neste caso não seria mesmo necessário, mas por segurança usamos "try {...} finally {...}"
        //para garantir que caso ocorra alguma exceção o lock é libertado na mesma.
        try {                   // Utilizamos o lock do banco para bloquear esta secção onde são feitas alterações ao banco
            id = nextId;
            nextId += 1;
            map.put(id, c);
        } finally {             // Depois de todas as alterações terem sido feitas, libertamos o lock
            lock.unlock();
        }
        
        return id;
    }

    // close account and return balance, or 0 if no such account
    public int closeAccount(int id) {
        Account c;
        lock.lock();
        try {                       
            c = map.remove(id);
            // Neste caso, se tentarem remover uma entrada que não existe no hashMap c será igual a null e iria retornar logo, por isso usamos o finally 
            if (c == null)      //para garantir o desbloqueio do lock.
                return 0;
            c.lock.lock();  // Para dinamizar um bocado o acesso das threads, protegemos a conta especificamente, e libertamos a parte que modifica
        } finally {         //o banco para que outras threads já possam aceder
            lock.unlock();
        }
        try {
            return c.balance(); // Assim agora podemos consultar uma conta especifica livremente sem ter de restringir o acesso a todo o banco
        } finally {
            c.lock.unlock();
        }
    }

    // account balance; 0 if no such account
    public int balance(int id) {
        Account c;
        lock.lock();
        try {
            c = map.get(id);
            if (c == null)
                return 0;
            c.lock.lock();
        } finally {
            lock.unlock();
        } 
        try {
            return c.balance();
        } finally {
            c.lock.unlock();
        }
    }

    // deposit; fails if no such account
    public boolean deposit(int id, int value) {
        Account c;
        lock.lock();
        try {
            c = map.get(id);
            if (c == null)
                return false;
            c.lock.lock();
        } finally {
            lock.unlock();
        }
        try {
            return c.deposit(value);
        } finally {
            c.lock.unlock();
        }
    }

    // withdraw; fails if no such account or insufficient balance
    public boolean withdraw(int id, int value) {
        Account c;
        lock.lock();
        try {
            c = map.get(id);
            if (c == null)
                return false;
            c.lock.lock();
        } finally {
            lock.unlock();
        }
        try {
            return c.withdraw(value);
        } finally {
            c.lock.unlock();
        }
    }

    // transfer value between accounts;
    // fails if either account does not exist or insufficient balance

    // Este exemplo já é um pouco mais complexo!
    public boolean transfer(int from, int to, int value) {
        Account cfrom, cto;
        
        lock.lock();
        try {   // primeiro bloqueamos o acesso ao banco pelas outras threads
            cfrom = map.get(from);
            cto = map.get(to);
            if (cfrom == null || cto ==  null)
                return false;

                // Neste caso trata se de uma operação que envolve 2 contas, portanto vamos precisar de bloquear as 2 individualmente
            if (from < to) {            // para evitar deadlocks vamos bloquear as contas por ordem crescente de número de id  
                cfrom.lock.lock();
                cto.lock.lock();
            } else {
                cto.lock.lock();
                cfrom.lock.lock();
            }
        } finally {
            lock.unlock();
        }
        try {
            try{
                if (!cfrom.withdraw(value)) {
                    return false;
                }
            } finally {
                cfrom.lock.unlock();   // Depois de fazermos o levantamento da conta origem libertamos o respetivo lock o mais cedo possível
            }

            return cto.deposit(value);

        } finally {                     // no fim da transferência libertamos o lock da conta destino
            cto.lock.unlock();
        }
    }

    // sum of balances in set of accounts; 0 if some does not exist
    public int totalBalance(int[] ids) {

        // Para não alterar a lista original criamos um clone para trabalhar dentro desta chamada do método
        ids = ids.clone();
        Arrays.sort(ids);

        int total = 0;

        // Lista auxiliar para guardar as contas temporáriamente
        Account[] arrAcc = new Account[ids.length];

        lock.lock();
        try {
            // percorremos todas as contas e guardamos no Array para podermos manusea las independentemente
            for (int i = 0; i < ids.length; i++) {
                arrAcc[i] = map.get(ids[i]);
                if (arrAcc[i] == null) return 0;
            }

            // Damos lock a cada uma das contas que vamos mexer
            for (Account c : arrAcc) {
                c.lock.lock();
            }

        } finally {
            lock.unlock();
        }

        int i = 0;

        try {   // Agora vamos consultar o saldo de cada conta e á medida que consultamos o saldo desbloqueamos logo essa conta para que putras threads possam aceder
            while ( i < ids.length) {  
                    total += arrAcc[i].balance();
                    arrAcc[i].lock.unlock();
                    i++;                            // Usei um while em vez de um for, para garantir que este i apenas era incrementado caso todas as 
            }                                       //operações fossem realizadas com sucesso
        } finally {
            for (; i < ids.length; i++) {           // Ao utilizar o while com i++, posso no finally garantir que em caso de erro todas as contas restantes
                arrAcc[i].lock.unlock();            //por desbloquear são desbloqueadas
            }
        }
        return total;
    }

}

