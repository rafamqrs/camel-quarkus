update customers
set
    nome = :#nome,
    email = :#email,
    endereco = :#endereco
where
    cpf = :#cpf

