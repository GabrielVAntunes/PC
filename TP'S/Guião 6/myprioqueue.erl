% Módulo myqueue2.erl
-module(myprioqueue).
-export([create/0, enqueue/3, dequeue/1, test/0]).

create() -> [].

% Desta vez vamos preenchendo a queue, de modo a que os valores com maior prioridade fiquem mais perto da cabeça da lista
enqueue([], Item, Prio) -> [{Item, Prio}];
enqueue([{H, HPrio} | T], Item, Prio) -> 
    if 
        Prio =< HPrio -> [{H, HPrio} | enqueue(T, Item, Prio)];
        true -> [{Item, Prio} | [{H, HPrio} | T]]
    end.

% Com esta estratégia o dequeue fica mais simples pois basta remover o valor à cabeça
dequeue([]) -> empty;
dequeue([{H, Hprio} | T]) -> {T, H}.

test() -> 
    Q0 = create(),
    Q1 = enqueue(Q0, q, 1),
    Q2 = enqueue(Q1, w, 1),
    Q3 = enqueue(Q2, e, 1),
    Q4 = enqueue(Q3, s, 5),
    Q5 = enqueue(Q4, x, 3),
    Q6 = enqueue(Q5, z, 5),
    
    io:format("Queue após 6 inserções: ~p~n", [Q6]),

    {Q7, V1} = dequeue(Q6),
    io:format("Queue após primeira remoção: ~p~n", [Q7]),
    {Q8, V2} = dequeue(Q7),
    io:format("Queue após 2 remoção: ~p~n", [Q8]),
    {Q9, V3} = dequeue(Q8),
    io:format("Queue após 3 remoção: ~p~n", [Q9]),
    {Q10, V4} = dequeue(Q9),
    io:format("Queue após 4 remoção: ~p~n", [Q10]),
    {Q11, V5} = dequeue(Q10),
    io:format("Queue após 5 remoção: ~p~n", [Q11]),

    io:format("Valores removidos: ~p, ~p, ~p, ~p, ~p~n", [V1, V2, V3, V4, V5]),
    io:format("Queue final: ~p~n", [Q11]),

    empty = dequeue(Q11),
    ok.
