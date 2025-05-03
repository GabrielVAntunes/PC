import java.util.*;
import java.util.concurrent.locks.*;

// Versão Cooperativa

class WarehouseCoop {
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
   
    // Desta vez na versão Cooperativa, vamos tentar que uma encomenda esteja completa antes de efetivamnete retirar os produtos do armazém
    public void consume(Set<String> items) throws InterruptedException {
        lock.lock();
        try {
            Product[] a = new Product[items.size()];
            Product p;

            int i = 0;
            for (String s : items) {
                a[i++] = get(s);
            }                               // Criamos uma lista coms os produtos desejados

            while (true) {                  // Vamos repetir este ciclo infinitamente até a condição ativar o break

                p = Unavailable(a);         // Utilizamos a função auxiliar "Unavailable()" para que verifique se há algum produto numa lista que não tem
                                            //stock disponível, retornando null caso não ocorra para nenhum dos produtos
                if (p == null) {            // Quando o p é null quer dizer que todos os elementos da lista estão disponíveis, logo podemos sair do ciclo
                    break;
                } else {                    // Caso algum elemento não esteja disponível vamos notificar a Thread para que ela espere até esse produto
                    p.cond.await();         //novamente disponível e aí voltamos a testar se todos os produtos estão disponiveis 
                }
            }

            for (Product prod : a) {        // Uma vez fora do ciclo podemos retirar 1 de cada produto na lista
                prod.quantity--;
            }

        } finally {
            lock.unlock();
        }
    }

    // método auxiliar que retorna o primeiro elemento indisponível numa lista ou null caso todos estejam disponíveis
    private Product Unavailable(Product[] a){

        for (Product p : a){
            if (p.quantity <= 0) return p;
        }
        return null;
    }
}
