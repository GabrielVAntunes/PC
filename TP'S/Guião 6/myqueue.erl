% Módulo myqueue.erl
-module(myqueue).
-export([create/0, enqueue/2, dequeue/1, test/0]).

% Função que apenas cria uma queue vazia
create() -> [].

% A função "enqueue" vai inserir um novo elemento numa queue já existente
enqueue([], Item) -> [Item];
enqueue([H | T], Item) -> [H | enqueue(T, Item)].

% A função "dequeue" vai remover um elemento de uma queue, caso a queue esteja vazia vai retornar "empty"
dequeue([]) -> empty;
dequeue([H | T]) -> {T , H}.

% Função de teste apenas para verificar se as operações estão corretas
test() -> 
    Q0 = create(),
    Q1 = enqueue(Q0, 1),
    Q2 = enqueue(Q1, 2),
    Q3 = enqueue(Q2, 3),
    Q4 = enqueue(Q3, 4),
    Q5 = enqueue(Q4, 5),
    
    io:format("Queue após 5 inserções: ~p~n", [Q5]),

    {Q7, V1} = dequeue(Q5),
    {Q8, V2} = dequeue(Q7),
    {Q9, V3} = dequeue(Q8),
    {Q10, V4} = dequeue(Q9),
    {Q11, V5} = dequeue(Q10),

    io:format("Valores removidos: ~p, ~p, ~p, ~p, ~p~n", [V1, V2, V3, V4, V5]),
    io:format("Queue final: ~p~n", [Q11]),

    empty = dequeue(Q11),
    ok.