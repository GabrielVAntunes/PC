import java.util.*;
import java.util.concurrent.locks.*;

// Versão Egoísta

class WarehouseEgo {
    private Map<String, Product> map =  new HashMap<String, Product>();
    private Lock lock = new ReentrantLock();            // Acrescentamos o Lock de biblioteca que vamos usar para o armazém

    private class Product { 
        int quantity = 0; 
        Condition cond = lock.newCondition();           // Inicializamos o Lock
    }

    private Product get(String item) {
        Product p = map.get(item);
        if (p != null) return p;
        p = new Product();
        map.put(item, p);
        return p;
    }

    public void supply(String item, int quantity) throws InterruptedException{  // Nesta funcão foi simplesmente aplicar o lock de biblioteca
        lock.lock();
        try {
            Product p = get(item);
            p.quantity += quantity;
            p.cond.signalAll();         // Diferentemente de como fizemos nos Guiões anteriores, neste vamos utilizar condições, neste caso
        } finally {                     //vamos reativar as Threads que estivessem bloqueadas segundo esta condição (há x produto disponível)
            lock.unlock();
        }
    }

    public void consume(Set<String> items) throws InterruptedException{
        lock.lock();
        try {
            for (String s : items){                 // Esta versão é considerada Egoísta porque se foca em cada cliente igualmente, mal um produto esteja
                Product product = get(s);           //disponível um cliente que estivesse à espera pode logo recolhê-lo mesmo que deixe o resto da encomenda incompleta
                while (product.quantity <= 0) {
                    product.cond.await();          // Neste caso vamos associar à Thread uma condição que é não haver um produto, assim mesmo com um unico lock
                }                                  //conseguimos mais flexibilidade sobre quais Threads ativar em determinadas situações
                product.quantity--;
            }
        } finally {
                lock.unlock();
        }
    }

}
